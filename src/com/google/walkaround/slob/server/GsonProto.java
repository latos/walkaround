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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.walkaround.slob.shared.MessageException;

import org.waveprotocol.wave.communication.gson.GsonException;
import org.waveprotocol.wave.communication.gson.GsonSerializable;
import org.waveprotocol.wave.communication.json.RawStringData;

/**
 * Utilities for JSON protobuf handling based on GSON.
 *
 * @author piotrkaleta@google.com (Piotr Kaleta)
 * @author ohler@google.com (Christian Ohler)
 */
public class GsonProto {

  private GsonProto() {}

  public enum JsonStrategy {
    REGULAR, MULTISTAGE,
  }

  public static <T extends GsonSerializable> T fromGson(T gsonObj, String serialized)
      throws MessageException {
    Gson gson = new Gson();
    JsonElement jsonElement = new JsonParser().parse(serialized);
    try {
      gsonObj.fromGson(jsonElement, gson, null);
      return gsonObj;
    } catch (RuntimeException e) {
      // I've seen IllegalStateExceptions in JsonArray.getAsString().
      throw new MessageException("Failed to parse " + gsonObj.getClass() + ": " + serialized, e);
    } catch (GsonException e) {
      throw new MessageException("Failed to parse " + gsonObj.getClass() + ": " + serialized, e);
    }
  }

  public static String toJson(GsonSerializable message, JsonStrategy strategy) {
    Gson gson = new Gson();
    // TODO(piotrkaleta): Do we need multistage strategy at all?
    switch (strategy) {
      case REGULAR:
        return gson.toJson(message.toGson(null, gson));
      case MULTISTAGE:
        RawStringData data = new RawStringData();
        JsonElement rootElement = message.toGson(data, gson);
        data.setBaseStringIndex(data.addString(gson.toJson(rootElement)));
        return data.serialize();
      default:
        throw new AssertionError("Unknown JsonStrategy: " + strategy);
    }
  }

  /**
   * Helper method to turn Gson object into JSON representation
   *
   * @return String containing JSON representation of an object
   */
  public static String toJson(GsonSerializable message) {
    return toJson(message, JsonStrategy.REGULAR);
  }

}
