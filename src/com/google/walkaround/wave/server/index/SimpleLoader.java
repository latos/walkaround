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

package com.google.walkaround.wave.server.index;

import com.google.inject.Inject;
import com.google.walkaround.slob.server.MutationLog;
import com.google.walkaround.slob.server.MutationLog.MutationLogFactory;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.StateAndVersion;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.wave.server.conv.ConvStore;

import java.io.IOException;

/**
 * Helper class for loading slobs.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class SimpleLoader {
  private final CheckedDatastore datastore;
  private final MutationLogFactory mutationLogFactory;

  @Inject
  public SimpleLoader(CheckedDatastore datastore,
      // HACK(danilatos): conv store is hard coded.
      @ConvStore MutationLogFactory mutationLogFactory) {
    this.datastore = datastore;
    this.mutationLogFactory = mutationLogFactory;
  }

  public StateAndVersion load(SlobId id) throws IOException {
    try {
      CheckedTransaction tx = datastore.beginTransaction();
      try {
        MutationLog l = mutationLogFactory.create(tx, id);
        return l.reconstruct(null);
      } finally {
        tx.rollback();
      }
    } catch (PermanentFailure e) {
      throw new IOException(e);
    } catch (RetryableFailure e) {
      throw new IOException(e);
    }
  }
}
