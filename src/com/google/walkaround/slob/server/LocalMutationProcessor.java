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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.walkaround.proto.ServerMutateRequest;
import com.google.walkaround.proto.ServerMutateResponse;
import com.google.walkaround.proto.gson.ServerMutateResponseGsonImpl;
import com.google.walkaround.slob.server.MutationLog.DeltaIteratorProvider;
import com.google.walkaround.slob.server.MutationLog.MutationLogFactory;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ChangeRejected;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.SlobModel;
import com.google.walkaround.util.server.MonitoringVars;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.util.server.writebatch.BatchingUpdateProcessor;
import com.google.walkaround.util.server.writebatch.TransactionFactory;
import com.google.walkaround.util.server.writebatch.UpdateResult;
import com.google.walkaround.util.server.writebatch.UpdateTransaction;
import com.google.walkaround.util.shared.ConcatenatingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Accepts mutations on objects.
 *
 * Handles concurrent updates using transform, and write batching.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// Singleton so that mutations that come in in different threads reach the same
// instance which can then process them in one batch.
@Singleton
public class LocalMutationProcessor {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(LocalMutationProcessor.class.getName());

  // TODO(danilatos): Make these flags.
  /** The maximum number of ops the server is willing to transform updates against */
  private static final long MAX_TAIL_SIZE = 500;
  /** A soft limit on the number of ops the server is willing save in a single batch */
  private static final long MAX_BATCH_SIZE = 99;

  // TODO(danilatos): Give these inner classes a bit of a manicure, after
  // figuring out exactly what it is that bugs me about them.

  private static class Processor extends BatchingUpdateProcessor<Update, UpResult, Tx> {
    public Processor(TransactionFactory<UpResult, Tx> txFactory, RetryHelper retryHelper) {
      super(txFactory, retryHelper);
    }
  }

  private class Update {
    private final SlobId objectId;
    private final ClientId clientId;
    private final long version;
    // Payloads kept in addition to changes for the common non-transform case,
    // and to avoid redundant info in toString().
    private final ImmutableList<String> payloads;

    public Update(SlobId id, ClientId clientId, long version, List<String> payloads) {
      Preconditions.checkArgument(version >= 0, "Bad version %s", version);
      this.objectId = Preconditions.checkNotNull(id, "Null id");
      this.clientId = Preconditions.checkNotNull(clientId, "Null clientId");
      this.payloads = ImmutableList.copyOf(Preconditions.checkNotNull(payloads, "Null payloads"));
      this.version = version;
    }

    ImmutableList<ChangeData<String>> changes(List<String> payloads) {
      ImmutableList.Builder<ChangeData<String>> b = ImmutableList.builder();
      for (String payload : payloads) {
        b.add(new ChangeData<String>(clientId, payload));
      }
      return b.build();
    }

    @Override
    public String toString() {
      return toString(true);
    }

    String toString(boolean full) {
      return "Update(" + objectId + "," + clientId + "@" + version + "; "
          + (full
              ? payloads.toString()
              : (payloads.size() + " ops, " + System.identityHashCode(this)))
          + ")";
    }
  }

  /**
   * TODO(danilatos): There is some confusion between this class and
   * MutateResult, they are very similar. Fix this.
   */
  private static class UpResult implements UpdateResult {
    final long resultingRevision;
    final Exception exception;

    JSONArray broadcastData = null;
    String indexData = null;

    public UpResult(long resultingRevision, Exception exception) {
      this.resultingRevision = resultingRevision;
      this.exception = exception;
    }

    public UpResult(long resultingRevision, JSONArray broadcastData, String indexData) {
      this.resultingRevision = resultingRevision;
      this.exception = null;
      this.broadcastData = broadcastData;
      this.indexData = indexData;
    }

    @Override
    public boolean isRejected() {
      return exception != null;
    }

    public long getResultingRevision() {
      return resultingRevision;
    }

