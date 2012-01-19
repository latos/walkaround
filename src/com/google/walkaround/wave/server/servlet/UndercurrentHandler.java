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
import com.google.common.collect.ImmutableList;
import com.google.gxp.base.GxpContext;
import com.google.gxp.html.HtmlClosure;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.walkaround.proto.ClientVars.ErrorVars;
import com.google.walkaround.proto.ClientVars.LiveClientVars;
import com.google.walkaround.proto.ClientVars.StaticClientVars;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl.ErrorVarsGsonImpl;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl.LiveClientVarsGsonImpl;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl.StaticClientVarsGsonImpl;
import com.google.walkaround.proto.gson.ClientVarsGsonImpl.UdwLoadDataGsonImpl;
import com.google.walkaround.slob.server.AccessDeniedException;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.server.SlobFacilities;
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
import com.google.walkaround.wave.server.WaveLoader;
import com.google.walkaround.wave.server.WaveLoader.LoadedWave;
import com.google.walkaround.wave.server.auth.UserContext;
import com.google.walkaround.wave.server.conv.ConvStore;
import com.google.walkaround.wave.server.gxp.Wave;
import com.google.walkaround.wave.server.udw.UdwStore;

import org.json.JSONException;
import org.json.JSONObject;
import org.waveprotocol.wave.model.wave.ParticipantId;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  @Inject @UdwStore SlobFacilities udwStore;
  @Inject @ConvStore SlobFacilities convStore;

  private void setResponseHeaders(HttpServletResponse resp) {
    // TODO(ohler): Figure out how to make static versions cacheable.  The
    // problem is that we inline GWT's nocache JS, and having that cached is
    // very confusing when debugging, and could lead to situations where a
    // browser caches the nocache JS but not the cacheable JS and expects the
    // server to still have the cacheable JS -- this won't be true if a new
    // client binary has been deployed to the server.

    // TODO(ohler): Find a systematic way of setting no-cache headers in all
    // handlers that need them.

    // http://www.mnot.net/cache_docs/#PRAGMA recommends not to use this;
    // TODO(ohler): Figure out if it does any good.  Walkaround doesn't work in
    // old browsers anyway, so we shouldn't need hacks.
    resp.setHeader("Pragma", "No-cache");
    // NOTE(ohler): Using this rather than just "no-cache" seems to fix the
    // client crash on back/forward due to re-use of client id.
    resp.setHeader("Cache-Control", "no-store, must-revalidate");

    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
  }

  private JSONObject clientVarString(
      @Nullable LiveClientVars liveClientVars,
      @Nullable StaticClientVars staticClientVars,
      @Nullable ErrorVars errorVars) {
    Preconditions.checkArgument(Collections.frequency(
            ImmutableList.of(liveClientVars != null, staticClientVars != null, errorVars != null),
            true) == 1,
        "%s/%s/%s", liveClientVars, staticClientVars, errorVars);
    ClientVarsGsonImpl clientVars = new ClientVarsGsonImpl();
    if (liveClientVars != null) {
      clientVars.setLiveClientVars(liveClientVars);
    } else if (staticClientVars != null) {
      clientVars.setStaticClientVars(staticClientVars);
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
    Wave.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, clientVarString(null, null, errorVars), true,
        inlineNocacheJs(), channelApiUrl);
  }

  private void writeLiveClientResponse(HttpServletRequest req, HttpServletResponse resp,
      ClientId clientId, LoadedWave wave) throws IOException {
    LiveClientVarsGsonImpl vars = new LiveClientVarsGsonImpl();
    vars.setClientVersion(clientVersion);
    vars.setRandomSeed(random.nextInt());
    vars.setUserEmail(participantId.getAddress());
    vars.setHaveOauthToken(userContext.hasOAuthCredentials());
    vars.setConvSnapshot(wave.getConvSnapshotWithDiffs());
    vars.setConvConnectResponse(
        sessionHelper.createConnectResponse(
            new ObjectSession(wave.getConvObjectId(), clientId, convStore.getRootEntityKind()),
            wave.getConvConnectResult()));
    if (wave.getUdw() != null) {
      UdwLoadDataGsonImpl udwLoadData = new UdwLoadDataGsonImpl();
      udwLoadData.setConnectResponse(
          sessionHelper.createConnectResponse(
              new ObjectSession(wave.getUdw().getObjectId(), clientId,
                  udwStore.getRootEntityKind()),
              wave.getUdw().getConnectResult()));
      udwLoadData.setSnapshot(wave.getUdw().getSnapshot());
      vars.setUdw(udwLoadData);
    }
    setResponseHeaders(resp);
    Wave.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, clientVarString(vars, null, null), true,
        inlineNocacheJs(), channelApiUrl);
  }

  private void writeStaticClientResponse(HttpServletRequest req, HttpServletResponse resp,
      LoadedWave wave) throws IOException {
    StaticClientVarsGsonImpl vars = new StaticClientVarsGsonImpl();
    vars.setRandomSeed(random.nextInt());
    vars.setUserEmail(participantId.getAddress());
    vars.setHaveOauthToken(userContext.hasOAuthCredentials());
    vars.setConvObjectId(wave.getConvObjectId().getId());
    vars.setConvSnapshot(wave.getConvSnapshotWithDiffs());
    setResponseHeaders(resp);
    Wave.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, clientVarString(null, vars, null), true,
        inlineNocacheJs(), channelApiUrl);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    SlobId convObjectId = new SlobId(requireParameter(req, "id"));
    String versionString = optionalParameter(req, "version", null);
    @Nullable Long version = versionString == null ? null : Long.parseLong(versionString);
    @Nullable ClientId clientId = version != null ? null
        : new ClientId(random64.next(
                // TODO(ohler): justify this number
                8));
    LoadedWave wave;
    try {
      if (version == null) {
        wave = loader.load(convObjectId, clientId);
      } else {
        wave = loader.loadStaticAtVersion(convObjectId, version);
      }
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
    if (version == null) {
      writeLiveClientResponse(req, resp, clientId, wave);
    } else {
      writeStaticClientResponse(req, resp, wave);
    }
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
