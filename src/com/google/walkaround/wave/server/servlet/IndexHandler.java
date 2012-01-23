/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.google.walkaround.wave.server.servlet;

import com.google.inject.Inject;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.wave.server.util.RequestUtil;
import com.google.walkaround.wave.server.wavemanager.InboxHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Make the root page display one of two pages depending on user agent.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class IndexHandler extends AbstractHandler {
  @Inject private ClientHandler clientHandler;
  @Inject private InboxHandler inboxHandler;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
          ServletException {
    if (RequestUtil.isMobile(req)) {
      inboxHandler.doGet(req, resp);
    } else {
      clientHandler.doGet(req, resp);
    }
  }
}
