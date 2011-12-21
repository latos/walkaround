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

package org.waveprotocol.wave.federation.proto;

import org.waveprotocol.wave.federation.ProtocolSignerInfo.HashAlgorithm;
import org.waveprotocol.wave.federation.ProtocolSignerInfo;
import org.waveprotocol.wave.federation.ProtocolSignerInfoUtil;
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
 * Server implementation of ProtocolSignerInfo.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
// NOTE(kalman): It would be nicer to add a proto serialisation
// utility rather than having this class at all.
public final class ProtocolSignerInfoProtoImpl
    implements ProtocolSignerInfo,
// Note: fully-qualified path is required for GsonSerializable and ProtoWrapper.
// An import of it is not resolved correctly from inner classes.
// This appears to be a javac bug. The Eclipse compiler handles it fine.
org.waveprotocol.wave.communication.gson.GsonSerializable,
org.waveprotocol.wave.communication.proto.ProtoWrapper<org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo> {
  private org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo proto = null;
  private org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo.Builder protoBuilder = org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo.newBuilder();
  public ProtocolSignerInfoProtoImpl() {
  }

  public ProtocolSignerInfoProtoImpl(org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo proto) {
    this.proto = proto;
  }

  public ProtocolSignerInfoProtoImpl(ProtocolSignerInfo message) {
    copyFrom(message);
  }

  @Override
  public org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo getPB() {
    switchToProto();
    return proto;
  }

  @Override
  public void setPB(org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo proto) {
    this.proto = proto;
    this.protoBuilder = null;
  }

  @Override
  public void copyFrom(ProtocolSignerInfo message) {
    setHashAlgorithm(message.getHashAlgorithm());
    setDomain(message.getDomain());
    clearCertificate();
    for (Blob field : message.getCertificate()) {
      addCertificate(field);
    }
  }

  @Override
  public HashAlgorithm getHashAlgorithm() {
    switchToProto();
    return toPojoEnumHashAlgorithm(proto.getHashAlgorithm());
  }

  @Override
  public void setHashAlgorithm(HashAlgorithm value) {
    switchToProtoBuilder();
    protoBuilder.setHashAlgorithm(toProtoEnumHashAlgorithm(value));
  }

  @Override
  public String getDomain() {
    switchToProto();
    return proto.getDomain();
  }

  @Override
  public void setDomain(String value) {
    switchToProtoBuilder();
    protoBuilder.setDomain(value);
  }

  @Override
  public List<Blob> getCertificate() {
    switchToProto();
    List<Blob> list = new ArrayList<Blob>();
    for (int i = 0; i < getCertificateSize(); i++) {
      Blob blob = new Blob(Codec.encode(proto.getCertificate(i).toByteArray()));
      list.add(blob);
    }
    return list;
  }

  @Override
  public void addAllCertificate(List<Blob> values) {
    for (Blob blob : values) {
      addCertificate(blob);
    }
  }

  @Override
  public Blob getCertificate(int n) {
    switchToProto();
    return new Blob(Codec.encode(proto.getCertificate(n).toByteArray()));
  }

  @Override
  public void setCertificate(int n, Blob value) {
    switchToProtoBuilder();
    protoBuilder.setCertificate(n, ByteString.copyFrom(Codec.decode(value.getData())));
  }

  @Override
  public int getCertificateSize() {
    switchToProto();
    return proto.getCertificateCount();
  }

  @Override
  public void addCertificate(Blob value) {
    switchToProtoBuilder();
    protoBuilder.addCertificate(ByteString.copyFrom(Codec.decode(value.getData())));
  }

  @Override
  public void clearCertificate() {
    switchToProtoBuilder();
    protoBuilder.clearCertificate();
  }

  /** Translates a pojo enum to a proto enum. */
  private org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo.HashAlgorithm toProtoEnumHashAlgorithm(HashAlgorithm value) {
    return org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo.HashAlgorithm.valueOf(value.getValue());
  }

  /** Translates a proto enum to a pojo enum. */
  private HashAlgorithm toPojoEnumHashAlgorithm(org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo.HashAlgorithm value) {
    return ProtoEnums.valOf(value.getNumber(), HashAlgorithm.values());
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
          ? org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo.newBuilder()
          : org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo.newBuilder(proto);
      proto = null;
    }
  }

  private void invalidateAll() {
    proto = null;
    protoBuilder = org.waveprotocol.wave.federation.Proto.ProtocolSignerInfo.newBuilder();
  }

  @Override
  public JsonElement toGson(RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    json.add("1", new JsonPrimitive(getHashAlgorithm().getValue()));
    json.add("2", new JsonPrimitive(getDomain()));
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < getCertificateSize(); i++) {
        array.add(new JsonPrimitive(getCertificate(i).getData()));
      }
      json.add("3", array);
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
      setHashAlgorithm(ProtoEnums.valOf(elem.getAsInt(), HashAlgorithm.values()));
    }
    if (jsonObject.has("2")) {
      JsonElement elem = jsonObject.get("2");
      setDomain(elem.getAsString());
    }
    if (jsonObject.has("3")) {
      JsonElement elem = jsonObject.get("3");
      {
        JsonArray array = elem.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          addCertificate(new Blob(array.get(i).getAsString()));
        }
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof ProtocolSignerInfoProtoImpl) {
      return getPB().equals(((ProtocolSignerInfoProtoImpl) o).getPB());
    } else {
      return false;
    }
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (equals(o)) {
      return true;
    } else if (o instanceof ProtocolSignerInfo) {
      return ProtocolSignerInfoUtil.isEqual(this, (ProtocolSignerInfo) o);
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