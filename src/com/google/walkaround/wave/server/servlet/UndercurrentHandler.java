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

import com.google.common.base.Preconditions;
import com.google.gxp.base.GxpContext;
import com.google.gxp.html.HtmlClosure;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.walkaround.proto.ClientVars.ErrorVars;
import com.google.walkaround.proto.ClientVars.SuccessVars;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl.ErrorVarsGsonImpl;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl.SuccessVarsGsonImpl;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl.UdwLoadDataGsonImpl;
import com.google.walkaround.slob.server.AccessDeniedException;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.server.SlobNotFoundException;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.Util;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.shared.RandomBase64Generator;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.ObjectSession;
import com.google.walkaround.wave.server.ObjectSessionHelper;
import com.google.walkaround.wave.server.StoreType;
import com.google.walkaround.wave.server.WaveLoader;
import com.google.walkaround.wave.server.WaveLoader.LoadedWave;
import com.google.walkaround.wave.server.gxp.Client;
import com.google.walkaround.wave.server.auth.UserContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Serves the undercurrent client.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class UndercurrentHandler extends AbstractHandler {
  private static final String nocacheJs = Util.slurpRequired(
      "com.google.walkaround.wave.client.Walkaround.nocache.js");

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(UndercurrentHandler.class.getName());

  @Inject @Flag(FlagName.CLIENT_VERSION) int clientVersion;
  @Inject WaveLoader loader;
  @Inject Random random;
  @Inject RandomBase64Generator random64;
  @Inject ParticipantId participantId;
  @Inject @Named("channel api url") String channelApiUrl;
  @Inject ObjectSessionHelper sessionHelper;
  @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;
  @Inject UserContext userContext;

  private void setResponseHeaders(HttpServletResponse resp) {
    // TODO(ohler): It seems that the browser caches the response if we don't do this.
    // Do we need this everywhere?
    resp.setHeader("Pragma", "No-cache");
    resp.setHeader("Cache-Control", "no-cache");
    resp.setDateHeader("Expires", 1);
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
  }

  private JSONObject clientVarString(
      @Nullable SuccessVars successVars, @Nullable ErrorVars errorVars) {
    ClientVarsGsonImpl clientVars = new ClientVarsGsonImpl();
    if (successVars != null) {
      Preconditions.checkArgument(errorVars == null, "Both successVars and errorVars set: %s, %s",
          successVars, errorVars);
      clientVars.setSuccessVars(successVars);
    } else {
      clientVars.setErrorVars(errorVars);
    }
    String jsonString = GsonProto.toJson(clientVars);
    try {
      // TODO(ohler): Find a way to embed a GSON-generated JSON literal in GXP
      // without serializing and re-parsing to org.json.JSONObject.  Perhaps
      // through JavascriptClosure?
      return new JSONObject(jsonString);
    } catch (JSONException e) {
      throw new RuntimeException("org.json failed to parse GSON's output: " + jsonString, e);
    }
  }

  private void writeErrorResponse(HttpServletRequest req, HttpServletResponse resp,
      String errorMessage) throws IOException {
    ErrorVarsGsonImpl errorVars = new ErrorVarsGsonImpl();
    errorVars.setErrorMessage(errorMessage);
    setResponseHeaders(resp);
    Client.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, clientVarString(null, errorVars), inlineNocacheJs(), channelApiUrl);
  }

  private void writeSuccessResponse(HttpServletRequest req, HttpServletResponse resp,
      ClientId clientId, LoadedWave wave) throws IOException {
    SuccessVarsGsonImpl successVars = new SuccessVarsGsonImpl();
    successVars.setClientVersion(clientVersion);
    successVars.setRandomSeed(random.nextInt());
    successVars.setUserEmail(participantId.getAddress());
    successVars.setHaveOauthToken(userContext.hasOAuthCredentials());
    successVars.setConvConnectResponse(
        sessionHelper.createConnectResponse(
            new ObjectSession(wave.getConvObjectId(), clientId, StoreType.CONV),
            wave.getConvConnectResult()));
    successVars.setConvSnapshot(wave.getConvSnapshotWithDiffs());
    if (wave.getUdw() != null) {
      UdwLoadDataGsonImpl udwLoadData = new UdwLoadDataGsonImpl();
      udwLoadData.setConnectResponse(
          sessionHelper.createConnectResponse(
              new ObjectSession(wave.getUdw().getObjectId(), clientId, StoreType.UDW),
              wave.getUdw().getConnectResult()));
      udwLoadData.setSnapshot(wave.getUdw().getSnapshot());
      successVars.setUdw(udwLoadData);
    }
    setResponseHeaders(resp);
    Client.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, clientVarString(successVars, null), inlineNocacheJs(), channelApiUrl);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    ClientId clientId = new ClientId(random64.next(
        // TODO(ohler): justify this number
        8));
    SlobId convObjectId = new SlobId(requireParameter(req, "id"));
    LoadedWave wave;
    try {
      wave = loader.load(convObjectId, clientId);
    } catch (AccessDeniedException e) {
      log.log(Level.SEVERE, "Wave not found or access denied", e);
      writeErrorResponse(req, resp, "Wave not found or access denied");
      return;
    } catch (SlobNotFoundException e) {
      log.log(Level.SEVERE, "Wave not found or access denied", e);
      writeErrorResponse(req, resp, "Wave not found or access denied");
      return;
    } catch (IOException e) {
      log.log(Level.SEVERE, "Server error loading wave", e);
      writeErrorResponse(req, resp, "Server error loading wave");
      return;
    }
    writeSuccessResponse(req, resp, clientId, wave);
  }

  private HtmlClosure inlineNocacheJs() {
    return new HtmlClosure() {
      @Override public void write(Appendable out, GxpContext gxpContext) throws IOException {
        out.append("<script type='text/javascript'>\n");
        // No need to escape this, GWT's nocache javascript is already escaped for
        // the purpose of direct inclusion into script elements.
        out.append(nocacheJs);
        out.append("</script>");
      }
    };
  }
}
