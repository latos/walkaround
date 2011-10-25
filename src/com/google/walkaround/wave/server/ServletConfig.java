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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class ServletConfig extends GuiceServletContextListener {

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(
        // We use Stage.DEVELOPMENT to make start-up faster on App Engine.  We
        // have a test (GuiceSetupTest) that uses Stage.PRODUCTION to find
        // errors.  TODO(ohler): Find out if Stage.PRODUCTION combined with
        // warmup requests is better.
        Stage.DEVELOPMENT,
        GuiceSetup.getRootModule(),
        GuiceSetup.getServletModule());
  }

}
