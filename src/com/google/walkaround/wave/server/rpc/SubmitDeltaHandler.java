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
import com.google.walkaround.proto.ServerMutateRequest;
import com.google.walkaround.proto.gson.ServerMutateRequestGsonImpl;
import com.google.walkaround.slob.server.AccessDeniedException;
import com.google.walkaround.slob.server.MutateResult;
import com.google.walkaround.slob.server.SlobStoreSelector;
import com.google.walkaround.slob.server.SlobNotFoundException;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.ObjectSession;
import com.google.walkaround.wave.server.ObjectSessionHelper;
import com.google.walkaround.wave.server.model.ServerMessageSerializer;
import com.google.walkaround.wave.server.servlet.ServletUtil;
import com.google.walkaround.wave.shared.WaveSerializer;

import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Submits a delta to a wavelet.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class SubmitDeltaHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(SubmitDeltaHandler.class.getName());

  private static final WaveSerializer SERIALIZER =
      new WaveSerializer(new ServerMessageSerializer());

  @Inject SlobStoreSelector storeSelector;
  @Inject ObjectSession session;
  @Inject ParticipantId participantId;

  private List<String> makeDeltas(long timestamp, String serializedOperationBatch) {
    List<WaveletOperation> operations;
    try {
      operations = SERIALIZER.deserializeOperationBatch(participantId, serializedOperationBatch,
          timestamp);
    } catch (MessageException e) {
      throw new RuntimeException(e);
    }
    return SERIALIZER.serializeDeltas(operations);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String versionString = requireParameter(req, "v");
    long version;
    try {
      version = Long.parseLong(versionString);
    } catch (NumberFormatException e) {
      throw new RuntimeException(e);
    }
    long timestamp = System.currentTimeMillis();
    List<String> deltas = makeDeltas(timestamp, requireParameter(req, "operations"));

    ServerMutateRequest mutateRequest = new ServerMutateRequestGsonImpl();
    mutateRequest.setSession(ObjectSessionHelper.protoFromObjectSession(session));
    mutateRequest.setVersion(version);
    mutateRequest.addAllPayload(deltas);

    MutateResult res;
    try {
      res = storeSelector.get(session.getStoreType()).getSlobStore().mutateObject(mutateRequest);
    } catch (SlobNotFoundException e) {
      throw new BadRequestException("Object not found or access denied", e);
    } catch (AccessDeniedException e) {
      throw new BadRequestException("Object not found or access denied", e);
    }

    resp.setContentType("application/json");
    ServletUtil.writeJsonResult(resp.getWriter(),
        ServletUtil.getSubmitDeltaResultJson(res.getResultingVersion()));
  }
}