    public JSONArray getBroadcastData() {
      return broadcastData;
    }

    public String getIndexData() {
      return indexData;
    }
  }

  /**
   * A subsequence of the delta history that can be extended in both directions.
   */
  private class TransformDeltaCache {
    private final DeltaIteratorProvider reverseDeltaIterator;
    // Too bad ArrayDeque does not expose get(int) or perhaps even an
    // unmodifiable subList(); we could use that rather than doing all this.
    private final List<ChangeData<String>> onDiskDeltasReverse = Lists.newArrayList();
    /** Deltas added during this transaction. */
    private final List<ChangeData<String>> newDeltas = Lists.newArrayList();
    /**
     * A view over the reverse and forward deltas for conveniently allowing
     * uniform access and traversal.
     */
    private final List<ChangeData<String>> deltas = ConcatenatingList.of(
        Lists.reverse(onDiskDeltasReverse), newDeltas);
    private final long onDiskVersion;

    /** @param reverseTailDeltas must start at onDiskVersion (and then go back). */
    TransformDeltaCache(long onDiskVersion,
        List<ChangeData<String>> reverseTailDeltas,
        DeltaIteratorProvider reverseDeltaIterator) {
      this.onDiskVersion = onDiskVersion;
      onDiskDeltasReverse.addAll(reverseTailDeltas);
      this.reverseDeltaIterator = reverseDeltaIterator;
    }

    private long minVersion() {
      return onDiskVersion - onDiskDeltasReverse.size();
    }

    public List<ChangeData<String>> getNewDeltas() {
      return Collections.unmodifiableList(newDeltas);
    }

    public void append(ChangeData<String> delta) {
      newDeltas.add(delta);
    }

    private void ensureDeltasLoadedFrom(long version) throws PermanentFailure, RetryableFailure {
      while (version < minVersion()) {
        onDiskDeltasReverse.add(reverseDeltaIterator.get().next());
      }
    }

    public List<ChangeData<String>> suffix(long fromVersion)
        throws PermanentFailure, RetryableFailure {
      ensureDeltasLoadedFrom(fromVersion);
      return deltas.subList(Ints.checkedCast(fromVersion - minVersion()), deltas.size());
    }
 }

  private class Tx implements UpdateTransaction<Update, UpResult> {
    private final SlobId objectId;
    private final CheckedTransaction tx;
    private final MutationLog.Appender appender;
    private final TransformDeltaCache deltaCache;
    private final long onDiskVersion;
    /**
     * The most recent result. We choose it to be the "distinguished" request
     * that carries extra info. See MutateResult.
     */
    private UpResult lastResult = null;

    Tx(SlobId objectId, CheckedTransaction tx) throws PermanentFailure, RetryableFailure {
      this.objectId = objectId;
      this.tx = tx;
      MutationLog mutationLog = mutationLogFactory.create(tx, objectId);
      MutationLog.AppenderAndCachedDeltas prepared = mutationLog.prepareAppender();
      appender = prepared.getAppender();
      onDiskVersion = appender.getStagedVersion();
      deltaCache = new TransformDeltaCache(onDiskVersion,
          prepared.getReverseDeltasRead(),
          prepared.getReverseDeltaIteratorProvider());
    }

