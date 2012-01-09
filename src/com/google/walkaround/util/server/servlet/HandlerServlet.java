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

package com.google.walkaround.util.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that dispatches to {@link AbstractHandler} handlers bound through Guice.
 *
 * Also provides some default behavior like a default character encoding
 *
 * @author ohler@google.com (Christian Ohler)
 */
@Singleton
public class HandlerServlet extends HttpServlet {
  private static final long serialVersionUID = 878017407495028316L;

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(HandlerServlet.class.getName());

  private final Map<String, Provider<AbstractHandler>> exactPathHandlers;
  private final Map<String, Provider<AbstractHandler>> prefixPathHandlers;

  @Inject
  public HandlerServlet(@ExactPathHandlers Map<String, Provider<AbstractHandler>> exactPathHandlers,
      @PrefixPathHandlers Map<String, Provider<AbstractHandler>> prefixPathHandlers) {
    this.exactPathHandlers = exactPathHandlers;
    this.prefixPathHandlers = prefixPathHandlers;
  }

  private AbstractHandler getHandler(HttpServletRequest req) {
    String requestUri = req.getRequestURI();
    AbstractHandler handler = getHandlerProvider(requestUri).get();
    log.info("getHandler(" + requestUri + ") = " + handler);
    return handler;
  }

  private Provider<AbstractHandler> getHandlerProvider(String requestUri) {
    // Try exact match.
    {
      Provider<AbstractHandler> handler = exactPathHandlers.get(requestUri);
      if (handler != null) {
        return handler;
      }
    }
    // Try prefix match.
    for (Map.Entry<String, Provider<AbstractHandler>> entry : prefixPathHandlers.entrySet()) {
      if (requestUri.startsWith(entry.getKey())) {
        return entry.getValue();
      }
    }
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
