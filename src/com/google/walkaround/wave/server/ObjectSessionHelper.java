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

package com.google.walkaround.wave.server;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.walkaround.proto.ObjectSessionProto;
import com.google.walkaround.proto.gson.ConnectResponseGsonImpl;
import com.google.walkaround.proto.gson.ObjectSessionProtoGsonImpl;
import com.google.walkaround.proto.gson.SignedObjectSessionGsonImpl;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.server.SlobStore.ConnectResult;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.auth.InvalidSecurityTokenException;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.auth.XsrfHelper;
import com.google.walkaround.wave.server.auth.XsrfHelper.XsrfTokenExpiredException;
import com.google.walkaround.wave.shared.SharedConstants.Params;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Deals with {@link ObjectSession}s: Serializes, parses, signs, verifies.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ObjectSessionHelper {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ObjectSessionHelper.class.getName());

  private final XsrfHelper xsrfHelper;

  @Inject
  public ObjectSessionHelper(XsrfHelper xsrfHelper) {
    this.xsrfHelper = xsrfHelper;
  }

  private String makeAction(ObjectSessionProto session) {
    // If we made sure GSON serialization has the keys in deterministic order,
    // we could just serialize the protobuf and use that.
    Preconditions.checkArgument(!session.getObjectId().contains(" "),
        "Object ID contains a space: %s", session);
    Preconditions.checkArgument(!session.getClientId().contains(" "),
        "Client ID contains a space: %s", session);
    return session.getObjectId() + " " + session.getClientId()
        + " " + session.getStoreType();
  }

  /**
   * Extracts a signed ObjectSession from req, verifies its signature, and
   * returns it.
   */
  public ObjectSession getVerifiedSession(HttpServletRequest req)
      throws InvalidSecurityTokenException, XsrfTokenExpiredException {
    String rawSessionString = AbstractHandler.requireParameter(req, Params.SESSION);
    log.info("Parsing and verifying signed session " + rawSessionString);
    SignedObjectSessionGsonImpl signedSession;
    try {
      signedSession = GsonProto.fromGson(
          new SignedObjectSessionGsonImpl(), rawSessionString);
    } catch (MessageException e) {
      throw new BadRequestException("Failed to parse signed session", e);
    }
    xsrfHelper.verify(makeAction(signedSession.getSession()), signedSession.getSignature());
    return objectSessionFromProto(signedSession.getSession());
  }

  public static ObjectSession objectSessionFromProto(ObjectSessionProto proto) {
    return new ObjectSession(new SlobId(proto.getObjectId()),
        new ClientId(proto.getClientId()),
        StoreType.parse(proto.getStoreType()));
  }

  public static ObjectSessionProto protoFromObjectSession(ObjectSession session) {
    ObjectSessionProto proto = new ObjectSessionProtoGsonImpl();
    proto.setObjectId(session.getObjectId().getId());
    proto.setClientId(session.getClientId().getId());
    proto.setStoreType(session.getStoreType().serialize());
    return proto;
  }

  private SignedObjectSessionGsonImpl createSignedSession(ObjectSession session) {
    ObjectSessionProto proto = protoFromObjectSession(session);
    SignedObjectSessionGsonImpl signedSession = new SignedObjectSessionGsonImpl();
    signedSession.setSession(proto);
    signedSession.setSignature(xsrfHelper.createToken(makeAction(proto)));
    return signedSession;
  }

  /**
   * Signs the ObjectSession and puts it in a ConnectResponse together with the
   * ConnectResult.
   */
  public ConnectResponseGsonImpl createConnectResponse(
      ObjectSession session, ConnectResult result) {
    ConnectResponseGsonImpl response = new ConnectResponseGsonImpl();
    SignedObjectSessionGsonImpl signedSession = createSignedSession(session);
    response.setSignedSession(signedSession);
    response.setSignedSessionString(GsonProto.toJson(signedSession));
    if (result.getChannelToken() != null) {
      response.setChannelToken(result.getChannelToken());
    }
    response.setObjectVersion(result.getVersion());
    return response;
  }

}
