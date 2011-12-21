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
import org.waveprotocol.wave.federation.ProtocolWaveletOperation;
import org.waveprotocol.wave.federation.ProtocolWaveletOperationUtil;
import org.waveprotocol.wave.federation.impl.ProtocolWaveletOperationImpl;
import org.waveprotocol.wave.federation.ProtocolWaveletOperation.MutateDocument;
import org.waveprotocol.wave.federation.ProtocolWaveletOperationUtil.MutateDocumentUtil;
import org.waveprotocol.wave.federation.impl.ProtocolWaveletOperationImpl.MutateDocumentImpl;
import org.waveprotocol.wave.federation.ProtocolWaveletOperation.MutateDocument;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.gson.ProtocolWaveletOperationGsonImpl.MutateDocumentGsonImpl;
import org.waveprotocol.wave.federation.gson.ProtocolDocumentOperationGsonImpl;
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
 * Pojo implementation of ProtocolWaveletOperation with gson serialization and deserialization.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolWaveletOperationGsonImpl extends ProtocolWaveletOperationImpl
    implements GsonSerializable {

  public static final class MutateDocumentGsonImpl extends MutateDocumentImpl
      implements GsonSerializable {
    public MutateDocumentGsonImpl() {
      super();
    }

    public MutateDocumentGsonImpl(MutateDocument message) {
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
    public static JsonElement toGsonHelper(MutateDocument message, RawStringData raw, Gson gson) {
      JsonObject json = new JsonObject();
      json.add("1", new JsonPrimitive(message.getDocumentId()));
      json.add("2", ProtocolDocumentOperationGsonImpl.toGsonHelper(message.getDocumentOperation(), raw, gson));
      return json;
    }

    @Override
    public void fromGson(JsonElement json, Gson gson, RawStringData raw) throws GsonException {
      reset();
      JsonObject jsonObject = json.getAsJsonObject();
      // NOTE: always check with has(...) as the json might not have all required
      // fields set.
      if (jsonObject.has("1")) {
        setDocumentId(jsonObject.get("1").getAsString());
      }
      if (jsonObject.has("2")) {
        ProtocolDocumentOperationGsonImpl payload = new ProtocolDocumentOperationGsonImpl();
        GsonUtil.extractJsonObject(payload, jsonObject.get("2"), gson, raw);
        setDocumentOperation(payload);
      }
    }

  }

  public ProtocolWaveletOperationGsonImpl() {
    super();
  }

  public ProtocolWaveletOperationGsonImpl(ProtocolWaveletOperation message) {
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
  public static JsonElement toGsonHelper(ProtocolWaveletOperation message, RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    if (message.hasAddParticipant()) {
      json.add("1", new JsonPrimitive(message.getAddParticipant()));
    }
    if (message.hasRemoveParticipant()) {
      json.add("2", new JsonPrimitive(message.getRemoveParticipant()));
    }
    if (message.hasMutateDocument()) {
      json.add("3", MutateDocumentGsonImpl.toGsonHelper(message.getMutateDocument(), raw, gson));
    }
    if (message.hasNoOp()) {
      json.add("4", new JsonPrimitive(message.getNoOp()));
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
      JsonElement elem = jsonObject.get("1");
      if (!elem.isJsonNull()) {
        setAddParticipant(elem.getAsString());
      }
    }
    if (jsonObject.has("2")) {
      JsonElement elem = jsonObject.get("2");
      if (!elem.isJsonNull()) {
        setRemoveParticipant(elem.getAsString());
      }
    }
    if (jsonObject.has("3")) {
      JsonElement elem = jsonObject.get("3");
      if (!elem.isJsonNull()) {
        MutateDocumentGsonImpl payload = new MutateDocumentGsonImpl();
        GsonUtil.extractJsonObject(payload, elem, gson, raw);
        setMutateDocument(payload);
      }
    }
    if (jsonObject.has("4")) {
      JsonElement elem = jsonObject.get("4");
      if (!elem.isJsonNull()) {
        setNoOp(elem.getAsBoolean());
      }
    }
  }

}