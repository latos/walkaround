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

package org.waveprotocol.box.search.gson;

// Import order matters here due to what looks like a javac bug.
// Eclipse doesn't seem to have this problem.
import org.waveprotocol.wave.communication.gson.GsonSerializable;
import org.waveprotocol.box.search.SearchResponse;
import org.waveprotocol.box.search.SearchResponseUtil;
import org.waveprotocol.box.search.impl.SearchResponseImpl;
import org.waveprotocol.box.search.SearchResponse.Digest;
import org.waveprotocol.box.search.SearchResponseUtil.DigestUtil;
import org.waveprotocol.box.search.impl.SearchResponseImpl.DigestImpl;
import org.waveprotocol.box.search.SearchResponse.Digest;
import org.waveprotocol.box.search.gson.SearchResponseGsonImpl.DigestGsonImpl;
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
 * Pojo implementation of SearchResponse with gson serialization and deserialization.
 *
 * Generated from org/waveprotocol/box/search/search.proto. Do not edit.
 */
public final class SearchResponseGsonImpl extends SearchResponseImpl
    implements GsonSerializable {

  public static final class DigestGsonImpl extends DigestImpl
      implements GsonSerializable {
    public DigestGsonImpl() {
      super();
    }

    public DigestGsonImpl(Digest message) {
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
    public static JsonElement toGsonHelper(Digest message, RawStringData raw, Gson gson) {
      JsonObject json = new JsonObject();
      json.add("1", new JsonPrimitive(message.getTitle()));
      json.add("2", new JsonPrimitive(message.getSnippet()));
      json.add("3", new JsonPrimitive(message.getWaveId()));
      json.add("4", GsonUtil.toJson(message.getLastModified()));
      json.add("5", new JsonPrimitive(message.getUnreadCount()));
      json.add("6", new JsonPrimitive(message.getBlipCount()));
      {
        JsonArray array = new JsonArray();
        for (int i = 0; i < message.getParticipantsSize(); i++) {
          array.add(new JsonPrimitive(message.getParticipants(i)));
        }
        json.add("7", array);
      }
      json.add("8", new JsonPrimitive(message.getAuthor()));
      return json;
    }

    @Override
    public void fromGson(JsonElement json, Gson gson, RawStringData raw) throws GsonException {
      reset();
      JsonObject jsonObject = json.getAsJsonObject();
      // NOTE: always check with has(...) as the json might not have all required
      // fields set.
      if (jsonObject.has("1")) {
        setTitle(jsonObject.get("1").getAsString());
      }
      if (jsonObject.has("2")) {
        setSnippet(jsonObject.get("2").getAsString());
      }
      if (jsonObject.has("3")) {
        setWaveId(jsonObject.get("3").getAsString());
      }
      if (jsonObject.has("4")) {
        setLastModified(GsonUtil.fromJson(jsonObject.get("4")));
      }
      if (jsonObject.has("5")) {
        setUnreadCount(jsonObject.get("5").getAsInt());
      }
      if (jsonObject.has("6")) {
        setBlipCount(jsonObject.get("6").getAsInt());
      }
      if (jsonObject.has("7")) {
        JsonArray array = jsonObject.get("7").getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          addParticipants(array.get(i).getAsString());
        }
      }
      if (jsonObject.has("8")) {
        setAuthor(jsonObject.get("8").getAsString());
      }
    }

  }

  public SearchResponseGsonImpl() {
    super();
  }

  public SearchResponseGsonImpl(SearchResponse message) {
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
  public static JsonElement toGsonHelper(SearchResponse message, RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    json.add("1", new JsonPrimitive(message.getQuery()));
    json.add("2", new JsonPrimitive(message.getTotalResults()));
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < message.getDigestsSize(); i++) {
        // NOTE(kalman): if multistage parsing worked, we would add split points here.
        array.add(DigestGsonImpl.toGsonHelper(message.getDigests(i), raw, gson));
      }
      json.add("3", array);
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
      setQuery(jsonObject.get("1").getAsString());
    }
    if (jsonObject.has("2")) {
      setTotalResults(jsonObject.get("2").getAsInt());
    }
    if (jsonObject.has("3")) {
      JsonArray array = jsonObject.get("3").getAsJsonArray();
      for (int i = 0; i < array.size(); i++) {
        DigestGsonImpl payload = new DigestGsonImpl();
        GsonUtil.extractJsonObject(payload, array.get(i), gson, raw);
        addDigests(payload);
      }
    }
  }

}