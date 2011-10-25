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

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;

import junit.framework.TestCase;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveletDirectoryTest extends TestCase {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalMemcacheServiceTestConfig(),
          new LocalDatastoreServiceTestConfig());

  // NOTE(ohler): For historical reasons, some of these tests (like
  // testRegisterWritesToStorage) cover both WaveletDirectory and
  // AbstractDirectory.  Perhaps it would be better to test AbstractDirectory
  // in isolation, and use a mock AbstractDirectory to test WaveletDirectory.

  private static final SlobId OBJECT_ID = new SlobId("objectidfortest");
  private static final WaveletMapping MAPPING = new WaveletMapping(OBJECT_ID);

  @Override protected void setUp() throws Exception {
    super.setUp();
    helper.setUp();
  }

  @Override protected void tearDown() throws Exception {
    helper.tearDown();
    super.tearDown();
  }

  public void testRegisterWritesToStorage() throws Exception {
    CheckedDatastore datastore = newDatastore();
    WaveletDirectory directory = new WaveletDirectory(datastore,
        MemcacheServiceFactory.getMemcacheService());

    if (get(datastore, directory.directory.makeKey(OBJECT_ID)) != null) {
      fail();
    }

    directory.register(MAPPING);
    assertEquals(OBJECT_ID,
        directory.directory.parse(get(datastore, directory.directory.makeKey(OBJECT_ID)))
        .getObjectId());
  }

  public void testRegisterAddsToCache() throws Exception {
    WaveletDirectory directory = new WaveletDirectory(newDatastore(),
        MemcacheServiceFactory.getMemcacheService());

    assertNull(directory.cache.get(OBJECT_ID));
    directory.register(MAPPING);
    assertEquals(OBJECT_ID, directory.cache.get(OBJECT_ID).getCached().getObjectId());
  }

  public void testRegisterOverridesNegativeCache() throws Exception {
    WaveletDirectory directory = new WaveletDirectory(newDatastore(),
        MemcacheServiceFactory.getMemcacheService());

    assertNull(directory.cache.get(OBJECT_ID));
    assertEquals(null, directory.lookup(OBJECT_ID));
    assertEquals(null, directory.cache.get(OBJECT_ID).getCached());
    directory.register(MAPPING);
    assertEquals(OBJECT_ID, directory.cache.get(OBJECT_ID).getCached().getObjectId());
  }

  public void testSuccessfulLookup() throws Exception {
    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
    WaveletDirectory directory = new WaveletDirectory(newDatastore(), memcache);

    directory.register(MAPPING);
    memcache.clearAll();
    assertNull(directory.cache.get(OBJECT_ID));
    assertEquals(OBJECT_ID, directory.lookup(OBJECT_ID).getObjectId());
    assertEquals(OBJECT_ID, directory.cache.get(OBJECT_ID).getCached().getObjectId());
  }

  public void testFailedLookup() throws Exception {
    WaveletDirectory directory = new WaveletDirectory(newDatastore(),
        MemcacheServiceFactory.getMemcacheService());
    assertNull(directory.cache.get(OBJECT_ID));
    assertEquals(null, directory.lookup(OBJECT_ID));
    assertEquals(null, directory.cache.get(OBJECT_ID).getCached());
  }

  public void testLookupUsesCache() throws Exception {
    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
    WaveletDirectory directory = new WaveletDirectory(newDatastore(), memcache);

    directory.cache.put(OBJECT_ID, new WaveletDirectory.CacheEntry(MAPPING));
    assertEquals(OBJECT_ID, directory.lookup(OBJECT_ID).getObjectId());
    directory.cache.put(OBJECT_ID, new WaveletDirectory.CacheEntry(null));
    assertEquals(null, directory.lookup(OBJECT_ID));
  }

  private static CheckedDatastore newDatastore() {
    return new CheckedDatastore(DatastoreServiceFactory.getDatastoreService());
  }

  private static Entity get(CheckedDatastore store, Key key)
      throws PermanentFailure, RetryableFailure {
    CheckedTransaction tx = store.beginTransaction();
    try {
      return tx.get(key);
    } finally {
      tx.rollback();
    }
  }

}
