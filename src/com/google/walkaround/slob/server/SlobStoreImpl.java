/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.walkaround.slob.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.walkaround.proto.ServerMutateRequest;
import com.google.walkaround.proto.ServerMutateResponse;
import com.google.walkaround.slob.server.MutationLog.DeltaIterator;
import com.google.walkaround.slob.server.MutationLog.MutationLogFactory;
import com.google.walkaround.slob.server.SlobMessageRouter.TooManyListenersException;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ChangeRejected;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.StateAndVersion;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.shared.RandomBase64Generator;

import org.waveprotocol.wave.model.util.Pair;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Datastore (and other app enginy things) backed implementation.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class SlobStoreImpl implements SlobStore {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(SlobStoreImpl.class.getName());

  private final CheckedDatastore datastore;
  private final MutationLogFactory mutationLogFactory;
  private final SlobMessageRouter messageRouter;
  private final AffinityMutationProcessor defaultProcessor;
  private final AccessChecker accessChecker;
  private final PreCommitHook preCommitHook;

  @Inject
  public SlobStoreImpl(CheckedDatastore datastore,
      MutationLogFactory mutationLogFactory,
      SlobMessageRouter messageRouter,
      Random random, RandomBase64Generator random64,
      AffinityMutationProcessor defaultProcessor,
      LocalMutationProcessor localProcessor,
      AccessChecker accessChecker,
      PreCommitHook preCommitHook) {
    this.datastore = datastore;
    this.mutationLogFactory = mutationLogFactory;
    this.messageRouter = messageRouter;
    this.defaultProcessor = defaultProcessor;
    this.accessChecker = accessChecker;
    this.preCommitHook = preCommitHook;
  }

  @Override
  public Pair<ConnectResult, String> connect(SlobId objectId, ClientId clientId)
      throws SlobNotFoundException, IOException, AccessDeniedException {
    return connectOrReconnect(objectId, clientId, true);
  }

  @Override
  public ConnectResult reconnect(SlobId objectId, ClientId clientId)
      throws SlobNotFoundException, IOException, AccessDeniedException {
    return connectOrReconnect(objectId, clientId, false).getFirst();
  }

  private Pair<ConnectResult, String> connectOrReconnect(
      SlobId objectId, ClientId clientId, boolean withSnapshot)
      throws SlobNotFoundException, IOException, AccessDeniedException {
    accessChecker.checkCanRead(objectId);
    String snapshot;
    long version;
    try {
      CheckedTransaction tx = datastore.beginTransaction();
      try {
        MutationLog mutationLog = mutationLogFactory.create(tx, objectId);
        if (withSnapshot) {
          StateAndVersion x = mutationLog.reconstruct(null);
          version = x.getVersion();
          snapshot = x.getState().snapshot();
        } else {
          version = mutationLog.getVersion();
          snapshot = null;
        }
        if (version == 0) {
          throw new SlobNotFoundException("Object " + objectId + " not found");
        }
      } finally {
        tx.rollback();
      }
    } catch (PermanentFailure e) {
      throw new IOException(e);
    } catch (RetryableFailure e) {
      throw new IOException(e);
    }

    String channelToken;
    if (clientId != null) {
      try {
        channelToken = messageRouter.connectListener(objectId, clientId);
      } catch (TooManyListenersException e) {
        channelToken = null;
      }
    } else {
      channelToken = null;
    }
    return Pair.of(new ConnectResult(channelToken, version), snapshot);
  }

  @Override
  public String loadAtVersion(SlobId objectId, @Nullable Long version)
      throws IOException, AccessDeniedException {
    accessChecker.checkCanRead(objectId);
    try {
      CheckedTransaction tx = datastore.beginTransaction();
      try {
        MutationLog l = mutationLogFactory.create(tx, objectId);
        return l.reconstruct(version).getState().snapshot();
      } finally {
        tx.rollback();
      }
    } catch (PermanentFailure e) {
      throw new IOException(e);
    } catch (RetryableFailure e) {
      throw new IOException(e);
    }
  }

  @Override
  public HistoryResult loadHistory(SlobId objectId, long startVersion, @Nullable Long endVersion)
      throws SlobNotFoundException, IOException, AccessDeniedException {
    accessChecker.checkCanRead(objectId);
    log.info("loadHistory(" + objectId + ", " + startVersion + " - " + endVersion + ")");
    final int MAX_MILLIS = 3 * 1000;
    try {
      CheckedTransaction tx = datastore.beginTransaction();
      try {
        DeltaIterator result = mutationLogFactory.create(tx, objectId).forwardHistory(
            startVersion, endVersion);
        if (!result.hasNext()) {
          return new HistoryResult(ImmutableList.<ChangeData<String>>of(), false);
        }
        ImmutableList.Builder<ChangeData<String>> list = ImmutableList.builder();
        Stopwatch stopwatch = new Stopwatch().start();
        do {
          list.add(result.next());
        } while (result.hasNext() && stopwatch.elapsedMillis() < MAX_MILLIS);
        return new HistoryResult(list.build(), result.hasNext());
      } finally {
        tx.rollback();
      }
    } catch (PermanentFailure e) {
      throw new IOException(e);
    } catch (RetryableFailure e) {
      // TODO(danilatos): Retry?
      throw new IOException(e);
    }
  }

  @Override
  public MutateResult mutateObject(ServerMutateRequest req)
      // TODO(ohler): Actually throw SlobNotFoundException.
      throws SlobNotFoundException, IOException, AccessDeniedException {
    SlobId objectId = new SlobId(req.getSession().getObjectId());
    accessChecker.checkCanModify(objectId);
    Preconditions.checkArgument(req.getVersion() != 0,
        // NOTE(ohler): In Google Wave, there were security concerns around
        // creating objects by submitting deltas against version 0.  I'm not
        // sure Walkaround has the same problems, but let's disallow it anyway.
        "Can't create objects with mutateObject()");
    ServerMutateResponse response = defaultProcessor.mutateObject(req);
    MutateResult result = new MutateResult(response.getResultingVersion(), response.getIndexData());
    if (response.getBroadcastData() != null) {
      messageRouter.publishMessages(objectId, response.getBroadcastData());
    }
    return result;
  }

  @Override
  public void newObject(final SlobId objectId, final String metadata,
      final List<ChangeData<String>> initialHistory)
      throws SlobAlreadyExistsException, IOException, AccessDeniedException {
    Preconditions.checkNotNull(objectId, "Null objectId");
    Preconditions.checkNotNull(metadata, "Null metadata");
    Preconditions.checkNotNull(initialHistory, "Null initialHistory");
    accessChecker.checkCanCreate(objectId);
    try {
      boolean alreadyExists = new RetryHelper().run(new RetryHelper.Body<Boolean>() {
        @Override public Boolean run() throws RetryableFailure, PermanentFailure {
          CheckedTransaction tx = datastore.beginTransaction();
          try {
            MutationLog l = mutationLogFactory.create(tx, objectId);

            String existingMetadata = l.getMetadata();
            if (existingMetadata != null) {
              log.info("Object " + objectId + " already exists: found metadata: "
                  + existingMetadata);
              return true;
            }
            // Check for the existence of deltas as well because legacy conv
            // wavelets have no metadata entity.
            long version = l.getVersion();
            if (version != 0) {
              log.info("Object " + objectId + " already exists at version " + version);
              return true;
            }

            MutationLog.Appender appender = l.prepareAppender().getAppender();
            for (ChangeData<String> change : initialHistory) {
              try {
                appender.append(change);
              } catch (ChangeRejected e) {
                throw new IllegalArgumentException("Invalid initial history with change "
                    + change + " in history " + initialHistory);
              }
            }
            appender.flush();
            l.putMetadata(metadata);
            preCommitHook.run(tx, objectId, appender.getStagedVersion(), appender.getStagedState());
            tx.commit();
            return false;
          } finally {
            tx.close();
          }
        }
      });
      if (alreadyExists) {
        throw new SlobAlreadyExistsException(objectId + " already exists");
      }
    } catch (PermanentFailure e) {
      throw new IOException(e);
    }
  }

}
