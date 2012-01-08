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
import com.google.walkaround.proto.gson.ConnectResponseGsonImpl;
import com.google.walkaround.slob.server.AccessDeniedException;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.server.SlobNotFoundException;
import com.google.walkaround.slob.server.SlobStore.ConnectResult;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.ObjectSession;
import com.google.walkaround.wave.server.ObjectSessionHelper;
import com.google.walkaround.wave.server.ObjectStoreSelector;
import com.google.walkaround.wave.server.servlet.ServletUtil;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Gets current wavelet version and creates a new session.  Used for
 * reconnection.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ConnectHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ConnectHandler.class.getName());

  @Inject ObjectStoreSelector storeSelector;
  @Inject ObjectSession session;
  @Inject ObjectSessionHelper sessionHelper;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("connect " + session);

    ConnectResult result;
    try {
      result = storeSelector.get(session.getStoreType())
          .reconnect(session.getObjectId(), session.getClientId());
    } catch (SlobNotFoundException e) {
      throw new BadRequestException("Object not found or access denied", e);
    } catch (AccessDeniedException e) {
      throw new BadRequestException("Object not found or access denied", e);
    }
    log.info("connect " + session + ": " + result);

    ConnectResponseGsonImpl response = sessionHelper.createConnectResponse(session, result);

    resp.setContentType("application/json");
    ServletUtil.writeJsonResult(resp.getWriter(),
        GsonProto.toJson(response));
  }

}
