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

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.google.walkaround.wave.server.GuiceSetup;

import junit.framework.TestCase;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class GuiceSetupTest extends TestCase {

  private static final String WEB_INF_DIR = "build/war/WEB-INF";

  public void testBindingsForServlets() {
    SystemProperty.environment.set(SystemProperty.Environment.Value.Development);
    // The test is that none of this throws any exceptions.
    Injector injector = Guice.createInjector(Stage.PRODUCTION,
        Modules.combine(
            GuiceSetup.getRootModule(WEB_INF_DIR),
            GuiceSetup.getServletModule(),
            new DefaultModule()));
    // TODO(ohler): Figure out how to set up a fake request scope and reinstate this.
    //for (Class<?> handlerClass : WalkaroundServletModule.HANDLER_MAP.values()) {
    //  injector.getInstance(handlerClass);
    //}
  }

  public void testBindingsForMapreduce() {
    SystemProperty.environment.set(SystemProperty.Environment.Value.Development);
    // The test is that none of this throws any exceptions.
    Injector injector = Guice.createInjector(Stage.PRODUCTION,
        Modules.combine(
            GuiceSetup.getRootModule(WEB_INF_DIR),
            GuiceSetup.getMapreduceModule(),
            new DefaultModule()));
  }

}
