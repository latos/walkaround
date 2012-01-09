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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ChangeRejected;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.InvalidSnapshot;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.SlobModel;
import com.google.walkaround.slob.shared.SlobModel.ReadableSlob;
import com.google.walkaround.slob.shared.StateAndVersion;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedIterator;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.appengine.DatastoreUtil;
import com.google.walkaround.util.shared.Assert;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Functionality for traversing and appending to the mutation log.
 *
 * Use of only this class for mutating the log guarantees no corruption will
 * occur.
 *
 * XXX(danilatos): Not necessarily thread safe, should only be used in a
 * single-threaded fashion but let's make it thread safe anyway just in case...
 * (better yet, add assertions that it's used only from one thread, so that we
 * notice when it's not)
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class MutationLog {

  public interface MutationLogFactory {
    // TODO(danilatos): Rename this method to "get" to avoid connotations of
    // creating objects. Do this any time there's low chance of conflicts.
    MutationLog create(CheckedTransaction tx, SlobId objectId);
  }

  private static final Logger log = Logger.getLogger(MutationLog.class.getName());

  @VisibleForTesting static final String DELTA_OP_PROPERTY = "op";
  @VisibleForTesting static final String DELTA_CLIENT_ID_PROPERTY = "sid";
  @VisibleForTesting static final String SNAPSHOT_DATA_PROPERTY = "Data";
  private static final String METADATA_PROPERTY = "Metadata";

  // Datastore does not allow ids to be 0.

  private static long versionFromDeltaId(long id) {
    return id - 1;
  }

  private static long deltaIdFromVersion(long version) {
    return version + 1;
  }

  private static final class DeltaEntry {
    private final SlobId objectId;
    private final long version;
    private final ChangeData<String> data;

    DeltaEntry(SlobId objectId, long version, ChangeData<String> data) {
      this.objectId = objectId;
      this.version = version;
      this.data = data;
    }

    long getResultingVersion() {
      return version + 1;
    }

    @Override
    public String toString() {
      return "DeltaEntry(" + objectId + ", " + version + ", " + data + ")";
    }
  }

  private static final class SnapshotEntry {
    private final SlobId objectId;
    private final long version;
    private final String snapshot;

    SnapshotEntry(SlobId objectId, long version, String snapshot) {
      this.objectId = objectId;
      this.version = version;
      this.snapshot = snapshot;
    }

    @Override
    public String toString() {
      return "SnapshotEntry(" + objectId + ", " + version + ", " + snapshot + ")";
    }
  }

  static Key makeRootEntityKey(String entityGroupKind, SlobId objectId) {
    Key key = KeyFactory.createKey(entityGroupKind, objectId.getId());
    Assert.check(parseRootEntityKey(entityGroupKind, key).equals(objectId),
        "Mismatch: %s, %s", objectId, key);
    return key;
  }

  static SlobId parseRootEntityKey(String entityGroupKind, Key key) {
    Preconditions.checkArgument(entityGroupKind.equals(key.getKind()),
        "Key doesn't have kind %s: %s", entityGroupKind, key);
    return new SlobId(key.getName());
  }

  private Key makeRootEntityKey(SlobId slobId) {
    return makeRootEntityKey(entityGroupKind, slobId);
  }

  private Key makeDeltaKey(SlobId objectId, long version) {
    return KeyFactory.createKey(
        makeRootEntityKey(objectId),
        deltaEntityKind,
        deltaIdFromVersion(version));
  }

  private Key makeSnapshotKey(SlobId objectId, long version) {
    return KeyFactory.createKey(
        makeRootEntityKey(objectId),
        snapshotEntityKind,
        version);
  }

  private Key makeDeltaKey(DeltaEntry e) {
    return makeDeltaKey(e.objectId, e.version);
  }

  private Key makeSnapshotKey(SnapshotEntry e) {
    return makeSnapshotKey(e.objectId, e.version);
  }

  private static long estimateSizeBytes(Key key) {
    // It's not documented whether and how the representation returned by
    // keyToString() relates to the representation that the API limits are based
    // on; but in any case, it should be good enough for our estimate.
    long web64Size = KeyFactory.keyToString(key).length();
    // Base-64 strings have 6 bits per character, thus the ratio is 6/8 or 3/4.
    // Divide by 4 first to avoid overflow.  The base-64 string should be padded
    // to a length that is a multiple of 4, so there is no rounding error.  (If
    // it's not padded, our estimate will be off; but that is tolerable, too.)
    return (web64Size / 4) * 3;
  }

  private long estimateSizeBytes(DeltaEntry deltaEntry) {
    return estimateSizeBytes(makeDeltaKey(deltaEntry))
        + DELTA_CLIENT_ID_PROPERTY.length() + deltaEntry.data.getClientId().getId().length()
        + DELTA_OP_PROPERTY.length() + deltaEntry.data.getPayload().length();
  }

  private long estimateSizeBytes(SnapshotEntry snapshotEntry) {
    return estimateSizeBytes(makeSnapshotKey(snapshotEntry))
        + SNAPSHOT_DATA_PROPERTY.length() + snapshotEntry.snapshot.length();
  }

  public interface DeltaEntityConverter {
    ChangeData<String> convert(Entity entity);
  }

  public static class DefaultDeltaEntityConverter implements DeltaEntityConverter {
    @Override public ChangeData<String> convert(Entity entity) {
      return new ChangeData<String>(
          new ClientId(
              DatastoreUtil.getExistingProperty(entity, DELTA_CLIENT_ID_PROPERTY, String.class)),
          DatastoreUtil.getExistingProperty(entity, DELTA_OP_PROPERTY, Text.class).getValue());
    }
  }

  private DeltaEntry parseDelta(Entity entity) {
    SlobId slobId = new SlobId(entity.getKey().getParent().getName());
    long version = versionFromDeltaId(entity.getKey().getId());
    return new DeltaEntry(slobId, version, deltaEntityConverter.convert(entity));
  }

  private static void populateDeltaEntity(DeltaEntry in, Entity out) {
    DatastoreUtil.setNonNullUnindexedProperty(out, DELTA_CLIENT_ID_PROPERTY,
        in.data.getClientId().getId());
    DatastoreUtil.setNonNullUnindexedProperty(out, DELTA_OP_PROPERTY,
        new Text(in.data.getPayload()));
  }

  private static SnapshotEntry parseSnapshot(Entity e) {
    SlobId id = new SlobId(e.getKey().getParent().getName());
    long version = e.getKey().getId();
    return new SnapshotEntry(id, version,
        DatastoreUtil.getExistingProperty(e, SNAPSHOT_DATA_PROPERTY, Text.class).getValue());
  }

  private static void populateSnapshotEntity(SnapshotEntry in, Entity out) {
    DatastoreUtil.setNonNullUnindexedProperty(out, SNAPSHOT_DATA_PROPERTY, new Text(in.snapshot));
  }

  /**
   * An iterator over a datastore delta result list.
   *
   * Can be forward or reverse.
   *
   * The peek methods should be used in conjunction with
   * {@link DeltaIterator#hasNext()} since they will throw
   * {@code NoSuchElementException} if the end of the sequence is reached.
   */
  public class DeltaIterator {
    private final CheckedIterator it;
    private final boolean forward;
    private long previousResultingVersion;
    @Nullable private DeltaEntry peeked = null;

    public DeltaIterator(CheckedIterator it, boolean forward) {
      this.it = Preconditions.checkNotNull(it, "Null it");
      this.forward = forward;
      previousResultingVersion = -1;
    }

    public boolean hasNext() throws PermanentFailure, RetryableFailure {
      return peeked != null || it.hasNext();
    }

    DeltaEntry peekEntry() throws PermanentFailure, RetryableFailure {
      if (peeked == null) {
        // Let it.next() throw if there is no next.
        peeked = parseDelta(it.next());
        checkVersion(peeked);
      }
      return peeked;
    }

    DeltaEntry nextEntry() throws PermanentFailure, RetryableFailure {
      DeltaEntry result = peekEntry();
      peeked = null;
      return result;
    }

    public ChangeData<String> peek() throws PermanentFailure, RetryableFailure {
      return peekEntry().data;
    }

    public ChangeData<String> next() throws PermanentFailure, RetryableFailure {
      return nextEntry().data;
    }

    public boolean isForward() {
      return forward;
    }

    private void checkVersion(DeltaEntry delta) {
      if (previousResultingVersion != -1) {
        long expectedResultingVersion = previousResultingVersion + (forward ? 1 : -1);
        Assert.check(delta.getResultingVersion() == expectedResultingVersion,
            "%s: Expected version %s, got %s",
            this, expectedResultingVersion, delta.getResultingVersion());
      }
    }

    @Override public String toString() {
      return "DeltaIterator(" + (forward ? "forward" : "reverse")
          + ", " + previousResultingVersion + ")";
    }
  }

  /**
   * Extends the log by additional deltas.  Automatically takes snapshots as
   * needed.
   *
   * Deltas and snapshots are staged in memory and added to the underlying
   * transaction only when {@link Appender#flush()} is called.
   */
  public class Appender {
    private final StateAndVersion state;
    private final List<DeltaEntry> stagedDeltaEntries = Lists.newArrayList();
    private final List<SnapshotEntry> stagedSnapshotEntries = Lists.newArrayList();
    private long estimatedBytesStaged = 0;
    private long mostRecentSnapshotBytes;
    private long totalDeltaBytesSinceSnapshot;

    private Appender(StateAndVersion state,
        long mostRecentSnapshotBytes,
        long totalDeltaBytesSinceSnapshot) {
      this.state = state;
      this.mostRecentSnapshotBytes = mostRecentSnapshotBytes;
      this.totalDeltaBytesSinceSnapshot = totalDeltaBytesSinceSnapshot;
    }

    /**
     * Stages a delta for writing, verifying that it is valid (applies cleanly).
     */
    public void append(ChangeData<String> delta) throws ChangeRejected {
      long oldVersion = state.getVersion();
      state.apply(delta);
      DeltaEntry deltaEntry = new DeltaEntry(objectId, oldVersion,
          new ChangeData<String>(delta.getClientId(), delta.getPayload()));
      stagedDeltaEntries.add(deltaEntry);
      long thisDeltaBytes = estimateSizeBytes(deltaEntry);
      estimatedBytesStaged += thisDeltaBytes;
      totalDeltaBytesSinceSnapshot += thisDeltaBytes;

      // TODO(ohler): Avoid computing the snapshot every time since this is
      // costly.  Add a size estimation to slob instead.  We need this anyway to
      // implement size limits.
      SnapshotEntry snapshotEntry = new SnapshotEntry(
          objectId, state.getVersion(), state.getState().snapshot());
      long snapshotBytes = estimateSizeBytes(snapshotEntry);
      log.info("Object now at version " + state.getVersion() + "; snapshotBytes=" + snapshotBytes
          + ", mostRecentSnapshotBytes=" + mostRecentSnapshotBytes
          + ", totalDeltaBytesSinceSnapshot=" + totalDeltaBytesSinceSnapshot);
      // To reconstruct the object's snapshot S at the current version, we will
      // need to read the most recent snapshot P followed by a sequence of
      // deltas D.  To keep the amount of data required for this reconstruction
      // within a constant factor of |S| (the size of S), we write S to disk if
      // k * |S| < |P| + |D|, for some constant k.
      //
      // TODO(ohler): Provide bound on disk space consumption.
      //
      // TODO(ohler): This formula assumes that reading & reconstructing a
      // snapshot has the same cost per byte as reading & applying a delta.
      // That's probably not true.  The cost of applying a delta may not even be
      // linear in the size of that delta (and the same is true for
      // reconstructing from a snapshot); this depends on the model.  We should
      // allow for models to influence when to take snapshots, perhaps by
      // letting the model provide a size metric for deltas and snapshots
      // instead of using bytes, or by measuring actual computation time if we
      // can do that reliably.  It would be best to make it impossible for
      // models to cause quadratic disk space consumption, though.
      final long k = 2;
      if (k * snapshotBytes < mostRecentSnapshotBytes + totalDeltaBytesSinceSnapshot) {
        log.info("Adding snapshot");
        stagedSnapshotEntries.add(snapshotEntry);
        mostRecentSnapshotBytes = snapshotBytes;
        totalDeltaBytesSinceSnapshot = 0;
        estimatedBytesStaged += snapshotBytes;
      }
    }

    /**
     * A rough estimate of the total bytes currently staged to be written.  This
     * may be subject to inaccuracies such as counting Java's UTF-16 characters
     * in strings as one byte each (actual encoding that the API limits are
     * based on is probably UTF-8) and only counting raw payloads without taking
     * metadata, encoding overhead, or indexing overhead into account.
     */
    public long estimatedBytesStaged() {
      return estimatedBytesStaged;
    }

    public long getStagedVersion() {
      return state.getVersion();
    }

    // If having ReadableSlob in addition to Slob adds too much complexity to
    // SlobModel implementations, we could replace flush() with
    // closeAndGetStagedState(), which would hand over ownership of the mutable
    // Slob to the caller.
    public ReadableSlob getStagedState() {
      return state.getState();
    }

    public boolean hasNewDeltas() {
      return !stagedDeltaEntries.isEmpty();
    }

    /**
     * Returns the index data of the model at head state (including staged mutations).
     */
    // TODO(ohler): Add a generic task queue hook to MutationLog and
    // SlobStore, and use that to move indexing into an asynchronous task
    // queue task.  That would make indexing reliable and avoid polluting this
    // API with SlobManager concerns like indexing.
    public String getIndexedHtml() {
      return state.getState().getIndexedHtml();
    }

    /**
     * Calls {@code put()} on all staged deltas and snapshots.
     */
    public void flush() throws PermanentFailure, RetryableFailure {
      log.info("Flushing " + stagedDeltaEntries.size() + " deltas and "
          + stagedSnapshotEntries.size() + " snapshots");
      put(tx, stagedDeltaEntries, stagedSnapshotEntries);
      stagedDeltaEntries.clear();
      stagedSnapshotEntries.clear();
      estimatedBytesStaged = 0;
    }
  }

  private final String entityGroupKind;
  private final String deltaEntityKind;
  private final String snapshotEntityKind;
  private final DeltaEntityConverter deltaEntityConverter;

  private final CheckedTransaction tx;
  private final SlobId objectId;
  private final SlobModel model;

  @AssistedInject
  public MutationLog(@SlobRootEntityKind String entityGroupKind,
      @SlobDeltaEntityKind String deltaEntityKind,
      @SlobSnapshotEntityKind String snapshotEntityKind,
      DeltaEntityConverter deltaEntityConverter,
      @Assisted CheckedTransaction tx, @Assisted SlobId objectId,
      SlobModel model) {
    this.entityGroupKind = entityGroupKind;
    this.deltaEntityKind = deltaEntityKind;
    this.snapshotEntityKind = snapshotEntityKind;
    this.deltaEntityConverter = deltaEntityConverter;
    this.tx = Preconditions.checkNotNull(tx, "Null tx");
    this.objectId = Preconditions.checkNotNull(objectId, "Null objectId");
    this.model = Preconditions.checkNotNull(model, "Null model");
  }

  /** @see #forwardHistory(long, Long, FetchOptions) */
  public DeltaIterator forwardHistory(long minVersion, @Nullable Long maxVersion)
      throws PermanentFailure, RetryableFailure {
    return forwardHistory(minVersion, maxVersion, FetchOptions.Builder.withDefaults());
  }

  /**
   * Returns an iterator over the specified version range of the mutation log,
   * in a forwards direction.
   *
   * @param maxVersion null to end with the final delta in the mutation log
   *        (inclusive).
   */
  public DeltaIterator forwardHistory(long minVersion, @Nullable Long maxVersion,
      FetchOptions fetchOptions) throws PermanentFailure, RetryableFailure {
    return getDeltaIterator(minVersion, maxVersion, fetchOptions, true);
  }

  /** @see #reverseHistory(long, Long, FetchOptions) */
  public DeltaIterator reverseHistory(long minVersion, @Nullable Long maxVersion)
      throws PermanentFailure, RetryableFailure {
    return reverseHistory(minVersion, maxVersion, FetchOptions.Builder.withDefaults());
  }

  /**
   * Returns an iterator over the specified version range of the mutation log,
   * in a backwards direction.
   *
   * @param maxVersion null to begin with the final delta in the mutation log.
   */
  public DeltaIterator reverseHistory(long minVersion, @Nullable Long maxVersion,
      FetchOptions fetchOptions) throws PermanentFailure, RetryableFailure {
    return getDeltaIterator(minVersion, maxVersion, fetchOptions, false);
  }

  /**
   * Returns the current version of the object.
   */
  public long getVersion() throws PermanentFailure, RetryableFailure {
    DeltaIterator it = reverseHistory(0, null, FetchOptions.Builder.withLimit(1));
    if (!it.hasNext()) {
      return 0;
    }
    return it.nextEntry().getResultingVersion();
  }

  /**
   * Tuple of values returned by {@link #prepareAppender()}.
   */
  public static class AppenderAndCachedDeltas {
    private final Appender appender;
    private final List<ChangeData<String>> reverseDeltasRead;
    private final DeltaIterator reverseDeltaIterator;

    public AppenderAndCachedDeltas(Appender appender,
        List<ChangeData<String>> reverseDeltasRead,
        DeltaIterator reverseDeltaIterator) {
      Preconditions.checkNotNull(appender, "Null appender");
      Preconditions.checkNotNull(reverseDeltasRead, "Null reverseDeltasRead");
      Preconditions.checkNotNull(reverseDeltaIterator, "Null reverseDeltaIterator");
      this.appender = appender;
      this.reverseDeltasRead = reverseDeltasRead;
      this.reverseDeltaIterator = reverseDeltaIterator;
    }

    public Appender getAppender() {
      return appender;
    }

    public List<ChangeData<String>> getReverseDeltasRead() {
      return reverseDeltasRead;
    }

    public DeltaIterator getReverseDeltaIterator() {
      return reverseDeltaIterator;
    }

    @Override public String toString() {
      return "AppenderAndCachedDeltas("
          + appender + ", "
          + reverseDeltasRead + ", "
          + reverseDeltaIterator
          + ")";
    }
  }

  private void checkDeltaDoesNotExist(long version) throws RetryableFailure, PermanentFailure {
    // This check is not necessary but let's be paranoid.
    // TODO(danilatos): Make this async and check the result on flush() to
    // improve latency. Or, make an informed decision to remove it.
    Entity existing = tx.get(makeDeltaKey(objectId, version));
    Assert.check(existing == null,
        "Datastore fail?  Found unexpected delta: %s, %s, %s",
        objectId, version, existing);
  }

  /**
   * Creates an {@link Appender} for this mutation log and returns it together
   * with some by-products.  The by-products can be useful to callers who need
   * data from the datastore that overlaps with what was needed to create the
   * {@code Appender}, to avoid redudant datastore reads.
   */
  public AppenderAndCachedDeltas prepareAppender() throws PermanentFailure, RetryableFailure {
    DeltaIterator deltaIterator = getDeltaIterator(
        0, null, FetchOptions.Builder.withDefaults(), false);
    if (!deltaIterator.hasNext()) {
      log.info("Prepared appender at version 0");
      checkDeltaDoesNotExist(0);
      return new AppenderAndCachedDeltas(
          new Appender(createObject(null), 0, 0),
          ImmutableList.<ChangeData<String>>of(), deltaIterator);
    } else {
      SnapshotEntry snapshotEntry = getSnapshotEntryAtOrBefore(null);
      StateAndVersion state = createObject(snapshotEntry);
      long snapshotVersion = state.getVersion();
      long snapshotBytes = snapshotEntry == null ? 0 : estimateSizeBytes(snapshotEntry);

      // Read deltas between snapshot and current version.  Since we determine
      // the current version by reading the first delta (in our reverse
      // iterator), we always read at least one delta even if none are needed to
      // reconstruct the current version.
      DeltaEntry finalDelta = deltaIterator.nextEntry();
      long currentVersion = finalDelta.getResultingVersion();
      if (currentVersion == snapshotVersion) {
        // We read a delta but it precedes the snapshot.  It still has to go
        // into deltasRead in our AppenderAndCachedDeltas to ensure that there
        // is no gap between deltasRead and reverseIterator.
        log.info("Prepared appender; snapshotVersion=currentVersion=" + currentVersion);
        checkDeltaDoesNotExist(snapshotVersion);
        return new AppenderAndCachedDeltas(
            new Appender(state, snapshotBytes, 0),
            ImmutableList.of(finalDelta.data), deltaIterator);
      } else {
        // We need to apply the delta, and perhaps others.  Collect them.
        ImmutableList.Builder<ChangeData<String>> deltaAccu = ImmutableList.builder();
        deltaAccu.add(finalDelta.data);
        long totalDeltaBytesSinceSnapshot = estimateSizeBytes(finalDelta);
        {
          DeltaEntry delta = finalDelta;
          while (delta.version != snapshotVersion) {
            delta = deltaIterator.nextEntry();
            deltaAccu.add(delta.data);
            totalDeltaBytesSinceSnapshot += estimateSizeBytes(delta);
          }
        }
        ImmutableList<ChangeData<String>> reverseDeltas = deltaAccu.build();
        // Now iterate forward and apply the deltas.
        for (ChangeData<String> delta : Lists.reverse(reverseDeltas)) {
          try {
            state.apply(delta);
          } catch (ChangeRejected e) {
            throw new RuntimeException("Corrupt snapshot or delta history: "
                + objectId + " rejected delta " + delta + ": " + state);
          }
        }
        log.info("Prepared appender; snapshotVersion=" + snapshotVersion
            + ", " + reverseDeltas.size() + " deltas");
        checkDeltaDoesNotExist(state.getVersion());
        return new AppenderAndCachedDeltas(
            new Appender(state, snapshotBytes, totalDeltaBytesSinceSnapshot),
            reverseDeltas, deltaIterator);
      }
    }
  }

  private DeltaIterator getDeltaIterator(long startVersion, @Nullable Long endVersion,
      FetchOptions fetchOptions, boolean forward) throws PermanentFailure, RetryableFailure {
    checkRange(startVersion, endVersion);

    if (endVersion != null && startVersion == endVersion) {
      return new DeltaIterator(CheckedIterator.EMPTY, forward);
    }

    Query q = new Query(deltaEntityKind)
        .setAncestor(makeRootEntityKey(objectId))
        .addFilter(Entity.KEY_RESERVED_PROPERTY,
            FilterOperator.GREATER_THAN_OR_EQUAL, makeDeltaKey(objectId, startVersion))
        .addSort(Entity.KEY_RESERVED_PROPERTY,
            forward ? SortDirection.ASCENDING : SortDirection.DESCENDING);

    if (endVersion != null) {
      q.addFilter(Entity.KEY_RESERVED_PROPERTY,
          FilterOperator.LESS_THAN, makeDeltaKey(objectId, endVersion));
    }

    CheckedIterator result = tx.prepare(q).asIterator(fetchOptions);
    return new DeltaIterator(result, forward);
  }

  /**
   * Reconstructs the object at the specified version (current version if null).
   */
  public StateAndVersion reconstruct(@Nullable Long atVersion)
      throws PermanentFailure, RetryableFailure {
    checkRange(atVersion, null);

    StateAndVersion state = getSnapshottedState(atVersion);
    long startVersion = state.getVersion();
    Assert.check(atVersion == null || startVersion <= atVersion);

    DeltaIterator it = forwardHistory(startVersion, atVersion);
    while (it.hasNext()) {
      ChangeData<String> delta = it.next();
      try {
        state.apply(delta);
      } catch (ChangeRejected e) {
        throw new PermanentFailure(
            "Corrupt snapshot or delta history " + objectId + " @" + state.getVersion(), e);
      }
    }
    if (atVersion != null && state.getVersion() < atVersion) {
      throw new RuntimeException("Object max version is " + state.getVersion()
          + ", requested " + atVersion);
    }
    log.info("Reconstructed requested version " + atVersion
        + " from snapshot at " + startVersion
        + " followed by " + (state.getVersion() - startVersion) + " deltas");
    return state;
  }

  private void put(CheckedTransaction tx,
      List<DeltaEntry> newDeltas, List<SnapshotEntry> newSnapshots)
      throws PermanentFailure, RetryableFailure {
    Preconditions.checkNotNull(newDeltas, "null newEntries");
    Preconditions.checkNotNull(newSnapshots, "null newSnapshots");
    List<Entity> entities = Lists.newArrayListWithCapacity(newDeltas.size() + newSnapshots.size());
    for (DeltaEntry entry : newDeltas) {
      Key key = makeDeltaKey(entry);
      Entity newEntity = new Entity(key);
      populateDeltaEntity(entry, newEntity);
      parseDelta(newEntity); // Verify it parses with no exceptions.
      entities.add(newEntity);
    }
    for (SnapshotEntry entry : newSnapshots) {
      Key key = makeSnapshotKey(entry);
      Entity newEntity = new Entity(key);
      populateSnapshotEntity(entry, newEntity);
      parseSnapshot(newEntity); // Verify it parses with no exceptions.
      entities.add(newEntity);
    }
    tx.put(entities);
  }

  private SnapshotEntry getSnapshotEntryAtOrBefore(@Nullable Long atOrBeforeVersion)
      throws RetryableFailure, PermanentFailure {
   Query q = new Query(snapshotEntityKind)
        .setAncestor(makeRootEntityKey(objectId))
        .addSort(Entity.KEY_RESERVED_PROPERTY, SortDirection.DESCENDING);
    if (atOrBeforeVersion != null) {
      q = q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.LESS_THAN_OR_EQUAL,
          makeSnapshotKey(objectId, atOrBeforeVersion));
    }
    Entity e = tx.prepare(q).getFirstResult();
    log.info("query " + q + " returned first result " + e);
    return e == null ? null : parseSnapshot(e);
  }

  private StateAndVersion createObject(@Nullable SnapshotEntry entry) {
    String snapshot = entry == null ? null : entry.snapshot;
    long version = entry == null ? 0 : entry.version;
    try {
      return new StateAndVersion(model.create(snapshot), version);
    } catch (InvalidSnapshot e) {
      throw new RuntimeException("Could not create model from snapshot at version " + version
          + ": " + snapshot, e);
    }
  }

  /**
   * Constructs a model object from the snapshot with the highest version less
   * than or equal to atOrBeforeVersion.
   *
   * @param atOrBeforeVersion null for current version
   */
  public StateAndVersion getSnapshottedState(@Nullable Long atOrBeforeVersion)
      throws PermanentFailure, RetryableFailure {
    return createObject(getSnapshotEntryAtOrBefore(atOrBeforeVersion));
  }

  private void checkRange(@Nullable Long startVersion, @Nullable Long endVersion) {
    if (startVersion == null) {
      Preconditions.checkArgument(endVersion == null,
          "startVersion == null implies endVersion == null, not %s", endVersion);
    } else {
      Preconditions.checkArgument(startVersion >= 0 &&
          (endVersion == null || startVersion <= endVersion),
          "Invalid range requested (%s to %s)", startVersion, endVersion);

      // I doubt this would really happen, but...
      Assert.check(endVersion == null || (endVersion - startVersion <= Integer.MAX_VALUE),
          "Range too large: %s to %s", startVersion, endVersion);
    }
  }

  // TODO(ohler): eliminate; PreCommitHook should be enough
  @Nullable public String getMetadata() throws RetryableFailure, PermanentFailure {
    Key key = makeRootEntityKey(objectId);
    log.info("Looking up metadata " + key);
    Entity result = tx.get(key);
    log.info("Got " + result);
    return result == null ? null
        : DatastoreUtil.getExistingProperty(result, METADATA_PROPERTY, Text.class).getValue();
  }

  // TODO(ohler): eliminate; PreCommitHook should be enough
  public void putMetadata(String metadata) throws RetryableFailure, PermanentFailure {
    Key key = makeRootEntityKey(objectId);
    Entity e = new Entity(key);
    DatastoreUtil.setNonNullUnindexedProperty(e, METADATA_PROPERTY, new Text(metadata));
    log.info("Writing metadata: " + e);
    tx.put(e);
  }

}
