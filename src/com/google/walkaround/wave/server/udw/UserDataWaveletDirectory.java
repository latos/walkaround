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

package com.google.walkaround.wave.server.udw;

import com.google.appengine.api.datastore.Entity;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.appengine.AbstractDirectory;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.DatastoreUtil;
import com.google.walkaround.util.server.appengine.DatastoreUtil.InvalidPropertyException;
import com.google.walkaround.wave.server.auth.StableUserId;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class UserDataWaveletDirectory {

  private static class Key {
    private final SlobId convObjectId;
    private final StableUserId userId;

    public Key(SlobId convObjectId, StableUserId userId) {
      Preconditions.checkNotNull(convObjectId, "Null convObjectId");
      Preconditions.checkNotNull(userId, "Null userId");
      Preconditions.checkArgument(!convObjectId.getId().contains(" "),
          "Invalid convObjectId: %s", convObjectId);
      Preconditions.checkArgument(!userId.getId().contains(" "),
          "Invalid userId: %s", userId);
      this.convObjectId = convObjectId;
      this.userId = userId;
    }

    public SlobId getConvObjectId() {
      return convObjectId;
    }

    public StableUserId getUserId() {
      return userId;
    }

    @Override public String toString() {
      return "Key(" + convObjectId + ", " + userId + ")";
    }

    @Override public boolean equals(Object o) {
      if (o == this) { return true; }
      if (o == null) { return false; }
      if (!(o.getClass() == Key.class)) { return false; }
      Key other = (Key) o;
      return convObjectId.equals(other.convObjectId)
          && userId.equals(other.userId);
    }

    @Override public int hashCode() {
      return Objects.hashCode(Key.class, convObjectId, userId);
    }
  }

  private static class Entry {
    private final Key key;
    private final SlobId udwId;

    public Entry(Key key, SlobId udwId) {
      Preconditions.checkNotNull(key, "Null key");
      Preconditions.checkNotNull(udwId, "Null udwId");
      this.key = key;
      this.udwId = udwId;
    }

    public Key getKey() {
      return key;
    }

    public SlobId getUdwId() {
      return udwId;
    }

    @Override public String toString() {
      return "Entry(" + key + ", " + udwId + ")";
    }

    @Override public boolean equals(Object o) {
      if (o == this) { return true; }
      if (o == null) { return false; }
      if (!(o.getClass() == Entry.class)) { return false; }
      Entry other = (Entry) o;
      return key.equals(other.key)
          && udwId.equals(other.udwId);
    }

    @Override public int hashCode() {
      return Objects.hashCode(Entry.class, key, udwId);
    }
  }

  @VisibleForTesting
  static class Directory extends AbstractDirectory<Entry, Key> {
    private static final String UDW_ID_PROPERTY = "UdwId";

    Directory(CheckedDatastore datastore) {
      // Increased to 3 because the metadata format has changed.
      super(datastore, "UdwDirectoryEntry3");
    }

    @Override
    protected String serializeId(Key key) {
      return key.getConvObjectId().getId() + " " + key.getUserId().getId();
    }

    @Override
    protected Key getId(Entry entry) {
      return entry.getKey();
    }

    @Override
    protected void populateEntity(Entry entry, Entity out) {
      DatastoreUtil.setNonNullIndexedProperty(out, UDW_ID_PROPERTY, entry.getUdwId().getId());
    }

    private SlobId parseObjectId(Entity e, String propertyName, String objectIdStr) {
      return new SlobId(objectIdStr);
    }

    @Override
    protected Entry parse(Entity e) throws InvalidPropertyException {
      String key = e.getKey().getName();
      int space = key.indexOf(' ');
      if (space == -1 || space != key.lastIndexOf(' ')) {
        throw new InvalidPropertyException(e, "key");
      }
      SlobId convObjectId = parseObjectId(e, "key", key.substring(0, space));
      StableUserId userId = new StableUserId(key.substring(space + 1));
      SlobId udwId = parseObjectId(e, UDW_ID_PROPERTY,
          DatastoreUtil.getExistingProperty(e, UDW_ID_PROPERTY, String.class));
      return new Entry(new Key(convObjectId, userId), udwId);
    }
  }

  private final Directory directory;

  @Inject
  public UserDataWaveletDirectory(CheckedDatastore datastore) {
    this.directory = new Directory(datastore);
  }

  @Nullable public SlobId getUdwId(SlobId convObjectId, StableUserId userId) throws IOException {
    Preconditions.checkNotNull(convObjectId, "Null convObjectId");
    Preconditions.checkNotNull(userId, "Null userId");
    Entry e = directory.get(new Key(convObjectId, userId));
    return e == null ? null : e.getUdwId();
  }

  public SlobId getOrAdd(SlobId convObjectId, StableUserId userId, SlobId udwId)
      throws IOException {
    Preconditions.checkNotNull(convObjectId, "Null convObjectId");
    Preconditions.checkNotNull(userId, "Null userId");
    Preconditions.checkNotNull(udwId, "Null udwId");
    Entry newEntry = new Entry(new Key(convObjectId, userId), udwId);
    Entry existingEntry = directory.getOrAdd(newEntry);
    if (existingEntry == null) {
      return null;
    } else {
      return existingEntry.getUdwId();
    }
  }

}
