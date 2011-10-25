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

package com.google.walkaround.wave.server.googleimport;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.walkaround.proto.ImportTaskPayload;
import com.google.walkaround.proto.RobotSearchDigest;
import com.google.walkaround.proto.gson.ImportTaskPayloadGsonImpl;
import com.google.walkaround.proto.gson.RobotSearchDigestGsonImpl;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedIterator;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.appengine.DatastoreUtil;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.WalkaroundServletModule;
import com.google.walkaround.wave.server.auth.StableUserId;
import com.google.walkaround.wave.server.gxp.SourceInstance;

import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.util.Pair;
import org.waveprotocol.wave.model.util.ValueUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Holds data about import status.  All data that should be stored in one entity
 * group per user goes into this table.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class PerUserTable {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(PerUserTable.class.getName());

  private static final String ROOT_ENTITY_KIND = "ImportUser";
  private static final String WAVE_ENTITY_KIND = "ImportWave";
  private static final String WAVE_DIGEST_PROPERTY = "digest";
  // Last modified time is also stored in the digest but we put it in a separate
  // property as well so that we can have an index on it.
  private static final String WAVE_LAST_MODIFIED_MILLIS_PROPERTY = "lastModified";

  private static final String TASK_ENTITY_KIND = "ImportTask";
  private static final String TASK_CREATION_TIME_MILLIS_PROPERTY = "created";
  private static final String TASK_PAYLOAD_PROPERTY = "payload";

  private final SourceInstance.Factory sourceInstanceFactory;
  private final Queue taskQueue;

  @Inject
  public PerUserTable(SourceInstance.Factory sourceInstanceFactory,
      @ImportTaskQueue Queue taskQueue) {
    this.sourceInstanceFactory = sourceInstanceFactory;
    this.taskQueue = taskQueue;
  }

  private Key makeRootKey(StableUserId userId) {
    return KeyFactory.createKey(ROOT_ENTITY_KIND, userId.getId());
  }

  private String makeWaveKeyString(SourceInstance sourceInstance, WaveId waveId) {
    Preconditions.checkArgument(!sourceInstance.serialize().contains(" "),
        "Source instance id contains space: %s", sourceInstance);
    // By design, wave ids should be unique across all instances, but it's easy
    // not to rely on this, so let's not.
    return sourceInstance.serialize() + " " + waveId.serialise();
  }

  private Pair<SourceInstance, WaveId> parseWaveKey(Key key) {
    String s = key.getName();
    int space = s.indexOf(" ");
    Assert.check(space >= 0, "No space: %s", key);
    return Pair.of(sourceInstanceFactory.parseUnchecked(s.substring(0, space)),
        WaveId.deserialise(s.substring(space + 1)));
  }

  private Key makeWaveKey(StableUserId userId, SourceInstance sourceInstance, WaveId waveId) {
    return KeyFactory.createKey(makeRootKey(userId),
        WAVE_ENTITY_KIND, makeWaveKeyString(sourceInstance, waveId));
  }

  private Key makeTaskKey(StableUserId userId, long taskId) {
    return KeyFactory.createKey(makeRootKey(userId), TASK_ENTITY_KIND, taskId);
  }

  private RemoteWave parseWaveEntity(Entity entity) {
    Pair<SourceInstance, WaveId> instanceAndWaveId = parseWaveKey(entity.getKey());
    try {
      RobotSearchDigest digest = GsonProto.fromGson(new RobotSearchDigestGsonImpl(),
          DatastoreUtil.getExistingProperty(entity, WAVE_DIGEST_PROPERTY, Text.class).getValue());
      Assert.check(instanceAndWaveId.getSecond().equals(WaveId.deserialise(digest.getWaveId())),
          "Wave id mismatch: %s, %s", instanceAndWaveId, entity);
      Assert.check(
          DatastoreUtil.getExistingProperty(entity, WAVE_LAST_MODIFIED_MILLIS_PROPERTY, Long.class)
              .equals(digest.getLastModifiedMillis()),
          "Mismatched last modified times: %s, %s", digest, entity);
      return new RemoteWave(instanceAndWaveId.getFirst(), digest);
    } catch (MessageException e) {
      throw new RuntimeException("Failed to parse wave entity: " + entity, e);
    }
  }

  private Entity serializeWave(StableUserId userId, RemoteWave w) {
    Key key = makeWaveKey(userId, w.getSourceInstance(),
        WaveId.deserialise(w.getDigest().getWaveId()));
    Entity e = new Entity(key);
    DatastoreUtil.setNonNullUnindexedProperty(e, WAVE_DIGEST_PROPERTY,
        new Text(GsonProto.toJson((RobotSearchDigestGsonImpl) w.getDigest())));
    DatastoreUtil.setNonNullIndexedProperty(e, WAVE_LAST_MODIFIED_MILLIS_PROPERTY,
        w.getDigest().getLastModifiedMillis());
    // Sanity check.
    Assert.check(w.equals(parseWaveEntity(e)), "Serialized %s incorrectly: %s", w, e);
    return e;
  }

  private ImportTask parseTaskEntity(Entity entity) {
    long taskId = entity.getKey().getId();
    StableUserId userId = new StableUserId(entity.getParent().getName());
    try {
      return new ImportTask(userId, taskId,
          DatastoreUtil.getExistingProperty(entity, TASK_CREATION_TIME_MILLIS_PROPERTY, Long.class),
          GsonProto.fromGson(new ImportTaskPayloadGsonImpl(),
              DatastoreUtil.getExistingProperty(entity, TASK_PAYLOAD_PROPERTY, Text.class)
                  .getValue()));
    } catch (MessageException e) {
      throw new RuntimeException("Failed to parse task entity: " + entity, e);
    }
  }

  // Returns an entity with an incomplete key (the datastore will fill in an auto-allocated id).
  private Entity serializeTask(
      StableUserId userId, long creationTimeMillis, ImportTaskPayload payload) {
    Entity entity = new Entity(TASK_ENTITY_KIND, makeRootKey(userId));
    DatastoreUtil.setNonNullIndexedProperty(entity, TASK_CREATION_TIME_MILLIS_PROPERTY,
        creationTimeMillis);
    DatastoreUtil.setNonNullUnindexedProperty(entity, TASK_PAYLOAD_PROPERTY,
        new Text(GsonProto.toJson((ImportTaskPayloadGsonImpl) payload)));
    // Can't deserialize for sanity check here since the key is incomplete.  But
    // our caller can do the sanity check after put() completes the key.
    return entity;
  }

  /**
   * Adds the given remote waves.
   *
   * Returns true if any entities were put (i.e., if a commit is needed), false
   * otherwise.
   */
  public boolean addRemoteWaves(
      CheckedTransaction tx, StableUserId userId, SourceInstance sourceInstance,
      List<RobotSearchDigest> digests) throws RetryableFailure, PermanentFailure {
    log.info("Adding " + digests.size() + " digests");
    Map<Key, Entity> newEntities = Maps.newHashMap();
    for (RobotSearchDigest digest : digests) {
      Entity e = serializeWave(userId, new RemoteWave(sourceInstance, digest));
      newEntities.put(e.getKey(), e);
    }
    // Avoid overwriting existing entities since they may contain nontrivial
    // import state.  It might be good to update snippet and last modified time
    // but let's not bother for now.
    //
    // Maybe we should get the keys only?  Probably not a big enough win to
    // justify how much more difficult that is to program (need to use queries).
    Map<Key, Entity> found = tx.get(newEntities.keySet());
    if (!found.isEmpty()) {
      // Not very efficient but we only expect this to be called for 300 or so
      // digests anyway.
      newEntities.keySet().removeAll(found.keySet());
      log.warning("Ignoring " + found.size() + " existing entities; "
          + newEntities.size() + " remaining");
    }
    if (newEntities.isEmpty()) {
      return false;
    }
    log.info("Putting " + ValueUtils.abbrev("" + newEntities, 500));
    tx.put(newEntities.values());
    return true;
  }

  // TODO(ohler): make this scalable, e.g. by adding pagination
  public List<RemoteWave> getAllWaves(CheckedTransaction tx, StableUserId userId)
      throws RetryableFailure, PermanentFailure {
    log.info("getAllWaves(" + userId + ")");
    CheckedIterator i = tx.prepare(new Query(WAVE_ENTITY_KIND)
        .setAncestor(makeRootKey(userId))
        .addSort(WAVE_LAST_MODIFIED_MILLIS_PROPERTY, Query.SortDirection.DESCENDING))
        .asIterator();
    ImmutableList.Builder<RemoteWave> b = ImmutableList.builder();
    while (i.hasNext()) {
      b.add(parseWaveEntity(i.next()));
    }
    List<RemoteWave> out = b.build();
    log.info("got " + out.size() + " waves");
    return out;
  }

  // Among other things, mutates q to make it keys-only.
  private boolean deleteAll(CheckedTransaction tx, Query q)
      throws RetryableFailure, PermanentFailure {
    q.setKeysOnly();
    CheckedIterator i = tx.prepare(q).asIterator();
    if (!i.hasNext()) {
      return false;
    }
    while (i.hasNext()) {
      tx.delete(i.next().getKey());
    }
    return true;
  }

  // TODO(ohler): make this scalable, by deleting in chunks, or through the task queue
  /** Returns true if any entities have been deleted (i.e., tx needs to be committed. */
  public boolean deleteAllWaves(CheckedTransaction tx, StableUserId userId)
      throws RetryableFailure, PermanentFailure {
    log.info("deleteAllWaves(" + userId + ")");
    return deleteAll(tx, new Query(WAVE_ENTITY_KIND).setAncestor(makeRootKey(userId)));
  }

  /** Returns true if any entities have been deleted (i.e., tx needs to be committed. */
  public boolean deleteAllTasks(CheckedTransaction tx, StableUserId userId)
      throws RetryableFailure, PermanentFailure {
    log.info("deleteAllTasks(" + userId + ")");
    return deleteAll(tx, new Query(TASK_ENTITY_KIND).setAncestor(makeRootKey(userId)));
  }

  public ImportTask addTask(CheckedTransaction tx, StableUserId userId, ImportTaskPayload payload)
      throws RetryableFailure, PermanentFailure {
    Entity entity = serializeTask(userId, System.currentTimeMillis(), payload);
    tx.put(entity);
    // Sanity check, and retrieve ID.
    ImportTask written = parseTaskEntity(entity);
    Assert.check(userId.equals(written.getUserId()), "User id mismatch: %s, %s", userId, written);
    Assert.check(payload.equals(written.getPayload()),
        "Payload mismatch: %s, %s", payload, written);
    tx.enqueueTask(taskQueue,
        TaskOptions.Builder.withUrl(WalkaroundServletModule.IMPORT_TASK_PATH)
            .method(TaskOptions.Method.POST)
            .param(ImportTaskHandler.USER_ID_HEADER, userId.getId())
            .param(ImportTaskHandler.TASK_ID_HEADER, "" + written.getTaskId()));
    return written;
  }

  public List<ImportTask> getAllTasks(CheckedTransaction tx, StableUserId userId)
      throws RetryableFailure, PermanentFailure {
    log.info("getAllTasks(" + userId + ")");
    CheckedIterator i = tx.prepare(new Query(TASK_ENTITY_KIND)
        .setAncestor(makeRootKey(userId))
        .addSort(Entity.KEY_RESERVED_PROPERTY, Query.SortDirection.ASCENDING))
        .asIterator();
    ImmutableList.Builder<ImportTask> b = ImmutableList.builder();
    while (i.hasNext()) {
      b.add(parseTaskEntity(i.next()));
    }
    List<ImportTask> out = b.build();
    log.info("got " + out.size() + " tasks");
    return out;
  }

  @Nullable public ImportTask getTask(CheckedTransaction tx, StableUserId userId, long taskId)
      throws RetryableFailure, PermanentFailure {
    log.info("getTask(" + userId + ", " + taskId + ")");
    Entity entity = tx.get(makeTaskKey(userId, taskId));
    ImportTask task = entity == null ? null : parseTaskEntity(entity);
    log.info("got " + task);
    return task;
  }

  public void deleteTask(CheckedTransaction tx, StableUserId userId, long taskId)
      throws RetryableFailure, PermanentFailure {
    log.info("deleteTask(" + userId + ", " + taskId + ")");
    tx.delete(makeTaskKey(userId, taskId));
  }

}