    @Override
    public UpResult processUpdate(Update update)
        throws BatchTooLargeException,
        // TODO(danilatos): also throw BatchTooLargeException for updates that both
        // 1) were not in the waiting items when the transaction started.
        // 2) end up requiring an actual rpc to the datastore to fetch more data.
        // But also: can use memcache to fetch more data.
        RetryableFailure, PermanentFailure {
      log.info("processUpdate " + update.toString(false));
      Preconditions.checkArgument(objectId.equals(update.objectId),
          "Object id %s does not match update %s", objectId, update);

      // TODO(ohler): Randomly commit earlier to avoid deterministically running
      // into transaction size or time limit; we estimate the size, but not
      // accurately enough to rely on.
      if (appender.getStagedVersion() - onDiskVersion >= MAX_BATCH_SIZE) {
        throw new BatchTooLargeException("Batch max of " + MAX_BATCH_SIZE + " ops exceeded");
      }

      if (update.version > onDiskVersion) {
        return logRejection(new UpResult(-1, new IllegalArgumentException(
            "Update version " + update.version + " greater than " + onDiskVersion)));
      }

      long difference = appender.getStagedVersion() - update.version;
      if (difference > MAX_TAIL_SIZE) {
        log.info("Update too far in the past, out of date by " + difference);
        // TODO(danilatos): Force client to retrieve history through other
        // means, do the transform itself and then retry.
        log.warning("TODO: Reject update too far in the past");
        monitoring.incrementCounter("submitdelta-update-too-far-in-the-past");
      }

      log.info("Getting suffix..."); // Log on either side to time possible RPC.
      List<ChangeData<String>> concurrent = deltaCache.suffix(update.version);
      log.info("Got suffix of size " + concurrent.size());

      // TODO(danilatos): Add op-serializing/deserializing methods to the model
      // code, to avoid unnecessary deserialization when we have nothing to
      // transform, and then eliminate this if-statement (just do the first
      // branch unconditionally).
      ImmutableList<ChangeData<String>> transformedChanges;
      if (!concurrent.isEmpty()) {
        log.info("processUpdate: transforming " + update.payloads.size() + " client changes"
            + " against " + concurrent.size() + " concurrent changes");
        try {
          transformedChanges = update.changes(model.transform(
              update.changes(update.payloads), Collections.unmodifiableList(concurrent)));
        } catch (ChangeRejected e) {
          return logRejection(new UpResult(-1, e));
        }
      } else {
        transformedChanges = update.changes(update.payloads);
        log.info("processUpdate: not transforming");
      }

      // Stage payloads for writing.
      for (ChangeData<String> change : transformedChanges) {
        try {
          appender.append(change);
          deltaCache.append(change);
        } catch (ChangeRejected e) {
          return logRejection(new UpResult(-1, e));
        }
      }

      log.info("Ops successfully appended (staged for writing)");
      return lastResult = new UpResult(appender.getStagedVersion(), null);
    }

    private UpResult logRejection(UpResult r) {
      log.log(Level.WARNING, "Update rejected", r);
      return r;
    }

    // TODO(danilatos): Update the update objects with transformed operations to
    // avoid re-transforming what we've already transformed in case of a retry,
    // once a thorough unit test framework for this code has been set up.
    @Override
    public void commit() throws RetryableFailure, PermanentFailure {
      if (!appender.hasNewDeltas()) {
        log.info("Nothing to commit?");
        tx.rollback();
        return;
      }
      completeTransaction(tx, objectId, appender);
      if (lastResult != null) {
        List<ChangeData<String>> deltasToBroadcast = deltaCache.getNewDeltas();
        JSONArray messages = new JSONArray();
        for (int i = 0; i < deltasToBroadcast.size(); i++) {
          try {
            messages.put(i, ChangeDataSerializer.dataToClientJson(
                deltasToBroadcast.get(i), onDiskVersion + i + 1));
          } catch (JSONException e) {
            throw new RuntimeException(e);
          }
        }

        lastResult.broadcastData = messages;
        lastResult.indexData = appender.getIndexedHtml();
      }
    }

    @Override
    public void rollback() {
      tx.rollback();
    }
  }

