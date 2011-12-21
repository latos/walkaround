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

import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.proto.ProtocolDocumentOperationProtoImpl;
import org.waveprotocol.wave.diff.DocumentDiffSnapshot;
import org.waveprotocol.wave.diff.DocumentDiffSnapshotUtil;
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
 * Server implementation of DocumentDiffSnapshot.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
// NOTE(kalman): It would be nicer to add a proto serialisation
// utility rather than having this class at all.
public final class DocumentDiffSnapshotProtoImpl
    implements DocumentDiffSnapshot,
// Note: fully-qualified path is required for GsonSerializable and ProtoWrapper.
// An import of it is not resolved correctly from inner classes.
// This appears to be a javac bug. The Eclipse compiler handles it fine.
org.waveprotocol.wave.communication.gson.GsonSerializable,
org.waveprotocol.wave.communication.proto.ProtoWrapper<org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot> {
  private org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot proto = null;
  private org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot.Builder protoBuilder = org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot.newBuilder();
  public DocumentDiffSnapshotProtoImpl() {
  }

  public DocumentDiffSnapshotProtoImpl(org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot proto) {
    this.proto = proto;
  }

  public DocumentDiffSnapshotProtoImpl(DocumentDiffSnapshot message) {
    copyFrom(message);
  }

  @Override
  public org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot getPB() {
    switchToProto();
    return proto;
  }

  @Override
  public void setPB(org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot proto) {
    this.proto = proto;
    this.protoBuilder = null;
  }

  @Override
  public void copyFrom(DocumentDiffSnapshot message) {
    setDocumentId(message.getDocumentId());
    if (message.hasState()) {
      setState(new ProtocolDocumentOperationProtoImpl(message.getState()));
    } else {
      clearState();
    }
    if (message.hasDiff()) {
      setDiff(new ProtocolDocumentOperationProtoImpl(message.getDiff()));
    } else {
      clearDiff();
    }
    setAuthor(message.getAuthor());
    clearContributor();
    for (String field : message.getContributor()) {
      addContributor(field);
    }
    clearAddedContributor();
    for (String field : message.getAddedContributor()) {
      addAddedContributor(field);
    }
    clearRemovedContributor();
    for (String field : message.getRemovedContributor()) {
      addRemovedContributor(field);
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
  public boolean hasState() {
    switchToProto();
    return proto.hasState();
  }

  @Override
  public void clearState() {
    switchToProtoBuilder();
    protoBuilder.clearState();
  }

  @Override
  public ProtocolDocumentOperationProtoImpl getState() {
    switchToProto();
    return new ProtocolDocumentOperationProtoImpl(proto.getState());
  }

  @Override
  public void setState(ProtocolDocumentOperation value) {
    switchToProtoBuilder();
    protoBuilder.clearState();
    protoBuilder.setState(getOrCreateProtocolDocumentOperationProtoImpl(value).getPB());
  }

  @Override
  public boolean hasDiff() {
    switchToProto();
    return proto.hasDiff();
  }

  @Override
  public void clearDiff() {
    switchToProtoBuilder();
    protoBuilder.clearDiff();
  }

  @Override
  public ProtocolDocumentOperationProtoImpl getDiff() {
    switchToProto();
    return new ProtocolDocumentOperationProtoImpl(proto.getDiff());
  }

  @Override
  public void setDiff(ProtocolDocumentOperation value) {
    switchToProtoBuilder();
    protoBuilder.clearDiff();
    protoBuilder.setDiff(getOrCreateProtocolDocumentOperationProtoImpl(value).getPB());
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
  public List<String> getAddedContributor() {
    switchToProto();
    return Collections.unmodifiableList(proto.getAddedContributorList());
  }

  @Override
  public void addAllAddedContributor(List<String> values) {
    switchToProtoBuilder();
    protoBuilder.addAllAddedContributor(values);
  }

  @Override
  public String getAddedContributor(int n) {
    switchToProto();
    return proto.getAddedContributor(n);
  }

  @Override
  public void setAddedContributor(int n, String value) {
    switchToProtoBuilder();
    protoBuilder.setAddedContributor(n, value);
  }

  @Override
  public int getAddedContributorSize() {
    switchToProto();
    return proto.getAddedContributorCount();
  }

  @Override
  public void addAddedContributor(String value) {
    switchToProtoBuilder();
    protoBuilder.addAddedContributor(value);
  }

  @Override
  public void clearAddedContributor() {
    switchToProtoBuilder();
    protoBuilder.clearAddedContributor();
  }

  @Override
  public List<String> getRemovedContributor() {
    switchToProto();
    return Collections.unmodifiableList(proto.getRemovedContributorList());
  }

  @Override
  public void addAllRemovedContributor(List<String> values) {
    switchToProtoBuilder();
    protoBuilder.addAllRemovedContributor(values);
  }

  @Override
  public String getRemovedContributor(int n) {
    switchToProto();
    return proto.getRemovedContributor(n);
  }

  @Override
  public void setRemovedContributor(int n, String value) {
    switchToProtoBuilder();
    protoBuilder.setRemovedContributor(n, value);
  }

  @Override
  public int getRemovedContributorSize() {
    switchToProto();
    return proto.getRemovedContributorCount();
  }

  @Override
  public void addRemovedContributor(String value) {
    switchToProtoBuilder();
    protoBuilder.addRemovedContributor(value);
  }

  @Override
  public void clearRemovedContributor() {
    switchToProtoBuilder();
    protoBuilder.clearRemovedContributor();
  }

  @Override
  public double getLastModifiedVersion() {
    switchToProto();
    return proto.getLastModifiedVersion();
  }

  @Override
  public void setLastModifiedVersion(double value) {
    switchToProtoBuilder();
    protoBuilder.setLastModifiedVersion(Int52.int52to64(value));
  }

  @Override
  public double getLastModifiedTime() {
    switchToProto();
    return proto.getLastModifiedTime();
  }

  @Override
  public void setLastModifiedTime(double value) {
    switchToProtoBuilder();
    protoBuilder.setLastModifiedTime(Int52.int52to64(value));
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
          ? org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot.newBuilder()
          : org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot.newBuilder(proto);
      proto = null;
    }
  }

  private void invalidateAll() {
    proto = null;
    protoBuilder = org.waveprotocol.wave.diff.Diff.DocumentDiffSnapshot.newBuilder();
  }

  @Override
  public JsonElement toGson(RawStringData raw, Gson gson) {
    JsonObject json = new JsonObject();
    json.add("1", new JsonPrimitive(getDocumentId()));
    if (hasState()) {
      {
        JsonElement elem = ((GsonSerializable) getState()).toGson(raw, gson);
        json.add("2", elem);
      }
    }
    if (hasDiff()) {
      {
        JsonElement elem = ((GsonSerializable) getDiff()).toGson(raw, gson);
        json.add("21", elem);
      }
    }
    json.add("3", new JsonPrimitive(getAuthor()));
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < getContributorSize(); i++) {
        array.add(new JsonPrimitive(getContributor(i)));
      }
      json.add("4", array);
    }
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < getAddedContributorSize(); i++) {
        array.add(new JsonPrimitive(getAddedContributor(i)));
      }
      json.add("22", array);
    }
    {
      JsonArray array = new JsonArray();
      for (int i = 0; i < getRemovedContributorSize(); i++) {
        array.add(new JsonPrimitive(getRemovedContributor(i)));
      }
      json.add("23", array);
    }
    json.add("5", new JsonPrimitive(getLastModifiedVersion()));
    json.add("6", new JsonPrimitive(getLastModifiedTime()));
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
      if (!elem.isJsonNull()) {
        {
          ProtocolDocumentOperationProtoImpl payload = new ProtocolDocumentOperationProtoImpl();
          GsonUtil.extractJsonObject(payload, elem, gson, raw);
          setState(payload);
        }
      }
    }
    if (jsonObject.has("21")) {
      JsonElement elem = jsonObject.get("21");
      if (!elem.isJsonNull()) {
        {
          ProtocolDocumentOperationProtoImpl payload = new ProtocolDocumentOperationProtoImpl();
          GsonUtil.extractJsonObject(payload, elem, gson, raw);
          setDiff(payload);
        }
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
    if (jsonObject.has("22")) {
      JsonElement elem = jsonObject.get("22");
      {
        JsonArray array = elem.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          addAddedContributor(array.get(i).getAsString());
        }
      }
    }
    if (jsonObject.has("23")) {
      JsonElement elem = jsonObject.get("23");
      {
        JsonArray array = elem.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
          addRemovedContributor(array.get(i).getAsString());
        }
      }
    }
    if (jsonObject.has("5")) {
      JsonElement elem = jsonObject.get("5");
      setLastModifiedVersion(elem.getAsDouble());
    }
    if (jsonObject.has("6")) {
      JsonElement elem = jsonObject.get("6");
      setLastModifiedTime(elem.getAsDouble());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof DocumentDiffSnapshotProtoImpl) {
      return getPB().equals(((DocumentDiffSnapshotProtoImpl) o).getPB());
    } else {
      return false;
    }
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (equals(o)) {
      return true;
    } else if (o instanceof DocumentDiffSnapshot) {
      return DocumentDiffSnapshotUtil.isEqual(this, (DocumentDiffSnapshot) o);
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