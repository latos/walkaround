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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.walkaround.proto.ImportTaskPayload;
import com.google.walkaround.proto.RobotSearchDigest;
import com.google.walkaround.proto.gson.ImportTaskPayloadGsonImpl;
import com.google.walkaround.proto.gson.RobotSearchDigestGsonImpl;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
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
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;
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
  private static final String WAVELET_ENTITY_KIND = "ImportWavelet";
  private static final String WAVELET_DIGEST_PROPERTY = "digest";
  // Last modified time is also stored in the digest but we put it in a separate
  // property as well so that we can have an index on it.
  private static final String WAVELET_LAST_MODIFIED_MILLIS_PROPERTY = "lastModified";
  private static final String WAVELET_PRIVATE_LOCAL_ID_PROPERTY = "privateLocalId";
  private static final String WAVELET_SHARED_LOCAL_ID_PROPERTY = "sharedLocalId";

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

  private Pair<SourceInstance, WaveletName> parseWaveletKeyString(String key) {
    String[] components = key.split(" ", -1);
    Assert.check(components.length == 3, "Wrong number of spaces: %s", key);
    return Pair.of(
        sourceInstanceFactory.parseUnchecked(components[0]),
        WaveletName.of(WaveId.deserialise(components[1]),
            WaveletId.deserialise(components[2])));
  }

  private String makeWaveletKeyString(SourceInstance sourceInstance, WaveletName waveletName) {
    // By design, wave ids should be unique across all instances, but it's easy
    // not to rely on this, so let's not.
    String key = sourceInstance.serialize()
        + " " + waveletName.waveId.serialise() + " " + waveletName.waveletId.serialise();
    Assert.check(Pair.of(sourceInstance, waveletName).equals(parseWaveletKeyString(key)),
        "Failed to serialize: %s, %s", sourceInstance, waveletName);
    return key;
  }

  private Pair<SourceInstance, WaveletName> parseWaveletKey(Key key) {
    return parseWaveletKeyString(key.getName());
  }

  private Key makeWaveletKey(StableUserId userId, SourceInstance sourceInstance,
      WaveletName waveletName) {
    return KeyFactory.createKey(makeRootKey(userId),
        WAVELET_ENTITY_KIND, makeWaveletKeyString(sourceInstance, waveletName));
  }

  private Key makeTaskKey(StableUserId userId, long taskId) {
    return KeyFactory.createKey(makeRootKey(userId), TASK_ENTITY_KIND, taskId);
  }

  private RemoteConvWavelet parseWaveletEntity(Entity entity) {
    Pair<SourceInstance, WaveletName> instanceAndWaveId = parseWaveletKey(entity.getKey());
    try {
      RobotSearchDigest digest = GsonProto.fromGson(new RobotSearchDigestGsonImpl(),
          DatastoreUtil.getExistingProperty(entity, WAVELET_DIGEST_PROPERTY, Text.class)
          .getValue());
      Assert.check(
          instanceAndWaveId.getSecond().waveId.equals(WaveId.deserialise(digest.getWaveId())),
          "Wave id mismatch: %s, %s", instanceAndWaveId, entity);
      Assert.check(DatastoreUtil.getExistingProperty(
              entity, WAVELET_LAST_MODIFIED_MILLIS_PROPERTY, Long.class)
          .equals(digest.getLastModifiedMillis()),
          "Mismatched last modified times: %s, %s", digest, entity);
      @Nullable String privateLocalId = DatastoreUtil.getOptionalProperty(
          entity, WAVELET_PRIVATE_LOCAL_ID_PROPERTY, String.class);
      @Nullable String sharedLocalId = DatastoreUtil.getOptionalProperty(
          entity, WAVELET_SHARED_LOCAL_ID_PROPERTY, String.class);
      return new RemoteConvWavelet(instanceAndWaveId.getFirst(), digest,
          instanceAndWaveId.getSecond().waveletId,
          privateLocalId == null ? null : new SlobId(privateLocalId),
          sharedLocalId == null ? null : new SlobId(sharedLocalId));
    } catch (MessageException e) {
      throw new RuntimeException("Failed to parse wave entity: " + entity, e);
    }
  }

  private WaveletName getWaveletName(RemoteConvWavelet w) {
    return WaveletName.of(WaveId.deserialise(w.getDigest().getWaveId()), w.getWaveletId());
  }

  private Entity serializeWavelet(StableUserId userId, RemoteConvWavelet w) {
    Key key = makeWaveletKey(userId, w.getSourceInstance(), getWaveletName(w));
    Entity e = new Entity(key);
    DatastoreUtil.setNonNullUnindexedProperty(e, WAVELET_DIGEST_PROPERTY,
        new Text(GsonProto.toJson((RobotSearchDigestGsonImpl) w.getDigest())));
    DatastoreUtil.setNonNullIndexedProperty(e, WAVELET_LAST_MODIFIED_MILLIS_PROPERTY,
        w.getDigest().getLastModifiedMillis());
    DatastoreUtil.setOrRemoveIndexedProperty(e, WAVELET_PRIVATE_LOCAL_ID_PROPERTY,
        w.getPrivateLocalId() == null ? null : w.getPrivateLocalId().getId());
    DatastoreUtil.setOrRemoveIndexedProperty(e, WAVELET_SHARED_LOCAL_ID_PROPERTY,
        w.getSharedLocalId() == null ? null : w.getSharedLocalId().getId());
    // Sanity check.
    Assert.check(w.equals(parseWaveletEntity(e)), "Serialized %s incorrectly: %s", w, e);
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
  public boolean addRemoteWavelets(
      CheckedTransaction tx, StableUserId userId,
      List<RemoteConvWavelet> convWavelets) throws RetryableFailure, PermanentFailure {
    log.info("Adding " + convWavelets.size() + " digests");
    List<Key> keys = Lists.newArrayList();
    for (RemoteConvWavelet convWavelet : convWavelets) {
      keys.add(
          makeWaveletKey(userId, convWavelet.getSourceInstance(), getWaveletName(convWavelet)));
    }
    Map<Key, Entity> existingEntities = tx.get(keys);
    Map<Key, Entity> entitiesToPut = Maps.newHashMapWithExpectedSize(convWavelets.size());
    for (RemoteConvWavelet convWavelet : convWavelets) {
      Key key =
          makeWaveletKey(userId, convWavelet.getSourceInstance(), getWaveletName(convWavelet));
      Entity existingEntity = existingEntities.get(key);
      if (existingEntity == null) {
        log.info("New wavelet: " + key);
        entitiesToPut.put(key, serializeWavelet(userId, convWavelet));
      } else {
        RemoteConvWavelet existing = parseWaveletEntity(existingEntity);
        Assert.check(convWavelet.getSourceInstance().equals(existing.getSourceInstance())
            && convWavelet.getWaveletId().equals(existing.getWaveletId()),
            "%s, %s", convWavelet, existing);
        RemoteConvWavelet merged = new RemoteConvWavelet(convWavelet.getSourceInstance(),
            convWavelet.getDigest(),
            convWavelet.getWaveletId(),
            existing.getPrivateLocalId(),
            existing.getSharedLocalId());
        if (merged.equals(existing)) {
          log.info("Wavelet unchanged, not putting: " + key + " " + convWavelet + " "
              + convWavelet.getDigest().getTitle());
        } else {
          // TODO(ohler): Fix PST protobuf toString() method to be meaningful.
          log.info("Updating existing wavelet " + key + ": " + existing
              + " updated with " + convWavelet + " becomes " + merged);
          entitiesToPut.put(key, serializeWavelet(userId, merged));
        }
      }
    }
    if (entitiesToPut.isEmpty()) {
      return false;
    }
    log.info("Putting " + entitiesToPut.size() + " entities: "
        + ValueUtils.abbrev("" + entitiesToPut, 500));
    tx.put(entitiesToPut.values());
    return true;
  }

  @Nullable public RemoteConvWavelet getWavelet(CheckedTransaction tx,
      StableUserId userId, SourceInstance instance, WaveletName waveletName)
      throws RetryableFailure, PermanentFailure {
    log.info("getWavelet(" + userId + ", " + instance + ", " + waveletName + ")");
    Entity entity = tx.get(makeWaveletKey(userId, instance, waveletName));
    RemoteConvWavelet wavelet = entity == null ? null : parseWaveletEntity(entity);
    log.info("Got " + wavelet);
    return wavelet;
  }

  @Nullable public void putWavelet(CheckedTransaction tx,
      StableUserId userId, RemoteConvWavelet wavelet) throws RetryableFailure, PermanentFailure {
    log.info("putWavelet(" + userId + ", " + wavelet + ")");
    Entity entity = serializeWavelet(userId, wavelet);
    log.info("Putting " + wavelet);
    tx.put(entity);
  }

  // TODO(ohler): make this scalable, e.g. by adding pagination
  public List<RemoteConvWavelet> getAllWavelets(CheckedTransaction tx, StableUserId userId)
      throws RetryableFailure, PermanentFailure {
    log.info("getAllWaves(" + userId + ")");
    CheckedIterator i = tx.prepare(new Query(WAVELET_ENTITY_KIND)
        .setAncestor(makeRootKey(userId))
        .addSort(WAVELET_LAST_MODIFIED_MILLIS_PROPERTY, Query.SortDirection.DESCENDING))
        .asIterator();
    ImmutableList.Builder<RemoteConvWavelet> b = ImmutableList.builder();
    while (i.hasNext()) {
      b.add(parseWaveletEntity(i.next()));
    }
    List<RemoteConvWavelet> out = b.build();
    log.info("Got " + out.size() + " wavelets");
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
    return deleteAll(tx, new Query(WAVELET_ENTITY_KIND).setAncestor(makeRootKey(userId)));
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
    log.info("Got " + out.size() + " tasks");
    return out;
  }

  @Nullable public ImportTask getTask(CheckedTransaction tx, StableUserId userId, long taskId)
      throws RetryableFailure, PermanentFailure {
    log.info("getTask(" + userId + ", " + taskId + ")");
    Entity entity = tx.get(makeTaskKey(userId, taskId));
    ImportTask task = entity == null ? null : parseTaskEntity(entity);
    log.info("Got " + task);
    return task;
  }

  public void deleteTask(CheckedTransaction tx, StableUserId userId, long taskId)
      throws RetryableFailure, PermanentFailure {
    log.info("deleteTask(" + userId + ", " + taskId + ")");
    tx.delete(makeTaskKey(userId, taskId));
  }

}
