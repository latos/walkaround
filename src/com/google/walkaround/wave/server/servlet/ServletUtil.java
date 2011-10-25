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

import com.google.gson.JsonObject;
import com.google.walkaround.wave.shared.SharedConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;

/**
 * Utilities for the servlets
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(ohler): This is near-empty now, should eliminate altogether, or give it
// a more specific name.
public class ServletUtil {

  /**
   * Writes the string to the print writer according to the protocol the client
   * expects.
   *
   * @param w
   * @param str must be valid JSON
   */
  public static void writeJsonResult(PrintWriter w, String str) {
    try {
      assert new JSONObject(str) != null;
    } catch (JSONException e) {
      throw new IllegalArgumentException("Bad JSON: " + str, e);
    }

    w.print(SharedConstants.XSSI_PREFIX + "(" + str + ")");
  }

  public static String getSubmitDeltaResultJson(long resultingRevision) {
    JsonObject json = new JsonObject();
    json.addProperty("version", resultingRevision);
    return json.toString();
  }

}
