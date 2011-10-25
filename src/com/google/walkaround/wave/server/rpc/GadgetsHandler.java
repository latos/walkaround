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

package com.google.walkaround.wave.server.rpc;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.walkaround.wave.server.util.AbstractHandler;
import com.google.walkaround.wave.server.util.ProxyHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles gadget requests by proxying them to a gadget server.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class GadgetsHandler extends AbstractHandler {
  /** Configured proxy. */
  private final ProxyHandler delegate;

  @Inject
  public GadgetsHandler(
      @Named("gadget serve path") String source,
      @Named("gadget server") String target,
      URLFetchService fetch) {
    delegate = new ProxyHandler(source, target, fetch);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    delegate.doGet(req, resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    delegate.doPost(req, resp);
  }
}
