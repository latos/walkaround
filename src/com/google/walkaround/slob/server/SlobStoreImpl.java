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

import com.google.appengine.api.memcache.Expiration;
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
import com.google.walkaround.slob.shared.SlobModel.ReadableSlob;
import com.google.walkaround.slob.shared.StateAndVersion;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.appengine.MemcacheTable;
import com.google.walkaround.util.server.appengine.MemcacheTable.IdentifiableValue;
import com.google.walkaround.util.shared.RandomBase64Generator;

import org.waveprotocol.wave.model.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Datastore (and other app enginy things) backed implementation.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class SlobStoreImpl implements SlobStore {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(SlobStoreImpl.class.getName());

  // Split out because InternalPostCommitAction can't depend on the full
  // SlobStoreImpl, which needs an AccessChecker and thus a UserContext, which
  // task queue tasks don't have.
  static class Cache {
    private final MemcacheTable<SlobId, Long> currentVersions;

    @Inject Cache(MemcacheTable.Factory memcacheFactory,
        @SlobRootEntityKind String rootEntityKind) {
      this.currentVersions = memcacheFactory.create(MEMCACHE_TAG_PREFIX + rootEntityKind);
    }
  }

  static class InternalPostCommitAction implements PostCommitAction {
    @Inject Cache cache;

    @Override public void unreliableImmediatePostCommit(SlobId slobId, long resultingVersion,
        ReadableSlob resultingState) {
      // We have to put null rather than deleting since what we do here must
      // interfere with a concurrent getIdentifiable/putIfUntouched sequence,
      // and MemcacheService's Javadoc does not specify that putIfUntouched will
      // abort if the value was absent during the lookup and delete has been
      // called between the lookup and putIfUntouched.
      cache.currentVersions.put(slobId, null);
    }

    @Override public void reliableDelayedPostCommit(SlobId slobId) {
      cache.currentVersions.put(slobId, null);
    }
  }

  private static final String MEMCACHE_TAG_PREFIX = "slobversion-";
  private static final int VERSION_NUMBER_CACHE_EXPIRATION_MILLIS = 24 * 60 * 60 * 1000;

  private final CheckedDatastore datastore;
  private final MutationLogFactory mutationLogFactory;
  private final SlobMessageRouter messageRouter;
  private final AffinityMutationProcessor defaultProcessor;
  private final AccessChecker accessChecker;
  private final PostMutateHook postMutateHook;
  private final String rootEntityKind;
  private final Cache cache;
  private final LocalMutationProcessor localProcessor;

  @Inject
  public SlobStoreImpl(CheckedDatastore datastore,
      MutationLogFactory mutationLogFactory,
      SlobMessageRouter messageRouter,
      Random random, RandomBase64Generator random64,
      AffinityMutationProcessor defaultProcessor,
      LocalMutationProcessor localProcessor,
      AccessChecker accessChecker,
      PostMutateHook postMutateHook,
      @SlobRootEntityKind String rootEntityKind,
      Cache cache) {
    this.datastore = datastore;
    this.mutationLogFactory = mutationLogFactory;
    this.messageRouter = messageRouter;
    this.defaultProcessor = defaultProcessor;
    this.localProcessor = localProcessor;
    this.accessChecker = accessChecker;
    this.postMutateHook = postMutateHook;
    this.rootEntityKind = rootEntityKind;
    this.cache = cache;
  }

  @Override
  public Pair<ConnectResult, String> connect(SlobId slobId, ClientId clientId)
      throws SlobNotFoundException, IOException, AccessDeniedException {
    return connectOrReconnect(slobId, clientId, true);
  }

  @Override
  public ConnectResult reconnect(SlobId slobId, ClientId clientId)
      throws SlobNotFoundException, IOException, AccessDeniedException {
    return connectOrReconnect(slobId, clientId, false).getFirst();
  }

  private Pair<ConnectResult, String> connectOrReconnect(
      SlobId slobId, ClientId clientId, boolean withSnapshot)
      throws SlobNotFoundException, IOException, AccessDeniedException {
    accessChecker.checkCanRead(slobId);
    String snapshot;
    long version;
    IdentifiableValue<Long> cachedVersion = cache.currentVersions.getIdentifiable(slobId);
    if (!withSnapshot && cachedVersion != null && cachedVersion.getValue() != null) {
      version = cachedVersion.getValue();
      log.info("Version number from cache: " + version);
      snapshot = null;
    } else {
      try {
        CheckedTransaction tx = datastore.beginTransaction();
        try {
          MutationLog mutationLog = mutationLogFactory.create(tx, slobId);
          if (withSnapshot) {
            StateAndVersion x = mutationLog.reconstruct(null);
            version = x.getVersion();
            snapshot = x.getState().snapshot();
          } else {
            version = mutationLog.getVersion();
            snapshot = null;
          }
          if (version == 0) {
            throw new SlobNotFoundException(
                "Slob " + slobId + " (" + rootEntityKind + ") not found");
          }
        } finally {
          tx.rollback();
        }
      } catch (PermanentFailure e) {
        throw new IOException(e);
      } catch (RetryableFailure e) {
        throw new IOException(e);
      }
      cache.currentVersions.putIfUntouched(slobId, cachedVersion, version,
          // Since the cache is invalidated by a task queue task, we only need this expiration to
          // protect from admins deleting tasks and similar situations.
          Expiration.byDeltaMillis(VERSION_NUMBER_CACHE_EXPIRATION_MILLIS));
    }

    String channelToken;
    if (clientId != null) {
      try {
        channelToken = messageRouter.connectListener(slobId, clientId);
      } catch (TooManyListenersException e) {
        channelToken = null;
      }
    } else {
      channelToken = null;
    }
    return Pair.of(new ConnectResult(channelToken, version), snapshot);
  }

  @Override
  public String loadAtVersion(SlobId slobId, @Nullable Long version)
      throws IOException, AccessDeniedException {
    accessChecker.checkCanRead(slobId);
    try {
      CheckedTransaction tx = datastore.beginTransaction();
      try {
        MutationLog l = mutationLogFactory.create(tx, slobId);
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
  public HistoryResult loadHistory(SlobId slobId, long startVersion, @Nullable Long endVersion)
      throws SlobNotFoundException, IOException, AccessDeniedException {
    accessChecker.checkCanRead(slobId);
    IdentifiableValue<Long> cachedVersion = cache.currentVersions.getIdentifiable(slobId);
    log.info("loadHistory(" + slobId + ", " + startVersion + " - " + endVersion + "); cached: "
        + cachedVersion);
    if (cachedVersion != null && cachedVersion.getValue() != null
        && startVersion >= cachedVersion.getValue()
        && endVersion == null) {
      return new HistoryResult(ImmutableList.<ChangeData<String>>of(), false);
    }
    final int MAX_MILLIS = 3 * 1000;
    try {
      CheckedTransaction tx = datastore.beginTransaction();
      try {
        // TODO(ohler): put current version into cache
        DeltaIterator result = mutationLogFactory.create(tx, slobId).forwardHistory(
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
    // Sending the broadcast message is not critical, so run the
    // postMutateHook first.
    postMutateHook.run(objectId, result);
    if (response.getBroadcastData() != null) {
      messageRouter.publishMessages(objectId, response.getBroadcastData());
    }
    return result;
  }

  @Override
  public void newObject(final SlobId slobId, final String metadata,
      final List<ChangeData<String>> initialHistory)
      throws SlobAlreadyExistsException, IOException, AccessDeniedException {
    Preconditions.checkNotNull(slobId, "Null slobId");
    Preconditions.checkNotNull(metadata, "Null metadata");
    Preconditions.checkNotNull(initialHistory, "Null initialHistory");
    accessChecker.checkCanCreate(slobId);
    try {
      boolean alreadyExists = new RetryHelper().run(new RetryHelper.Body<Boolean>() {
        @Override public Boolean run() throws RetryableFailure, PermanentFailure {
          CheckedTransaction tx = datastore.beginTransaction();
          try {
            MutationLog l = mutationLogFactory.create(tx, slobId);

            String existingMetadata = l.getMetadata();
            if (existingMetadata != null) {
              log.info("Slob " + slobId + " already exists: found metadata: "
                  + existingMetadata);
              return true;
            }
            // Check for the existence of deltas as well because legacy conv
            // wavelets have no metadata entity.
            long version = l.getVersion();
            if (version != 0) {
              log.info("Slob " + slobId + " already exists at version " + version);
              return true;
            }

            MutationLog.Appender appender = l.prepareAppender().getAppender();
            try {
              appender.appendAll(initialHistory);
            } catch (ChangeRejected e) {
              throw new IllegalArgumentException("Invalid initial " + initialHistory);
            }
            l.putMetadata(metadata);
            localProcessor.completeTransaction(tx, slobId, appender);
            return false;
          } finally {
            tx.close();
          }
        }
      });
      if (alreadyExists) {
        throw new SlobAlreadyExistsException(slobId + " already exists");
      }
    } catch (PermanentFailure e) {
      throw new IOException(e);
    }
  }

}
