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

package com.google.walkaround.wave.server.util;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.walkaround.wave.server.WalkaroundServletModule;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that looks up a handler class from the handler maps in
 * {@link WalkaroundServletModule} for every request, instantiates
 * it with Guice, and dispatches to it.
 *
 * Also provides some default behavior like a default character encoding
 *
 * @author ohler@google.com (Christian Ohler)
 */
@Singleton
public class HandlerServlet extends HttpServlet {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(HandlerServlet.class.getName());

  // Instead of using an Injector, we could probably have a map of Providers or
  // one Provider that looks at annotations; but for now, this works.
  private final Injector injector;

  @Inject
  public HandlerServlet(Injector injector) {
    this.injector = injector;
  }

  private AbstractHandler getHandler(HttpServletRequest req) {
    String requestUri = req.getRequestURI();
    Class<? extends AbstractHandler> handlerClass = getHandlerClass(requestUri);
    AbstractHandler handler = injector.getInstance(handlerClass);
    log.info("getHandler(" + requestUri + ") = " + handler);
    return handler;
  }

  private Class<? extends AbstractHandler> getHandlerClass(String requestUri) {
    // Try exact match.
    if (WalkaroundServletModule.EXACT_PATH_HANDLERS.containsKey(requestUri)) {
      return WalkaroundServletModule.EXACT_PATH_HANDLERS.get(requestUri);
    }

    // Try prefix match.
    for (String prefix : WalkaroundServletModule.PREFIX_PATH_HANDLERS.keySet()) {
      if (requestUri.startsWith(prefix)) {
        return WalkaroundServletModule.PREFIX_PATH_HANDLERS.get(prefix);
      }
    }

    // This shouldn't happen since HandlerServlet should only be bound to paths
    // in the handler maps.
   throw new RuntimeException("No handler found for: " + requestUri);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    resp.setCharacterEncoding("UTF-8");
    getHandler(req).doGet(req, resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    resp.setCharacterEncoding("UTF-8");
    getHandler(req).doPost(req, resp);
  }

  // Other methods from javax.servlet.http.HttpServlet, like doPut etc., could
  // be added here if we need them.

}
