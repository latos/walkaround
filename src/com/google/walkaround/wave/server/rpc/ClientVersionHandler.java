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

import com.google.inject.Inject;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ClientVersionHandler extends AbstractHandler {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ClientVersionHandler.class.getName());

  @Inject @Flag(FlagName.CLIENT_VERSION) private int requiredClientVersion;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String actualClientVersion = optionalParameter(req, "version", "<unknown>");
    log.info("Client has version " + actualClientVersion
        + ", required version is " + requiredClientVersion);
    resp.setContentType("text/plain");
    resp.getWriter().print(requiredClientVersion + "");
  }
}
