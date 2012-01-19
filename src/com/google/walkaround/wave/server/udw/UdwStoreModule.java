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

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.inject.PrivateModule;
import com.google.walkaround.slob.server.AccessChecker;
import com.google.walkaround.slob.server.PostCommitActionQueue;
import com.google.walkaround.slob.server.StoreModuleHelper;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.SlobModel;
import com.google.walkaround.wave.server.model.WaveObjectStoreModel;

import java.util.logging.Logger;

/**
 * Guice module that configures an object store for user data wavelets.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class UdwStoreModule extends PrivateModule {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(UdwStoreModule.class.getName());

  public static String ROOT_ENTITY_KIND = "Udw";

  @Override protected void configure() {
    StoreModuleHelper.makeBasicBindingsAndExposures(binder(), UdwStore.class);
    StoreModuleHelper.bindEntityKinds(binder(), ROOT_ENTITY_KIND);

    bind(SlobModel.class).to(WaveObjectStoreModel.class);
    bind(AccessChecker.class).toInstance(
        new AccessChecker() {
          // We don't do access checks for UDWs here; we rely on WaveLoader to
          // look up the correct UDW, and on our session security to make it
          // impossible to access a UDW other than the one that WaveLoader
          // returned.
          @Override public void checkCanRead(SlobId objectId) {}
          @Override public void checkCanModify(SlobId objectId) {}
          @Override public void checkCanCreate(SlobId objectId) {}
        });
    bind(Queue.class).annotatedWith(PostCommitActionQueue.class).toInstance(
        QueueFactory.getQueue("post-commit-udw"));
  }

}
