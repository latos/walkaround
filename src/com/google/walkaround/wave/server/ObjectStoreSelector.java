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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.walkaround.slob.server.LocalMutationProcessor;
import com.google.walkaround.slob.server.MutationLog.MutationLogFactory;
import com.google.walkaround.slob.server.SlobStore;
import com.google.walkaround.wave.server.conv.ConvStore;
import com.google.walkaround.wave.server.udw.UdwStore;

/**
 * Returns either the conv or UDW object store, based on a {@link StoreType}.
 *
 * @author ohler@google.com (Christian Ohler)
 */
// TODO(ohler): Consider replacing this with a MapBinder<StoreType,
// Provider<StoreFacilities>>, where StoreFacilities is a tuple of SlobStore,
// MutationLog, and LocalMutationProcessor.
public class ObjectStoreSelector {

  // Providers because we won't need them all (and might not be able to
  // instantiate them on backends, where there is no UserContext).
  private final Provider<SlobStore> conv;
  private final Provider<SlobStore> udw;
  private final Provider<MutationLogFactory> convMutationLogFactory;
  private final Provider<MutationLogFactory> udwMutationLogFactory;
  private final Provider<LocalMutationProcessor> convLocalProcessor;
  private final Provider<LocalMutationProcessor> udwLocalProcessor;

  @Inject
  public ObjectStoreSelector(@ConvStore Provider<SlobStore> conv,
      @UdwStore Provider<SlobStore> udw,
      @ConvStore Provider<MutationLogFactory> convMutationLogFactory,
      @UdwStore Provider<MutationLogFactory> udwMutationLogFactory,
      @ConvStore Provider<LocalMutationProcessor> convLocalProcessor,
      @UdwStore Provider<LocalMutationProcessor> udwLocalProcessor) {
    this.conv = conv;
    this.udw = udw;
    this.convMutationLogFactory = convMutationLogFactory;
    this.udwMutationLogFactory = udwMutationLogFactory;
    this.convLocalProcessor = convLocalProcessor;
    this.udwLocalProcessor = udwLocalProcessor;
  }

  public SlobStore get(StoreType type) {
    switch (type) {
      case CONV: return conv.get();
      case UDW: return udw.get();
      default:
        throw new Error("Unexpected type " + type);
    }
  }

  public MutationLogFactory getMutationLogFactory(StoreType type) {
    switch (type) {
      case CONV: return convMutationLogFactory.get();
      case UDW: return udwMutationLogFactory.get();
      default:
        throw new Error("Unexpected type " + type);
    }
  }

  public LocalMutationProcessor getLocalProcessor(StoreType type) {
    switch (type) {
      case CONV: return convLocalProcessor.get();
      case UDW: return udwLocalProcessor.get();
      default:
        throw new Error("Unexpected type " + type);
    }
  }

}
