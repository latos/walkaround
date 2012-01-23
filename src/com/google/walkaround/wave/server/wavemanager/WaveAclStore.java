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

package com.google.walkaround.wave.server.wavemanager;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.walkaround.slob.server.SlobFacilities;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.DatastoreUtil;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.conv.ConvStore;
import com.google.walkaround.wave.server.model.WaveObjectStoreModel.ReadableWaveletObject;

import org.waveprotocol.wave.model.wave.ParticipantId;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Manages ACL storage.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveAclStore {

  // We want one entity group per wave.  We use the same entity group as the
  // mutation log so that we can transactionally update a conv wavelet and the
  // ACL, since we sync that from the participant list.  The problem may look
  // similar to indexing at first, but delays would be much more irritating, and
  // the fan-out is smaller (no group expansion).

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(WaveAclStore.class.getName());

  static class AclEntry {
    private final SlobId objectId;
    private final ParticipantId creator; // This field might not be needed, but keeping it for now.
    private final Set<ParticipantId> acl;

    public AclEntry(SlobId objectId,
        ParticipantId creator,
        Set<ParticipantId> acl) {
      this.objectId = checkNotNull(objectId, "Null objectId");
      this.creator = checkNotNull(creator, "Null creator");
      this.acl = checkNotNull(acl, "Null acl");
    }

    public SlobId getObjectId() {
      return objectId;
    }

    public ParticipantId getCreator() {
      return creator;
    }

    public Set<ParticipantId> getAcl() {
      return acl;
    }

    @Override public String toString() {
      return "IndexEntry("
          + objectId + ", "
          + creator + ", "
          + acl
          + ")";
    }

    @Override public final boolean equals(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof AclEntry)) { return false; }
      AclEntry other = (AclEntry) o;
      return Objects.equal(objectId, other.objectId)
          && Objects.equal(creator, other.creator)
          && Objects.equal(acl, other.acl);
    }

    @Override public final int hashCode() {
      return Objects.hashCode(objectId, creator, acl);
    }
  }

  private static final String INDEX_ENTITY_KIND = "ConvIndex";
  private static final String INDEX_KEY = "index";
  private static final String CREATOR_PROPERTY = "creator";
  private static final String ACL_PROPERTY = "p"; // for "participants"

  private final SlobFacilities facilities;

  @Inject public WaveAclStore(@ConvStore SlobFacilities facilities) {
    this.facilities = facilities;
  }

  private Key makeKey(SlobId objectId) {
    return KeyFactory.createKey(facilities.makeRootEntityKey(objectId),
        INDEX_ENTITY_KIND, INDEX_KEY);
  }

  private Entity makeEntity(AclEntry entry) {
    // Can't truncate the strings in this function since that would mean
    // parseEntity() is no longer its inverse.
    Entity entity = new Entity(makeKey(entry.getObjectId()));
    ImmutableList.Builder<String> b = ImmutableList.builder();
    for (ParticipantId user : entry.getAcl()) {
      b.add(user.getAddress());
    }
    entity.setProperty(ACL_PROPERTY, b.build());
    DatastoreUtil.setNonNullIndexedProperty(entity, CREATOR_PROPERTY,
        entry.getCreator().getAddress());
    Assert.check(entry.equals(parseEntity(entity)), "Mismatch serializing %s: %s", entry, entity);
    return entity;
  }

  private AclEntry parseEntity(Entity entity) {
    SlobId objectId = new SlobId(entity.getKey().getParent().getName());
    @SuppressWarnings("unchecked") // We only put in List<String>
    Collection<String> rawIds = (Collection<String>) entity.getProperty(ACL_PROPERTY);
    Set<ParticipantId> acl;
    if (rawIds == null) {
      acl = Collections.emptySet();
    } else {
      ImmutableSortedSet.Builder<ParticipantId> b = ImmutableSortedSet.naturalOrder();
      for (String id : rawIds) {
        b.add(ParticipantId.ofUnsafe(id));
      }
      acl = b.build();
    }
    return new AclEntry(
        objectId,
        ParticipantId.ofUnsafe(
            DatastoreUtil.getExistingProperty(entity, CREATOR_PROPERTY, String.class)),
        acl);
  }

  private void update(CheckedTransaction tx, SlobId objectId, AclEntry entry)
      throws RetryableFailure, PermanentFailure {
    log.info("Updating index for " + objectId + ": " + entry);
    tx.put(makeEntity(entry));
    // TODO(ohler): Make the ACL cache consistent with the datastore.
  }

  public void update(CheckedTransaction tx, SlobId objectId, ReadableWaveletObject convState)
      throws RetryableFailure, PermanentFailure {
    update(tx, objectId,
        new AclEntry(objectId, convState.getCreator(),
            Sets.newHashSet(convState.getParticipants())));
  }

  @Nullable public AclEntry getEntry(CheckedTransaction tx, SlobId objectId)
      throws RetryableFailure, PermanentFailure {
    Entity entity = tx.get(makeKey(objectId));
    AclEntry result = entity == null ? null : parseEntity(entity);
    log.info("Index entry for " + objectId + ": " + result);
    return result;
  }
}
