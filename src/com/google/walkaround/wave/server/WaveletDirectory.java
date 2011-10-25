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

package com.google.walkaround.wave.server;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.appengine.AbstractDirectory;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.DatastoreUtil.InvalidPropertyException;
import com.google.walkaround.util.server.appengine.MemcacheTable;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Interface to the wavelet directory in the datastore.
 *
 * Directory entries are also cached in memcache.  Wavelet ids that were looked
 * up but didn't exist are also cached to avoid paying a high cost if someone
 * repeatedly requests the same bogus wavelet id.  The cache does not help with
 * rapid requests of different bogus (or valid) wavelet ids, but at least those
 * will be distributed across entity groups.
 *
 * @author ohler@google.com (Christian Ohler)
 */
// Like WaveletMapping, this class is rather degenerate right now: The directory
// is nothing but a set of ObjectIds, and the memcache is rather pointless.
// However, once we add the mapping of legacy WaveletName to SlobId, we'll
// have a bit more logic in here again.
public class WaveletDirectory {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(WaveletDirectory.class.getName());

  /**
   * Thrown when an attempt is made to register a wavelet that is already known in
   * the wavelet directory.
   *
   * @author ohler@google.com (Christian Ohler)
   */
  public class ObjectIdAlreadyKnown extends Exception {
    private final WaveletMapping existingMapping;

    public ObjectIdAlreadyKnown(WaveletMapping existingMapping) {
      this(existingMapping, null, null);
    }

    public ObjectIdAlreadyKnown(WaveletMapping existingMapping, String message) {
      this(existingMapping, message, null);
    }

    public ObjectIdAlreadyKnown(WaveletMapping existingMapping, Throwable cause) {
      this(existingMapping, null, cause);
    }

    public ObjectIdAlreadyKnown(WaveletMapping existingMapping,
        String message, Throwable cause) {
      super(message, cause);
      Preconditions.checkNotNull(existingMapping, "Null existingMapping");
      this.existingMapping = existingMapping;
    }

    public WaveletMapping getExistingMapping() {
      return existingMapping;
    }
  }

  @VisibleForTesting
  static class CacheEntry implements Serializable {
    @Nullable private final WaveletMapping cached;

    public CacheEntry(@Nullable WaveletMapping cached) {
      this.cached = cached;
    }

    /** Null if wavelet does not exist. */
    @Nullable public WaveletMapping getCached() {
      return cached;
    }

    @Override public String toString() {
      return "CacheEntry(" + cached + ")";
    }
  }

  @VisibleForTesting
  static class Directory extends AbstractDirectory<WaveletMapping, SlobId> {
    Directory(CheckedDatastore datastore) {
      super(datastore, "WaveletDirectoryEntry");
    }

    @Override protected String serializeId(SlobId id) {
      return id.getId();
    }

    @Override protected SlobId getId(WaveletMapping mapping) {
      return mapping.getObjectId();
    }

    @Override protected void populateEntity(WaveletMapping mapping, Entity out) {
    }

    @Override protected WaveletMapping parse(Entity e) throws InvalidPropertyException {
      return new WaveletMapping(new SlobId(e.getKey().getName()));
    }
  }

  private static final String MEMCACHE_TAG = "W";

  @VisibleForTesting
  final Directory directory;
  @VisibleForTesting
  final MemcacheTable<SlobId, CacheEntry> cache;

  @Inject
  public WaveletDirectory(CheckedDatastore datastore, MemcacheService memcache) {
    this.directory = new Directory(datastore);
    this.cache = new MemcacheTable<SlobId, CacheEntry>(memcache, MEMCACHE_TAG);
  }

  /**
   * Returns null if object id is not known.  (This doesn't mean the id is not
   * assigned; it may be assigned in the object store, which is authoritative,
   * but not in the directory.)
   * @throws IOException
   */
  @Nullable public WaveletMapping lookup(SlobId objectId) throws IOException {
    CacheEntry cached = cache.get(objectId);
    if (cached != null) {
      return cached.getCached();
    }
    WaveletMapping result = directory.get(objectId);
    if (result != null) {
      cache.put(objectId, new CacheEntry(result));
      return result;
    } else {
      // We have to use ADD_ONLY_IF_NOT_PRESENT since there may be a concurrent
      // register() also trying to set the value, and that has to take priority.
      cache.put(objectId, new CacheEntry(null), null,
          MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
      return null;
    }
  }

  public void register(WaveletMapping newMapping) throws IOException, ObjectIdAlreadyKnown {
    Preconditions.checkNotNull(newMapping, "Null newMapping");
    // We do a cache lookup here to detect inconsistencies (and perhaps to catch
    // bugs that create the same wavelet more than once cheaper than the
    // datastore could do it).  It's not strictly necessary, but the datastore
    // access that follows is much more expensive anyway, so we don't mind the
    // extra cache lookup.
    CacheEntry cached = cache.get(newMapping.getObjectId());
    if (cached != null && cached.getCached() != null) {
      throw new ObjectIdAlreadyKnown(cached.getCached(),
          "Attempt to register " + newMapping
          + ", but object already exists (cached = " + cached + ")");
    }

    log.info("About to register " + newMapping);
    WaveletMapping existingMapping = directory.getOrAdd(newMapping);
    log.info("Existing mapping: " + existingMapping);
    if (existingMapping != null) {
      throw new ObjectIdAlreadyKnown(existingMapping,
          "Attempt to register " + newMapping + ", but object already exists: " + existingMapping
          + " (cached = " + cached + ")");
    }
    cache.put(newMapping.getObjectId(), new CacheEntry(newMapping));
  }

  public List<WaveletMapping> hackReadAllMappings() {
    return directory.hackReadAllEntries();
  }

}
