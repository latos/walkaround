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

import com.google.inject.Inject;
import com.google.walkaround.slob.server.PreCommitHook;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.SlobModel.ReadableSlob;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.wave.server.model.WaveObjectStoreModel.ReadableWaveletObject;
import com.google.walkaround.wave.server.wavemanager.WaveIndex;

/**
 * {@link PreCommitHook} for the conversation wavelet store.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ConvPreCommitHook implements PreCommitHook {

  private final WaveIndex index;

  @Inject public ConvPreCommitHook(WaveIndex index) {
    this.index = index;
  }

  @Override public void run(CheckedTransaction tx, SlobId objectId,
      long resultingVersion, ReadableSlob resultingState)
      throws RetryableFailure, PermanentFailure {
    // TODO(ohler): Use generics to avoid the cast.
    index.update(tx, objectId, (ReadableWaveletObject) resultingState);
  }

}