  void completeTransaction(CheckedTransaction tx, SlobId slobId,
      MutationLog.Appender appender) throws PermanentFailure, RetryableFailure {
    appender.flush();
    // TODO: share code with SlobStoreImpl.newObject().
    for (PreCommitAction action : preCommitActions) {
      log.info("Calling pre-commit action " + action);
      action.run(tx, slobId, appender.getStagedVersion(), appender.getStagedState());
    }
    Runnable postCommitRunnable =
        postCommitActionScheduler.prepareCommitAndGetPostCommitRunnable(
        tx, slobId, appender.getStagedVersion(), appender.getStagedState());
    log.info("Committing...");
    try {
      tx.commit();
    } catch (RetryableFailure e) {
      log.log(Level.INFO, "RetryableFailure while committing mutation", e);
      monitoring.incrementCounter("object-update-transaction-retryable-failure");
      throw e;
    } catch (PermanentFailure e) {
      log.log(Level.INFO, "PermanentFailure while committing mutation", e);
      monitoring.incrementCounter("object-update-transaction-permanent-failure");
      throw e;
    }
    log.info("Commit successful");
    // TODO: share code with SlobStoreImpl.newObject().
    appender.postCommit();
    postCommitRunnable.run();
  }

  private final SlobModel model;
  private final MutationLogFactory mutationLogFactory;
  private final CheckedDatastore datastore;
  private final MonitoringVars monitoring;
  private final Set<PreCommitAction> preCommitActions;
  private final PostCommitActionScheduler postCommitActionScheduler;
  // See commit ebb4736368b6d371a1bf5005541d96b88dcac504 for my failed attempt
  // at using CacheBuilder.  TODO(ohler): Figure out the right solution to this.
  @SuppressWarnings("deprecation")
  private final Map<SlobId, Processor> processors = new MapMaker()
      .weakValues()
      .makeComputingMap(new Function<SlobId, Processor>() {
        @Override public Processor apply(final SlobId id) {
          log.info("Creating new Processor for " + id);
          return new Processor(
              new TransactionFactory<UpResult, Tx>() {
                @Override public Tx beginTransaction() throws RetryableFailure, PermanentFailure {
                  return new Tx(id, datastore.beginTransaction());
                }
              }, new RetryHelper());
        }
      });

  @Inject
  public LocalMutationProcessor(SlobModel model,
      MutationLogFactory mutationLogFactory, CheckedDatastore datastore,
      MonitoringVars monitoring,
      Set<PreCommitAction> preCommitActions,
      PostCommitActionScheduler postCommitActionScheduler) {
    this.model = model;
    this.mutationLogFactory = mutationLogFactory;
    this.datastore = datastore;
    this.monitoring = monitoring;
    this.preCommitActions = preCommitActions;
    this.postCommitActionScheduler = postCommitActionScheduler;
  }

  private String jsonBroadcastData(SlobId objectId, JSONArray broadcastData) {
    try {
      JSONObject obj = new JSONObject();
      obj.put("id", objectId.getId());
      obj.put("m", broadcastData);
      return obj.toString();
    } catch (JSONException e) {
      throw new RuntimeException("Bad broadcast data: " + broadcastData, e);
    }
  }

  public ServerMutateResponse mutateObject(ServerMutateRequest req) throws IOException {
    SlobId objectId = new SlobId(req.getSession().getObjectId());
    Preconditions.checkArgument(req.getVersion() >= 0, "Invalid version: %s", req.getVersion());
    Preconditions.checkArgument(!req.getPayload().isEmpty(), "Empty payload list");

    Update update = new Update(objectId,
        new ClientId(req.getSession().getClientId()),
        req.getVersion(),
        req.getPayload());
    log.info("mutateObject, update=" + update);

    UpResult result;
    try {
      result = processors.get(objectId).processUpdate(update);
    } catch (PermanentFailure e) {
      throw new IOException(e);
    }

    if (result.isRejected()) {
      throw new BadRequestException(result.exception);
    }

    ServerMutateResponse response = new ServerMutateResponseGsonImpl();
    response.setResultingVersion(result.getResultingRevision());
    response.setBroadcastData(jsonBroadcastData(objectId, result.getBroadcastData()));
    response.setIndexData(result.getIndexData());
    return response;
  }

}
