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

package com.google.walkaround.wave.server.servlet;

import com.google.inject.Inject;
import com.google.walkaround.proto.ServerMutateRequest;
import com.google.walkaround.proto.ServerMutateResponse;
import com.google.walkaround.proto.gson.ServerMutateRequestGsonImpl;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.server.SlobStoreSelector;
import com.google.walkaround.slob.server.StoreAccessChecker;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.ObjectSession;
import com.google.walkaround.wave.server.ObjectSessionHelper;

import org.waveprotocol.wave.communication.gson.GsonSerializable;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Directly mutates the data store. Looks for the "X-Walkaround-Trusted" header.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class StoreMutateHandler extends AbstractHandler {

  private static final Logger log = Logger.getLogger(StoreMutateHandler.class.getName());

  @Inject StoreAccessChecker accessChecker;
  @Inject SlobStoreSelector storeSelector;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    accessChecker.checkPermittedStoreRequest(req);
    String requestString = requireParameter(req, "req");
    ServerMutateRequest mutateRequest;
    try {
      mutateRequest = GsonProto.fromGson(new ServerMutateRequestGsonImpl(),
          requestString);
    } catch (MessageException e) {
      throw new BadRequestException("Failed to parse request: " + requestString, e);
    }
    ObjectSession session = ObjectSessionHelper.objectSessionFromProto(mutateRequest.getSession());
    ServerMutateResponse result =
        storeSelector.get(session.getStoreType()).getLocalMutationProcessor()
        .mutateObject(mutateRequest);
    log.info("Success @" + result.getResultingVersion());

    resp.setStatus(200);
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().print("OK" + GsonProto.toJson((GsonSerializable) result));
  }

}
