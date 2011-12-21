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

package org.waveprotocol.box.common.comms.proto;

import org.waveprotocol.box.common.comms.DocumentSnapshot;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.box.common.comms.proto.DocumentSnapshotProtoImpl;
import org.waveprotocol.wave.federation.proto.ProtocolHashedVersionProtoImpl;
import org.waveprotocol.box.common.comms.WaveletSnapshot;
import org.waveprotocol.box.common.comms.WaveletSnapshotUtil;
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
 * Server implementation of WaveletSnapshot.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
// NOTE(kalman): It would be nicer to add a proto serialisation
// utility rather than having this class at all.
public final class WaveletSnapshotProtoImpl
    implements WaveletSnapshot,
// Note: fully-qualified path is required for GsonSerializable and ProtoWrapper.
// An import of it is not resolved correctly from inner classes.
// This appears to be a javac bug. The Eclipse compiler handles it fine.
org.waveprotocol.wave.communication.gson.GsonSerializable,
org.waveprotocol.wave.communication.proto.ProtoWrapper<org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot> {
  private org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot proto = null;
  private org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot.Builder protoBuilder = org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot.newBuilder();
  public WaveletSnapshotProtoImpl() {
  }

  public WaveletSnapshotProtoImpl(org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot proto) {
    this.proto = proto;
  }

  public WaveletSnapshotProtoImpl(WaveletSnapshot message) {
    copyFrom(message);
  }

  @Override
  public org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot getPB() {
    switchToProto();
    return proto;
  }

  @Override
  public void setPB(org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot proto) {
    this.proto = proto;
    this.protoBuilder = null;
  }

  @Override
  public void copyFrom(WaveletSnapshot message) {
    setWaveletId(message.getWaveletId());
    clearParticipantId();
    for (String field : message.getParticipantId()) {
      addParticipantId(field);
    }
    clearDocument();
    for (DocumentSnapshot field : message.getDocument()) {
      addDocument(new DocumentSnapshotProtoImpl(field));
    }
    setVersion(new ProtocolHashedVersionProtoImpl(message.getVersion()));
    setLastModifiedTime(message.getLastModifiedTime());
    setCreator(message.getCreator());
    setCreationTime(message.getCreationTime());
  }

  @Override
  public String getWaveletId() {
    switchToProto();
    return proto.getWaveletId();
  }

  @Override
  public void setWaveletId(String value) {
    switchToProtoBuilder();
    protoBuilder.setWaveletId(value);
  }

  @Override
  public List<String> getParticipantId() {
    switchToProto();
    return Collections.unmodifiableList(proto.getParticipantIdList());
  }

  @Override
  public void addAllParticipantId(List<String> values) {
    switchToProtoBuilder();
    protoBuilder.addAllParticipantId(values);
  }

  @Override
  public String getParticipantId(int n) {
    switchToProto();
    return proto.getParticipantId(n);
  }

  @Override
  public void setParticipantId(int n, String value) {
    switchToProtoBuilder();
    protoBuilder.setParticipantId(n, value);
  }

  @Override
  public int getParticipantIdSize() {
    switchToProto();
    return proto.getParticipantIdCount();
  }

  @Override
  public void addParticipantId(String value) {
    switchToProtoBuilder();
    protoBuilder.addParticipantId(value);
  }

  @Override
  public void clearParticipantId() {
    switchToProtoBuilder();
    protoBuilder.clearParticipantId();
  }

  @Override
  public List<DocumentSnapshotProtoImpl> getDocument() {
    switchToProto();
    List<DocumentSnapshotProtoImpl> list = new ArrayList<DocumentSnapshotProtoImpl>();
    for (int i = 0; i < getDocumentSize(); i++) {
      DocumentSnapshotProtoImpl message = new DocumentSnapshotProtoImpl(proto.getDocument(i));
      list.add(message);
    }
    return list;
  }

  @Override
  public void addAllDocument(List<? extends DocumentSnapshot> values) {
    for (DocumentSnapshot message : values) {
      addDocument(message);
    }
  }

  @Override
  public DocumentSnapshotProtoImpl getDocument(int n) {
    switchToProto();
    return new DocumentSnapshotProtoImpl(proto.getDocument(n));
  }

  @Override
  public void setDocument(int n, DocumentSnapshot value) {
    switchToProtoBuilder();
    protoBuilder.setDocument(n, getOrCreateDocumentSnapshotProtoImpl(value).getPB());
  }

  @Override
  public int getDocumentSize() {
    switchToProto();
    return proto.getDocumentCount();
  }

  @Override
  public void addDocument(DocumentSnapshot value) {
    switchToProtoBuilder();
    protoBuilder.addDocument(getOrCreateDocumentSnapshotProtoImpl(value).getPB());
  }

  @Override
  public void clearDocument() {
    switchToProtoBuilder();
    protoBuilder.clearDocument();
  }

  @Override
  public ProtocolHashedVersionProtoImpl getVersion() {
    switchToProto();
    return new ProtocolHashedVersionProtoImpl(proto.getVersion());
  }

  @Override
  public void setVersion(ProtocolHashedVersion value) {
    switchToProtoBuilder();
    protoBuilder.clearVersion();
    protoBuilder.setVersion(getOrCreateProtocolHashedVersionProtoImpl(value).getPB());
  }

  @Override
  public long getLastModifiedTime() {
    switchToProto();
    return proto.getLastModifiedTime();
  }

  @Override
  public void setLastModifiedTime(long value) {
    switchToProtoBuilder();
    protoBuilder.setLastModifiedTime(value);
  }

  @Override
  public String getCreator() {
    switchToProto();
    return proto.getCreator();
  }

  @Override
  public void setCreator(String value) {
    switchToProtoBuilder();
    protoBuilder.setCreator(value);
  }

  @Override
  public long getCreationTime() {
    switchToProto();
    return proto.getCreationTime();
  }

  @Override
  public void setCreationTime(long value) {
    switchToProtoBuilder();
    protoBuilder.setCreationTime(value);
  }

  /** Get or create a DocumentSnapshotProtoImpl from a DocumentSnapshot. */
  private DocumentSnapshotProtoImpl getOrCreateDocumentSnapshotProtoImpl(DocumentSnapshot message) {
    if (message instanceof DocumentSnapshotProtoImpl) {
      return (DocumentSnapshotProtoImpl) message;
    } else {
      DocumentSnapshotProtoImpl messageImpl = new DocumentSnapshotProtoImpl();
      messageImpl.copyFrom(message);
      return messageImpl;
    }
  }

  /** Get or create a ProtocolHashedVersionProtoImpl from a ProtocolHashedVersion. */
  private ProtocolHashedVersionProtoImpl getOrCreateProtocolHashedVersionProtoImpl(ProtocolHashedVersion message) {
    if (message instanceof ProtocolHashedVersionProtoImpl) {
      return (ProtocolHashedVersionProtoImpl) message;
    } else {
      ProtocolHashedVersionProtoImpl messageImpl = new ProtocolHashedVersionProtoImpl();
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
          ? org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot.newBuilder()
          : org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot.newBuilder(proto);
      proto = null;
    }
  }

  private void invalidateAll() {
    proto = null;
    protoBuilder = org.waveprotocol.box.common.comms.WaveClientRpc.WaveletSnapshot.newBuilder();
  }

  @Override
  public JsonElement toGson(RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    json.add("1", new JsonPrimitive(getWaveletId()));
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < getParticipantIdSize(); i++) {
        array.add(new JsonPrimitive(getParticipantId(i)));
      }
      json.add("2", array);
    }
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < getDocumentSize(); i++) {
        JsonElement elem = ((GsonSerializable) getDocument(i)).toGson(raw, gson);
        // NOTE(kalman): if multistage parsing worked, split point would go here.
        array.add(elem);
      }
      json.add("3", array);
    }
    {
      JsonElement elem = ((GsonSerializable) getVersion()).toGson(raw, gson);
      json.add("4", elem);
    }
    json.add("5", GsonUtil.toJson(getLastModifiedTime()));
    json.add("6", new JsonPrimitive(getCreator()));
    json.add("7", GsonUtil.toJson(getCreationTime()));
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
      setWaveletId(elem.getAsString());
    }
    if (jsonObject.has("2")) {
      JsonElement elem = jsonObject.get("2");
      {
        JsonArray array = elem.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          addParticipantId(array.get(i).getAsString());
        }
      }
    }
    if (jsonObject.has("3")) {
      JsonElement elem = jsonObject.get("3");
      {
        JsonArray array = elem.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          DocumentSnapshotProtoImpl payload = new DocumentSnapshotProtoImpl();
          GsonUtil.extractJsonObject(payload, array.get(i), gson, raw);
          addDocument(payload);
        }
      }
    }
    if (jsonObject.has("4")) {
      JsonElement elem = jsonObject.get("4");
      {
        ProtocolHashedVersionProtoImpl payload = new ProtocolHashedVersionProtoImpl();
        GsonUtil.extractJsonObject(payload, elem, gson, raw);
        setVersion(payload);
      }
    }
    if (jsonObject.has("5")) {
      JsonElement elem = jsonObject.get("5");
      setLastModifiedTime(GsonUtil.fromJson(elem));
    }
    if (jsonObject.has("6")) {
      JsonElement elem = jsonObject.get("6");
      setCreator(elem.getAsString());
    }
    if (jsonObject.has("7")) {
      JsonElement elem = jsonObject.get("7");
      setCreationTime(GsonUtil.fromJson(elem));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof WaveletSnapshotProtoImpl) {
      return getPB().equals(((WaveletSnapshotProtoImpl) o).getPB());
    } else {
      return false;
    }
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (equals(o)) {
      return true;
    } else if (o instanceof WaveletSnapshot) {
      return WaveletSnapshotUtil.isEqual(this, (WaveletSnapshot) o);
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