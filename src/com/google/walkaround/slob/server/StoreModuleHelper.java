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

import com.google.appengine.api.datastore.Key;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.walkaround.slob.server.MutationLog.MutationLogFactory;
import com.google.walkaround.slob.shared.SlobId;

import java.lang.annotation.Annotation;
import java.util.logging.Logger;

/**
 * Helper for Guice {@link PrivateModule}s that configure instances of the
 * slob store.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class StoreModuleHelper {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(StoreModuleHelper.class.getName());

  public static <F, P> Module factoryModule(Class<F> factoryClass, Class<P> productClass) {
    return new FactoryModuleBuilder()
        .implement(productClass, productClass)
        .build(factoryClass);
  }

  private static class FacilitiesImpl implements SlobFacilities {
    // These are providers since many users will only need one.
    @Inject Provider<SlobStore> slobStore;
    @Inject Provider<LocalMutationProcessor> localMutationProcessor;
    @Inject Provider<MutationLogFactory> mutationLogFactory;
    @Inject @SlobRootEntityKind String rootEntityKind;
    @Inject Provider<PostCommitActionScheduler> postCommitActionScheduler;

    @Override public String toString() {
      return "SlobFacilities(" + rootEntityKind + ")";
    }

    @Override public SlobStore getSlobStore() {
      return slobStore.get();
    }

    @Override public LocalMutationProcessor getLocalMutationProcessor() {
      return localMutationProcessor.get();
    }

    @Override public MutationLogFactory getMutationLogFactory() {
      return mutationLogFactory.get();
    }

    @Override public String getRootEntityKind() {
      return rootEntityKind;
    }

    @Override public Key makeRootEntityKey(SlobId slobId) {
      return MutationLog.makeRootEntityKey(rootEntityKind, slobId);
    }

    @Override public SlobId parseRootEntityKey(Key key) {
      return MutationLog.parseRootEntityKey(rootEntityKind, key);
    }

    @Override public PostCommitActionScheduler getPostCommitActionScheduler() {
      return postCommitActionScheduler.get();
    }
  }

  public static void makeBasicBindingsAndExposures(PrivateBinder binder,
      Class<? extends Annotation> annotation) {
    binder.bind(SlobFacilities.class).to(FacilitiesImpl.class);
    binder.bind(SlobStore.class).to(SlobStoreImpl.class);
    binder.install(factoryModule(MutationLogFactory.class, MutationLog.class));

    binder.bind(MutationLogFactory.class).annotatedWith(annotation).to(MutationLogFactory.class);
    binder.bind(SlobStore.class).annotatedWith(annotation).to(SlobStore.class);
    binder.bind(LocalMutationProcessor.class).annotatedWith(annotation)
        .to(LocalMutationProcessor.class);
    binder.bind(SlobFacilities.class).annotatedWith(annotation).to(FacilitiesImpl.class);

    binder.expose(MutationLogFactory.class).annotatedWith(annotation);
    binder.expose(SlobStore.class).annotatedWith(annotation);
    binder.expose(LocalMutationProcessor.class).annotatedWith(annotation);
    binder.expose(SlobFacilities.class).annotatedWith(annotation);

    // Make sure a binding for the Set exists.
    Multibinder.newSetBinder(binder, PreCommitAction.class);
    // Make sure a binding for the Set exists.
    Multibinder.newSetBinder(binder, PostCommitAction.class);
  }

  public static void bindEntityKinds(PrivateBinder binder, String prefix) {
    binder.bind(String.class).annotatedWith(SlobRootEntityKind.class).toInstance(prefix);
    binder.bind(String.class).annotatedWith(SlobDeltaEntityKind.class)
        .toInstance(prefix + "Delta");
    binder.bind(String.class).annotatedWith(SlobSnapshotEntityKind.class)
        .toInstance(prefix + "Snapshot");
    binder.bind(String.class).annotatedWith(SlobSynchronizationEntityKind.class)
        .toInstance(prefix + "Sync");
  }

}
