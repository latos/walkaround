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

package org.waveprotocol.wave.federation.gson;

// Import order matters here due to what looks like a javac bug.
// Eclipse doesn't seem to have this problem.
import org.waveprotocol.wave.communication.gson.GsonSerializable;
import org.waveprotocol.wave.federation.ProtocolWaveletDelta;
import org.waveprotocol.wave.federation.ProtocolWaveletDeltaUtil;
import org.waveprotocol.wave.federation.impl.ProtocolWaveletDeltaImpl;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.ProtocolWaveletOperation;
import org.waveprotocol.wave.federation.gson.ProtocolHashedVersionGsonImpl;
import org.waveprotocol.wave.federation.gson.ProtocolWaveletOperationGsonImpl;
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
 * Pojo implementation of ProtocolWaveletDelta with gson serialization and deserialization.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolWaveletDeltaGsonImpl extends ProtocolWaveletDeltaImpl
    implements GsonSerializable {
  public ProtocolWaveletDeltaGsonImpl() {
    super();
  }

  public ProtocolWaveletDeltaGsonImpl(ProtocolWaveletDelta message) {
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
  public static JsonElement toGsonHelper(ProtocolWaveletDelta message, RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    json.add("1", ProtocolHashedVersionGsonImpl.toGsonHelper(message.getHashedVersion(), raw, gson));
    json.add("2", new JsonPrimitive(message.getAuthor()));
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < message.getOperationSize(); i++) {
        // NOTE(kalman): if multistage parsing worked, we would add split points here.
        array.add(ProtocolWaveletOperationGsonImpl.toGsonHelper(message.getOperation(i), raw, gson));
      }
      json.add("3", array);
    }
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < message.getAddressPathSize(); i++) {
        array.add(new JsonPrimitive(message.getAddressPath(i)));
      }
      json.add("4", array);
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
      ProtocolHashedVersionGsonImpl payload = new ProtocolHashedVersionGsonImpl();
      GsonUtil.extractJsonObject(payload, jsonObject.get("1"), gson, raw);
      setHashedVersion(payload);
    }
    if (jsonObject.has("2")) {
      setAuthor(jsonObject.get("2").getAsString());
    }
    if (jsonObject.has("3")) {
      JsonArray array = jsonObject.get("3").getAsJsonArray();
      for (int i = 0; i < array.size(); i++) {
        ProtocolWaveletOperationGsonImpl payload = new ProtocolWaveletOperationGsonImpl();
        GsonUtil.extractJsonObject(payload, array.get(i), gson, raw);
        addOperation(payload);
      }
    }
    if (jsonObject.has("4")) {
      JsonArray array = jsonObject.get("4").getAsJsonArray();
      for (int i = 0; i < array.size(); i++) {
        addAddressPath(array.get(i).getAsString());
      }
    }
  }

}