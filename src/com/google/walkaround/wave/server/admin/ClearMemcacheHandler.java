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

package com.google.walkaround.wave.server.admin;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.users.User;
import com.google.inject.Inject;
import com.google.walkaround.wave.server.util.AbstractHandler;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Clears memcache.  Should only be exposed to admin users.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ClearMemcacheHandler extends AbstractHandler {

  private final MemcacheService memcache;
  private final User user;

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ClearMemcacheHandler.class.getName());

  @Inject
  public ClearMemcacheHandler(MemcacheService memcache, User user) {
    this.memcache = memcache;
    this.user = user;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.warning("Memcache clear requested by " + user.getEmail());
    memcache.clearAll();
    log.warning("Memcache cleared");
    resp.setContentType("text/plain");
    resp.getWriter().println("Memcache cleared, " + user.getEmail());
  }


}
