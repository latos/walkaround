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

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that redirects to another URL.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class RedirectServlet extends HttpServlet {
  private static final long serialVersionUID = 930597932186069542L;

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(RedirectServlet.class.getName());

  private final String destination;

  public RedirectServlet(String destination) {
    this.destination = destination;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("redirecting from " + req.getRequestURL() + " to " + destination);
    resp.sendRedirect(destination);
  }
}
