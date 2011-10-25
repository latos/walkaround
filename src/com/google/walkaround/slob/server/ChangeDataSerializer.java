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

package com.google.walkaround.slob.server;

import com.google.common.base.Preconditions;
import com.google.walkaround.slob.shared.ChangeData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ChangeDataSerializer {

  private ChangeDataSerializer() {}

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ChangeDataSerializer.class.getName());

  /** The largest integer that can be represented losslessly by a double */
  public static final long MAX_DOUBLE_INTEGER = 1L << 52 - 1;

  public static JSONObject dataToClientJson(ChangeData<String> data, long resultingRevision) {
    Preconditions.checkArgument(resultingRevision <= MAX_DOUBLE_INTEGER,
        "Resulting revision %s is too large", resultingRevision);

    // Assume payload is JSON, and parse it to avoid nested json.
    // TODO(danilatos): Consider using ChangeData<JSONObject> instead.
    // The reason I haven't done it yet is because it's not immutable,
    // and also for reasons described in ChangeData.
    JSONObject payloadJson;
    try {
      payloadJson = new JSONObject(data.getPayload());
    } catch (JSONException e) {
      throw new IllegalArgumentException("Invalid payload for " + data, e);
    }

    JSONObject json = new JSONObject();
    try {
      Preconditions.checkArgument(resultingRevision >= 0, "invalid rev %s", resultingRevision);
      json.put("revision", resultingRevision);
      long sanityCheck = json.getLong("revision");
      if (sanityCheck != resultingRevision) {
        throw new AssertionError("resultingRevision " + resultingRevision
            + " not losslessly represented in JSON, got back " + sanityCheck);
      }
      json.put("sid", data.getClientId().getId());
      json.put("op", payloadJson);
      return json;
    } catch (JSONException e) {
      throw new Error(e);
    }
  }

}
