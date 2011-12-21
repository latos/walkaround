/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.waveprotocol.box.common.comms.gson;

// Import order matters here due to what looks like a javac bug.
// Eclipse doesn't seem to have this problem.
import org.waveprotocol.wave.communication.gson.GsonSerializable;
import org.waveprotocol.box.common.comms.ProtocolSubmitResponse;
import org.waveprotocol.box.common.comms.ProtocolSubmitResponseUtil;
import org.waveprotocol.box.common.comms.impl.ProtocolSubmitResponseImpl;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.gson.ProtocolHashedVersionGsonImpl;
import org.waveprotocol.wave.communication.Blob;
import org.waveprotocol.wave.communication.ProtoEnums;
import org.waveprotocol.wave.communication.gson.GsonException;
import org.waveprotocol.wave.communication.gson.GsonUtil;
import org.waveprotocol.wave.communication.json.RawStringData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Pojo implementation of ProtocolSubmitResponse with gson serialization and deserialization.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public final class ProtocolSubmitResponseGsonImpl extends ProtocolSubmitResponseImpl
    implements GsonSerializable {
  public ProtocolSubmitResponseGsonImpl() {
    super();
  }

  public ProtocolSubmitResponseGsonImpl(ProtocolSubmitResponse message) {
    super(message);
  }

  @Override
  public JsonElement toGson(RawStringData raw, Gson gson) {
    return toGsonHelper(this, raw, gson);
  }

  /**
   * Static implementation-independent GSON serializer. Call this from
   * {@link #toGson} to avoid subclassing issues with inner message types.
   */
  public static JsonElement toGsonHelper(ProtocolSubmitResponse message, RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    json.add("1", new JsonPrimitive(message.getOperationsApplied()));
    if (message.hasErrorMessage()) {
      json.add("2", new JsonPrimitive(message.getErrorMessage()));
    }
    if (message.hasHashedVersionAfterApplication()) {
      json.add("3", ProtocolHashedVersionGsonImpl.toGsonHelper(message.getHashedVersionAfterApplication(), raw, gson));
    }
    return json;
  }

  @Override
  public void fromGson(JsonElement json, Gson gson, RawStringData raw) throws GsonException {
    reset();
    JsonObject jsonObject = json.getAsJsonObject();
    // NOTE: always check with has(...) as the json might not have all required
    // fields set.
    if (jsonObject.has("1")) {
      setOperationsApplied(jsonObject.get("1").getAsInt());
    }
    if (jsonObject.has("2")) {
      JsonElement elem = jsonObject.get("2");
      if (!elem.isJsonNull()) {
        setErrorMessage(elem.getAsString());
      }
    }
    if (jsonObject.has("3")) {
      JsonElement elem = jsonObject.get("3");
      if (!elem.isJsonNull()) {
        ProtocolHashedVersionGsonImpl payload = new ProtocolHashedVersionGsonImpl();
        GsonUtil.extractJsonObject(payload, elem, gson, raw);
        setHashedVersionAfterApplication(payload);
      }
    }
  }

}