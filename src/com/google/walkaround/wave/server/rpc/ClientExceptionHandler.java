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
import com.google.walkaround.util.server.Util;
import com.google.walkaround.util.server.gwt.StackTraceDeobfuscator;
import com.google.walkaround.wave.server.util.AbstractHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ClientExceptionHandler extends AbstractHandler {

  /**
   * Represents an exception from the client.
   */
  private static class ClientException extends Exception {
    private ClientException(String className, String message, StackTraceElement[] st,
        ClientException cause) {
      super("(" + className + ") " + message, cause);
      setStackTrace(st);
    }
  }

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ClientExceptionHandler.class.getName());

  private final StackTraceDeobfuscator deobfuscator;

  @Inject
  public ClientExceptionHandler(StackTraceDeobfuscator deobfuscator) {
    this.deobfuscator = deobfuscator;
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      handleData(Util.slurpUtf8(req.getInputStream()));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleData(String raw) throws JSONException {
    log.info("raw data: " + raw);
    JSONObject data = new JSONObject(raw);
    Level level = "SEVERE".equals(data.getString("level")) ? Level.SEVERE : Level.WARNING;
    StringBuilder b = new StringBuilder();
    b.append("[" + data.getString("stream") + ":" + data.getLong("timestamp") + "] ");
    JSONArray objects = data.getJSONArray("objects");
    for (int i = 0; i < objects.length(); i++) {
      b.append(objects.getString(i));
    }
    ClientException ex = null;
    if (data.has("exception")) {
      ex = buildException(data.getJSONObject("exception"), data.getString("strongName"));
    }
    log.log(level, b.toString(), ex);
  }

  private ClientException buildException(JSONObject data, String strongName) throws JSONException {
    ClientException cause = null;
    if (data.has("cause")) {
      cause = buildException(data.getJSONObject("cause"), strongName);
    }
    JSONArray stData = data.getJSONArray("stackTrace");
    String[] st = new String[stData.length()];
    for (int i = 0; i < st.length; i++) {
      st[i] = stData.getString(i);
    }

    String exceptionConstructor = data.getString("name").replace("Class$", "");
    return new ClientException(
        deobfuscator.resymbolize(exceptionConstructor, strongName).getClassName()
        + "?", // NOTE(danilatos): sometimes the class name might be wrong.
        data.getString("message"),
        deobfuscator.deobfuscateStackTrace(st, strongName), cause);
  }
}
