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

package org.waveprotocol.wave.diff.proto;

import org.waveprotocol.wave.concurrencycontrol.WaveletVersion;
import org.waveprotocol.wave.concurrencycontrol.proto.WaveletVersionProtoImpl;
import org.waveprotocol.wave.diff.FetchDiffRequest;
import org.waveprotocol.wave.diff.FetchDiffRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import org.waveprotocol.wave.communication.Codec;
import org.waveprotocol.wave.communication.ProtoEnums;
import org.waveprotocol.wave.communication.proto.Int52;
import org.waveprotocol.wave.communication.proto.ProtoWrapper;
import org.waveprotocol.wave.communication.gson.GsonException;
import org.waveprotocol.wave.communication.gson.GsonSerializable;
import org.waveprotocol.wave.communication.gson.GsonUtil;
import org.waveprotocol.wave.communication.json.RawStringData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Server implementation of FetchDiffRequest.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
// NOTE(kalman): It would be nicer to add a proto serialisation
// utility rather than having this class at all.
public final class FetchDiffRequestProtoImpl
    implements FetchDiffRequest,
// Note: fully-qualified path is required for GsonSerializable and ProtoWrapper.
// An import of it is not resolved correctly from inner classes.
// This appears to be a javac bug. The Eclipse compiler handles it fine.
org.waveprotocol.wave.communication.gson.GsonSerializable,
org.waveprotocol.wave.communication.proto.ProtoWrapper<org.waveprotocol.wave.diff.Diff.FetchDiffRequest> {
  private org.waveprotocol.wave.diff.Diff.FetchDiffRequest proto = null;
  private org.waveprotocol.wave.diff.Diff.FetchDiffRequest.Builder protoBuilder = org.waveprotocol.wave.diff.Diff.FetchDiffRequest.newBuilder();
  public FetchDiffRequestProtoImpl() {
  }

  public FetchDiffRequestProtoImpl(org.waveprotocol.wave.diff.Diff.FetchDiffRequest proto) {
    this.proto = proto;
  }

  public FetchDiffRequestProtoImpl(FetchDiffRequest message) {
    copyFrom(message);
  }

  @Override
  public org.waveprotocol.wave.diff.Diff.FetchDiffRequest getPB() {
    switchToProto();
    return proto;
  }

  @Override
  public void setPB(org.waveprotocol.wave.diff.Diff.FetchDiffRequest proto) {
    this.proto = proto;
    this.protoBuilder = null;
  }

  @Override
  public void copyFrom(FetchDiffRequest message) {
    setWaveId(message.getWaveId());
    clearKnownWavelet();
    for (WaveletVersion field : message.getKnownWavelet()) {
      addKnownWavelet(new WaveletVersionProtoImpl(field));
    }
  }

  @Override
  public String getWaveId() {
    switchToProto();
    return proto.getWaveId();
  }

  @Override
  public void setWaveId(String value) {
    switchToProtoBuilder();
    protoBuilder.setWaveId(value);
  }

  @Override
  public List<WaveletVersionProtoImpl> getKnownWavelet() {
    switchToProto();
    List<WaveletVersionProtoImpl> list = new ArrayList<WaveletVersionProtoImpl>();
    for (int i = 0; i < getKnownWaveletSize(); i++) {
      WaveletVersionProtoImpl message = new WaveletVersionProtoImpl(proto.getKnownWavelet(i));
      list.add(message);
    }
    return list;
  }

  @Override
  public void addAllKnownWavelet(List<? extends WaveletVersion> values) {
    for (WaveletVersion message : values) {
      addKnownWavelet(message);
    }
  }

  @Override
  public WaveletVersionProtoImpl getKnownWavelet(int n) {
    switchToProto();
    return new WaveletVersionProtoImpl(proto.getKnownWavelet(n));
  }

  @Override
  public void setKnownWavelet(int n, WaveletVersion value) {
    switchToProtoBuilder();
    protoBuilder.setKnownWavelet(n, getOrCreateWaveletVersionProtoImpl(value).getPB());
  }

  @Override
  public int getKnownWaveletSize() {
    switchToProto();
    return proto.getKnownWaveletCount();
  }

  @Override
  public void addKnownWavelet(WaveletVersion value) {
    switchToProtoBuilder();
    protoBuilder.addKnownWavelet(getOrCreateWaveletVersionProtoImpl(value).getPB());
  }

  @Override
  public void clearKnownWavelet() {
    switchToProtoBuilder();
    protoBuilder.clearKnownWavelet();
  }

  /** Get or create a WaveletVersionProtoImpl from a WaveletVersion. */
  private WaveletVersionProtoImpl getOrCreateWaveletVersionProtoImpl(WaveletVersion message) {
    if (message instanceof WaveletVersionProtoImpl) {
      return (WaveletVersionProtoImpl) message;
    } else {
      WaveletVersionProtoImpl messageImpl = new WaveletVersionProtoImpl();
      messageImpl.copyFrom(message);
      return messageImpl;
    }
  }

  private void switchToProto() {
    if (proto == null) {
      proto = protoBuilder.build();
      protoBuilder = null;
    }
  }

  private void switchToProtoBuilder() {
    if (protoBuilder == null) {
      protoBuilder = (proto == null)
          ? org.waveprotocol.wave.diff.Diff.FetchDiffRequest.newBuilder()
          : org.waveprotocol.wave.diff.Diff.FetchDiffRequest.newBuilder(proto);
      proto = null;
    }
  }

  private void invalidateAll() {
    proto = null;
    protoBuilder = org.waveprotocol.wave.diff.Diff.FetchDiffRequest.newBuilder();
  }

  @Override
  public JsonElement toGson(RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    json.add("1", new JsonPrimitive(getWaveId()));
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < getKnownWaveletSize(); i++) {
        JsonElement elem = ((GsonSerializable) getKnownWavelet(i)).toGson(raw, gson);
        // NOTE(kalman): if multistage parsing worked, split point would go here.
        array.add(elem);
      }
      json.add("2", array);
    }
    return json;
  }

  @Override
  public void fromGson(JsonElement json, Gson gson, RawStringData raw) throws GsonException {
    JsonObject jsonObject = json.getAsJsonObject();
    // NOTE: always check with has(...) as the json might not have all required
    // fields set; however these (obviously) will need to be set by other means
    // before accessing this object.
    invalidateAll();
    if (jsonObject.has("1")) {
      JsonElement elem = jsonObject.get("1");
      setWaveId(elem.getAsString());
    }
    if (jsonObject.has("2")) {
      JsonElement elem = jsonObject.get("2");
      {
        JsonArray array = elem.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          WaveletVersionProtoImpl payload = new WaveletVersionProtoImpl();
          GsonUtil.extractJsonObject(payload, array.get(i), gson, raw);
          addKnownWavelet(payload);
        }
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof FetchDiffRequestProtoImpl) {
      return getPB().equals(((FetchDiffRequestProtoImpl) o).getPB());
    } else {
      return false;
    }
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (equals(o)) {
      return true;
    } else if (o instanceof FetchDiffRequest) {
      return FetchDiffRequestUtil.isEqual(this, (FetchDiffRequest) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return getPB().hashCode();
  }

  @Override
  public String toString() {
    return getPB().toString();
  }

}