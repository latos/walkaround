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

package com.google.walkaround.wave.server.conv;

import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.walkaround.slob.server.AccessChecker;
import com.google.walkaround.slob.server.PreCommitHook;
import com.google.walkaround.slob.server.StoreModuleHelper;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.SlobModel;
import com.google.walkaround.slob.shared.SlobModel.ReadableSlob;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.wave.server.conv.PermissionCache.PermissionSource;
import com.google.walkaround.wave.server.index.IndexTaskHandler;
import com.google.walkaround.wave.server.model.WaveObjectStoreModel;
import com.google.walkaround.wave.server.model.WaveObjectStoreModel.ReadableWaveletObject;
import com.google.walkaround.wave.server.wavemanager.WaveIndex;
import com.google.walkaround.wave.server.wavemanager.WaveManager;

import java.util.logging.Logger;

/**
 * Guice module that configures an object store for conversation wavelets.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ConvStoreModule extends PrivateModule {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ConvStoreModule.class.getName());

  @Override protected void configure() {
    StoreModuleHelper.makeBasicBindingsAndExposures(binder(), ConvStore.class);
    // Perhaps a better name would be "Conv", but we can't change it, for
    // compatibility with existing data.
    StoreModuleHelper.bindEntityKinds(binder(), "Wavelet");

    bind(SlobModel.class).to(WaveObjectStoreModel.class);
    bind(AccessChecker.class).to(ConvAccessChecker.class);
    bind(PermissionSource.class).to(WaveManager.class);
    final Provider<WaveIndex> index = getProvider(WaveIndex.class);
    bind(PreCommitHook.class).toInstance(
        new PreCommitHook() {
          @Override public void run(CheckedTransaction tx, SlobId objectId,
              long resultingVersion, ReadableSlob resultingState)
              throws RetryableFailure, PermanentFailure {
            // TODO(ohler): Introduce generics in SlobModel to avoid the cast.
            index.get().update(tx, objectId, (ReadableWaveletObject) resultingState);
            IndexTaskHandler.scheduleIndex(objectId);
          }
        });
  }

}
