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

package org.waveprotocol.wave.concurrencycontrol.proto;

import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.proto.ProtocolDocumentOperationProtoImpl;
import org.waveprotocol.wave.concurrencycontrol.DocumentSnapshot;
import org.waveprotocol.wave.concurrencycontrol.DocumentSnapshotUtil;
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
 * Server implementation of DocumentSnapshot.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
// NOTE(kalman): It would be nicer to add a proto serialisation
// utility rather than having this class at all.
public final class DocumentSnapshotProtoImpl
    implements DocumentSnapshot,
// Note: fully-qualified path is required for GsonSerializable and ProtoWrapper.
// An import of it is not resolved correctly from inner classes.
// This appears to be a javac bug. The Eclipse compiler handles it fine.
org.waveprotocol.wave.communication.gson.GsonSerializable,
org.waveprotocol.wave.communication.proto.ProtoWrapper<org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot> {
  private org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot proto = null;
  private org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot.Builder protoBuilder = org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot.newBuilder();
  public DocumentSnapshotProtoImpl() {
  }

  public DocumentSnapshotProtoImpl(org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot proto) {
    this.proto = proto;
  }

  public DocumentSnapshotProtoImpl(DocumentSnapshot message) {
    copyFrom(message);
  }

  @Override
  public org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot getPB() {
    switchToProto();
    return proto;
  }

  @Override
  public void setPB(org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot proto) {
    this.proto = proto;
    this.protoBuilder = null;
  }

  @Override
  public void copyFrom(DocumentSnapshot message) {
    setDocumentId(message.getDocumentId());
    setDocumentOperation(new ProtocolDocumentOperationProtoImpl(message.getDocumentOperation()));
    setAuthor(message.getAuthor());
    clearContributor();
    for (String field : message.getContributor()) {
      addContributor(field);
    }
    setLastModifiedVersion(message.getLastModifiedVersion());
    setLastModifiedTime(message.getLastModifiedTime());
  }

  @Override
  public String getDocumentId() {
    switchToProto();
    return proto.getDocumentId();
  }

  @Override
  public void setDocumentId(String value) {
    switchToProtoBuilder();
    protoBuilder.setDocumentId(value);
  }

  @Override
  public ProtocolDocumentOperationProtoImpl getDocumentOperation() {
    switchToProto();
    return new ProtocolDocumentOperationProtoImpl(proto.getDocumentOperation());
  }

  @Override
  public void setDocumentOperation(ProtocolDocumentOperation value) {
    switchToProtoBuilder();
    protoBuilder.clearDocumentOperation();
    protoBuilder.setDocumentOperation(getOrCreateProtocolDocumentOperationProtoImpl(value).getPB());
  }

  @Override
  public String getAuthor() {
    switchToProto();
    return proto.getAuthor();
  }

  @Override
  public void setAuthor(String value) {
    switchToProtoBuilder();
    protoBuilder.setAuthor(value);
  }

  @Override
  public List<String> getContributor() {
    switchToProto();
    return Collections.unmodifiableList(proto.getContributorList());
  }

  @Override
  public void addAllContributor(List<String> values) {
    switchToProtoBuilder();
    protoBuilder.addAllContributor(values);
  }

  @Override
  public String getContributor(int n) {
    switchToProto();
    return proto.getContributor(n);
  }

  @Override
  public void setContributor(int n, String value) {
    switchToProtoBuilder();
    protoBuilder.setContributor(n, value);
  }

  @Override
  public int getContributorSize() {
    switchToProto();
    return proto.getContributorCount();
  }

  @Override
  public void addContributor(String value) {
    switchToProtoBuilder();
    protoBuilder.addContributor(value);
  }

  @Override
  public void clearContributor() {
    switchToProtoBuilder();
    protoBuilder.clearContributor();
  }

  @Override
  public long getLastModifiedVersion() {
    switchToProto();
    return proto.getLastModifiedVersion();
  }

  @Override
  public void setLastModifiedVersion(long value) {
    switchToProtoBuilder();
    protoBuilder.setLastModifiedVersion(value);
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

  /** Get or create a ProtocolDocumentOperationProtoImpl from a ProtocolDocumentOperation. */
  private ProtocolDocumentOperationProtoImpl getOrCreateProtocolDocumentOperationProtoImpl(ProtocolDocumentOperation message) {
    if (message instanceof ProtocolDocumentOperationProtoImpl) {
      return (ProtocolDocumentOperationProtoImpl) message;
    } else {
      ProtocolDocumentOperationProtoImpl messageImpl = new ProtocolDocumentOperationProtoImpl();
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
          ? org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot.newBuilder()
          : org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot.newBuilder(proto);
      proto = null;
    }
  }

  private void invalidateAll() {
    proto = null;
    protoBuilder = org.waveprotocol.wave.concurrencycontrol.ClientServer.DocumentSnapshot.newBuilder();
  }

  @Override
  public JsonElement toGson(RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    json.add("1", new JsonPrimitive(getDocumentId()));
    {
      JsonElement elem = ((GsonSerializable) getDocumentOperation()).toGson(raw, gson);
      json.add("2", elem);
    }
    json.add("3", new JsonPrimitive(getAuthor()));
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < getContributorSize(); i++) {
        array.add(new JsonPrimitive(getContributor(i)));
      }
      json.add("4", array);
    }
    json.add("5", GsonUtil.toJson(getLastModifiedVersion()));
    json.add("6", GsonUtil.toJson(getLastModifiedTime()));
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
      setDocumentId(elem.getAsString());
    }
    if (jsonObject.has("2")) {
      JsonElement elem = jsonObject.get("2");
      {
        ProtocolDocumentOperationProtoImpl payload = new ProtocolDocumentOperationProtoImpl();
        GsonUtil.extractJsonObject(payload, elem, gson, raw);
        setDocumentOperation(payload);
      }
    }
    if (jsonObject.has("3")) {
      JsonElement elem = jsonObject.get("3");
      setAuthor(elem.getAsString());
    }
    if (jsonObject.has("4")) {
      JsonElement elem = jsonObject.get("4");
      {
        JsonArray array = elem.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          addContributor(array.get(i).getAsString());
        }
      }
    }
    if (jsonObject.has("5")) {
      JsonElement elem = jsonObject.get("5");
      setLastModifiedVersion(GsonUtil.fromJson(elem));
    }
    if (jsonObject.has("6")) {
      JsonElement elem = jsonObject.get("6");
      setLastModifiedTime(GsonUtil.fromJson(elem));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof DocumentSnapshotProtoImpl) {
      return getPB().equals(((DocumentSnapshotProtoImpl) o).getPB());
    } else {
      return false;
    }
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (equals(o)) {
      return true;
    } else if (o instanceof DocumentSnapshot) {
      return DocumentSnapshotUtil.isEqual(this, (DocumentSnapshot) o);
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