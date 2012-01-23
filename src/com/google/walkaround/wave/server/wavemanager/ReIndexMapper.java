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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.inject.Inject;
import com.google.walkaround.proto.WaveletMetadata;
import com.google.walkaround.proto.gson.WaveletMetadataGsonImpl;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.server.MutationLog;
import com.google.walkaround.slob.server.SlobFacilities;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.StateAndVersion;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.wave.server.GuiceSetup;
import com.google.walkaround.wave.server.conv.ConvStore;
import com.google.walkaround.wave.server.model.WaveObjectStoreModel.ReadableWaveletObject;

import org.apache.hadoop.io.NullWritable;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Mapreduce mapper that re-indexes and re-extracts ACL metadata from all
 * conversations.
 *
 * TODO(danilatos): Add re-indexing.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ReIndexMapper extends AppEngineMapper<Key, Entity, NullWritable, NullWritable> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ReIndexMapper.class.getName());

  private static class Handler {
    @Inject CheckedDatastore datastore;
    @Inject @ConvStore SlobFacilities facilities;
    @Inject WaveAclStore aclStore;

    void process(Context context, final Key key) throws PermanentFailure {
      new RetryHelper().run(new RetryHelper.VoidBody() {
          @Override public void run() throws PermanentFailure, RetryableFailure {
            CheckedTransaction tx = datastore.beginTransaction();
            try {
              SlobId objectId = facilities.parseRootEntityKey(key);
              MutationLog mutationLog = facilities.getMutationLogFactory().create(tx, objectId);
              try {
                WaveletMetadata metadata = GsonProto.fromGson(
                    new WaveletMetadataGsonImpl(), mutationLog.getMetadata());
                if (metadata.getType() == WaveletMetadata.Type.UDW) {
                  log.info("Skipping UDW " + objectId);
                  return;
                }
              } catch (MessageException e) {
                throw new RuntimeException("Failed to parse metadata for " + objectId, e);
              }
              StateAndVersion state = mutationLog.reconstruct(null);
              log.info("Re-indexing " + objectId + " at version " + state.getVersion());
              aclStore.update(tx, objectId, (ReadableWaveletObject) state.getState());
              tx.commit();
            } finally {
              tx.close();
            }
          }
        });
    }
  }

  @Override
  public void map(Key key, Entity value, Context context) throws IOException {
    context.getCounter(getClass().getSimpleName(), "entities-seen").increment(1);
    log.info("Re-indexing " + key);
    try {
      GuiceSetup.getInjectorForTaskQueueTask().getInstance(Handler.class).process(context, key);
    } catch (PermanentFailure e) {
      throw new IOException("PermanentFailure re-indexing key " + key, e);
    }
    context.getCounter(getClass().getSimpleName(), "entities-processed").increment(1);
  }

}
