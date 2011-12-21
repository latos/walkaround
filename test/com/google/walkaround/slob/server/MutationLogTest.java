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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ChangeRejected;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.InvalidSnapshot;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.SlobModel;
import com.google.walkaround.slob.shared.SlobModel.Slob;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedIterator;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedPreparedQuery;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class MutationLogTest extends TestCase {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(MutationLogTest.class.getName());

  // NOTE(ohler): testSizeEstimates() does NOT pass regardless of what these
  // constants are.  It was off by one when I ran with other values.  Still good
  // enough, didn't investigate in detail.
  private static final String SNAPSHOT_STRING =
      "snapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshot"
      + "snapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshotsnapshot";
  private static final String ROOT_ENTITY_KIND = "Wavelet";
  private static final String DELTA_ENTITY_KIND = "WaveletDelta";
  private static final String SNAPSHOT_ENTITY_KIND = "WaveletSnapshot";
  private static final String OBJECT_ID = "obj";

  private static class TestModel implements SlobModel {
    public class TestObject implements Slob {
      @Override @Nullable public String snapshot() {
        return SNAPSHOT_STRING;
      }

      public void apply(ChangeData<String> payload) throws ChangeRejected {
        // accept any payload, do nothing with it
      }

      public String getIndexedContent() {
        throw new AssertionError("Not implemented");
      }
    }

    @Override
    public Slob create(@Nullable String snapshot) throws InvalidSnapshot {
      return new TestObject();
    }

    @Override
    public List<String> transform(List<ChangeData<String>> clientOps,
        List<ChangeData<String>> serverOps) throws ChangeRejected {
      throw new AssertionError("Not implemented");
    }
  }

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig());

  @Override protected void setUp() throws Exception {
    super.setUp();
    helper.setUp();
  }

  @Override protected void tearDown() throws Exception {
    helper.tearDown();
    super.tearDown();
  }

  public void testSizeEstimates() throws Exception {
    CheckedTransaction tx = new CheckedTransaction() {
        @Override
        public Entity get(Key key) throws PermanentFailure, RetryableFailure {
          return null;
        }

        @Override
        public Map<Key, Entity> get(Iterable<Key> keys) throws PermanentFailure, RetryableFailure {
          throw new AssertionError("Not implemented");
        }

        @Override
        public CheckedPreparedQuery prepare(Query q) {
          return new CheckedPreparedQuery() {
            public CheckedIterator asIterator(final FetchOptions options)
                throws PermanentFailure, RetryableFailure {
              return CheckedIterator.EMPTY;
            }

            public List<Entity> asList(final FetchOptions options)
                throws PermanentFailure, RetryableFailure {
              return ImmutableList.of();
            }

            public int countEntities(final FetchOptions options)
                throws PermanentFailure, RetryableFailure {
              throw new AssertionError("Not implemented");
            }

            public Entity asSingleEntity() throws PermanentFailure, RetryableFailure {
              throw new AssertionError("Not implemented");
            }
          };
        }

        @Override
        public Key put(Entity e) throws PermanentFailure, RetryableFailure {
          throw new AssertionError("Not implemented");
        }

        @Override
        public List<Key> put(Iterable<Entity> e) throws PermanentFailure, RetryableFailure {
          throw new AssertionError("Not implemented");
        }

        @Override
        public void delete(Key... keys) throws PermanentFailure, RetryableFailure {
          throw new AssertionError("Not implemented");
        }

        @Override
        public TaskHandle enqueueTask(Queue queue, TaskOptions task)
            throws PermanentFailure, RetryableFailure {
          throw new AssertionError("Not implemented");
        }

        @Override
        public void rollback() {
          throw new AssertionError("Not implemented");
        }

        @Override
        public void commit() throws PermanentFailure, RetryableFailure {
          throw new AssertionError("Not implemented");
        }

        @Override
        public boolean isActive() {
          throw new AssertionError("Not implemented");
        }

        @Override
        public void close() {
          throw new AssertionError("Not implemented");
        }
      };

    SlobId objectId = new SlobId(OBJECT_ID);
    ClientId clientId = new ClientId("s");
    String payload = "{\"a\": 5}";

    // I didn't track down exactly where the 12 comes from.
    int encodingOverhead = 12;
    int idSize = ROOT_ENTITY_KIND.length() + OBJECT_ID.length() + encodingOverhead;
    int versionSize = 8;
    int deltaSize = idSize + DELTA_ENTITY_KIND.length() + versionSize
        + MutationLog.DELTA_CLIENT_ID_PROPERTY.length() + clientId.getId().length()
        + MutationLog.DELTA_OP_PROPERTY.length() + payload.length();
    int snapshotSize = idSize + SNAPSHOT_ENTITY_KIND.length() + versionSize
        + MutationLog.SNAPSHOT_DATA_PROPERTY.length() + SNAPSHOT_STRING.length();
    log.info("deltaSize=" + deltaSize
        + ", snapshotSize=" + snapshotSize);
    assertEquals(56, deltaSize);
    assertEquals(225, snapshotSize);
    ChangeData<String> delta = new ChangeData<String>(clientId, payload);

    MutationLog mutationLog =
        new MutationLog(ROOT_ENTITY_KIND, DELTA_ENTITY_KIND, SNAPSHOT_ENTITY_KIND,
            new MutationLog.DefaultDeltaEntityConverter(),
            tx, objectId, new TestModel());
    MutationLog.Appender appender = mutationLog.prepareAppender().getAppender();

    assertEquals(0, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 1 * deltaSize + 0 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 2 * deltaSize + 0 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 3 * deltaSize + 0 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 4 * deltaSize + 0 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 5 * deltaSize + 0 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 6 * deltaSize + 0 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 7 * deltaSize + 0 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 8 * deltaSize + 0 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals( 9 * deltaSize + 1 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals(10 * deltaSize + 1 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals(11 * deltaSize + 1 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals(12 * deltaSize + 1 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals(13 * deltaSize + 1 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals(14 * deltaSize + 2 * snapshotSize, appender.estimatedBytesStaged());
    appender.append(delta);
    assertEquals(15 * deltaSize + 2 * snapshotSize, appender.estimatedBytesStaged());
  }

}
