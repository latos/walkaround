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
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.walkaround.slob.server.SlobFacilities;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedIterator;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.appengine.DatastoreUtil;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.conv.ConvStore;
import com.google.walkaround.wave.server.model.WaveObjectStoreModel.ReadableWaveletObject;

import org.waveprotocol.wave.model.util.ValueUtils;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Manages indexing and ACL storage.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveIndex {

  // We want one entity group per wave.  We use the same entity group as the
  // mutation log so that we can transactionally update a conv wavelet and the
  // ACL, since we sync that from the participant list.  The problem may look
  // similar to indexing at first, but delays would be much more irritating, and
  // the fan-out is smaller (no group expansion).

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(WaveIndex.class.getName());

  static class IndexEntry {
    private final SlobId objectId;
    private final ParticipantId creator;
    private final String title;
    private final String snippet;
    private final long lastModifiedMillis;
    private final Set<ParticipantId> acl;

    public IndexEntry(SlobId objectId,
        ParticipantId creator,
        String title,
        String snippet,
        long lastModifiedMillis,
        Set<ParticipantId> acl) {
      this.objectId = checkNotNull(objectId, "Null objectId");
      this.creator = checkNotNull(creator, "Null creator");
      this.title = checkNotNull(title, "Null title");
      this.snippet = checkNotNull(snippet, "Null snippet");
      this.lastModifiedMillis = lastModifiedMillis;
      this.acl = checkNotNull(acl, "Null acl");
    }

    public SlobId getObjectId() {
      return objectId;
    }

    public ParticipantId getCreator() {
      return creator;
    }

    public String getTitle() {
      return title;
    }

    public String getSnippet() {
      return snippet;
    }

    public long getLastModifiedMillis() {
      return lastModifiedMillis;
    }

    public Set<ParticipantId> getAcl() {
      return acl;
    }

    @Override public String toString() {
      return "IndexEntry("
          + objectId + ", "
          + creator + ", "
          + title + ", "
          + snippet + ", "
          + lastModifiedMillis + ", "
          + acl
          + ")";
    }

    @Override public final boolean equals(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof IndexEntry)) { return false; }
      IndexEntry other = (IndexEntry) o;
      return lastModifiedMillis == other.lastModifiedMillis
          && Objects.equal(objectId, other.objectId)
          && Objects.equal(creator, other.creator)
          && Objects.equal(title, other.title)
          && Objects.equal(snippet, other.snippet)
          && Objects.equal(acl, other.acl);
    }

    @Override public final int hashCode() {
      return Objects.hashCode(objectId, creator, title, snippet, lastModifiedMillis, acl);
    }
  }

  private static final String INDEX_ENTITY_KIND = "ConvIndex";
  private static final String INDEX_KEY = "index";
  private static final String CREATOR_PROPERTY = "creator";
  private static final String TITLE_PROPERTY = "title";
  private static final String SNIPPET_PROPERTY = "snippet";
  private static final String LAST_MODIFIED_MILLIS_PROPERTY = "lastModified";
  private static final String ACL_PROPERTY = "p"; // for "participants"

  public static final int MAX_TITLE_CHARS = 300;
  public static final int MAX_SNIPPET_CHARS = 300;

  private final CheckedDatastore datastore;
  private final SlobFacilities facilities;

  @Inject public WaveIndex(CheckedDatastore datastore,
      @ConvStore SlobFacilities facilities) {
    this.datastore = datastore;
    this.facilities = facilities;
  }

  private Key makeKey(SlobId objectId) {
    return KeyFactory.createKey(facilities.makeRootEntityKey(objectId),
        INDEX_ENTITY_KIND, INDEX_KEY);
  }

  private Entity makeEntity(IndexEntry entry) {
    // Can't truncate the strings in this function since that would mean
    // parseEntity() is no longer its inverse.
    Preconditions.checkArgument(entry.getTitle().length() <= MAX_TITLE_CHARS,
        "Title too long: " + entry);
    Preconditions.checkArgument(entry.getSnippet().length() <= MAX_SNIPPET_CHARS,
        "Snippet too long: " + entry);
    Entity entity = new Entity(makeKey(entry.getObjectId()));
    ImmutableList.Builder<String> b = ImmutableList.builder();
    for (ParticipantId user : entry.getAcl()) {
      b.add(user.getAddress());
    }
    entity.setProperty(ACL_PROPERTY, b.build());
    DatastoreUtil.setNonNullIndexedProperty(entity, CREATOR_PROPERTY,
        entry.getCreator().getAddress());
    DatastoreUtil.setNonNullIndexedProperty(entity, TITLE_PROPERTY, entry.getTitle());
    DatastoreUtil.setNonNullIndexedProperty(entity, SNIPPET_PROPERTY, entry.getSnippet());
    DatastoreUtil.setNonNullIndexedProperty(entity, LAST_MODIFIED_MILLIS_PROPERTY,
        entry.getLastModifiedMillis());
    Assert.check(entry.equals(parseEntity(entity)), "Mismatch serializing %s: %s", entry, entity);
    return entity;
  }

  private IndexEntry parseEntity(Entity entity) {
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
    return new IndexEntry(
        objectId,
        ParticipantId.ofUnsafe(
            DatastoreUtil.getExistingProperty(entity, CREATOR_PROPERTY, String.class)),
        DatastoreUtil.getExistingProperty(entity, TITLE_PROPERTY, String.class),
        DatastoreUtil.getExistingProperty(entity, SNIPPET_PROPERTY, String.class),
        DatastoreUtil.getExistingProperty(entity, LAST_MODIFIED_MILLIS_PROPERTY, Long.class),
        acl);
  }

  // TODO(ohler): Take indexable text as an argument; writing indexable text
  // should only be part of this function if indexing can be done
  // transactionally, though.
  private void update(CheckedTransaction tx, SlobId objectId, IndexEntry entry)
      throws RetryableFailure, PermanentFailure {
    log.info("Updating index for " + objectId + ": " + entry);
    tx.put(makeEntity(entry));
    // TODO(ohler): Make the ACL cache consistent with the datastore.
  }

  public void update(CheckedTransaction tx, SlobId objectId, ReadableWaveletObject convState)
      throws RetryableFailure, PermanentFailure {
    update(tx, objectId,
        new IndexEntry(objectId, convState.getCreator(),
            ValueUtils.abbrev(convState.getTitle(), MAX_TITLE_CHARS),
            ValueUtils.abbrev(convState.getSnippet(), MAX_SNIPPET_CHARS),
            convState.getLastModifiedMillis(),
            Sets.newHashSet(convState.getParticipants())));
  }

  @Nullable public IndexEntry getEntry(CheckedTransaction tx, SlobId objectId)
      throws RetryableFailure, PermanentFailure {
    Entity entity = tx.get(makeKey(objectId));
    IndexEntry result = entity == null ? null : parseEntity(entity);
    log.info("Index entry for " + objectId + ": " + result);
    return result;
  }

  // TODO(ohler): make this scalable, e.g. by adding pagination
  public List<IndexEntry> getAllWaves(ParticipantId participantId)
      throws RetryableFailure, PermanentFailure {
    log.info("getAllWaves(" + participantId + ")");
    CheckedIterator i = datastore.prepareNontransactionalQuery(new Query(INDEX_ENTITY_KIND)
        .addFilter(ACL_PROPERTY, Query.FilterOperator.EQUAL, participantId.getAddress())
        .addSort(LAST_MODIFIED_MILLIS_PROPERTY, Query.SortDirection.DESCENDING))
        .asIterator();
    ImmutableList.Builder<IndexEntry> b = ImmutableList.builder();
    while (i.hasNext()) {
      b.add(parseEntity(i.next()));
    }
    List<IndexEntry> out = b.build();
    log.info("got " + out.size() + " waves");
    return out;
  }

}
