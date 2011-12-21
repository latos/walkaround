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

package org.waveprotocol.box.common.comms.impl;

import org.waveprotocol.box.common.comms.DocumentSnapshot;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.box.common.comms.impl.DocumentSnapshotImpl;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.box.common.comms.WaveletSnapshot;
import org.waveprotocol.box.common.comms.WaveletSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of WaveletSnapshot.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public class WaveletSnapshotImpl implements WaveletSnapshot {
  private String waveletId;
  private final List<String> participantId = new ArrayList<String>();
  private final List<DocumentSnapshotImpl> document = new ArrayList<DocumentSnapshotImpl>();
  private ProtocolHashedVersionImpl version;
  private Long lastModifiedTime;
  private String creator;
  private Long creationTime;
  public WaveletSnapshotImpl() {
  }

  public WaveletSnapshotImpl(WaveletSnapshot message) {
    copyFrom(message);
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
      addDocument(new DocumentSnapshotImpl(field));
    }
    setVersion(message.getVersion());
    setLastModifiedTime(message.getLastModifiedTime());
    setCreator(message.getCreator());
    setCreationTime(message.getCreationTime());
  }

  @Override
  public String getWaveletId() {
    return waveletId;
  }

  @Override
  public void setWaveletId(String value) {
    this.waveletId = value;
  }

  @Override
  public List<String> getParticipantId() {
    return Collections.unmodifiableList(participantId);
  }

  @Override
  public void addAllParticipantId(List<String> values) {
    this.participantId.addAll(values);
  }

  @Override
  public String getParticipantId(int n) {
    return participantId.get(n);
  }

  @Override
  public void setParticipantId(int n, String value) {
    this.participantId.set(n, value);
  }

  @Override
  public int getParticipantIdSize() {
    return participantId.size();
  }

  @Override
  public void addParticipantId(String value) {
    this.participantId.add(value);
  }

  @Override
  public void clearParticipantId() {
    participantId.clear();
  }

  @Override
  public List<DocumentSnapshotImpl> getDocument() {
    return Collections.unmodifiableList(document);
  }

  @Override
  public void addAllDocument(List<? extends DocumentSnapshot> messages) {
    for (DocumentSnapshot message : messages) {
      addDocument(message);
    }
  }

  @Override
  public DocumentSnapshotImpl getDocument(int n) {
    return new DocumentSnapshotImpl(document.get(n));
  }

  @Override
  public void setDocument(int n, DocumentSnapshot message) {
    this.document.set(n, new DocumentSnapshotImpl(message));
  }

  @Override
  public int getDocumentSize() {
    return document.size();
  }

  @Override
  public void addDocument(DocumentSnapshot message) {
    this.document.add(new DocumentSnapshotImpl(message));
  }

  @Override
  public void clearDocument() {
    document.clear();
  }

  @Override
  public ProtocolHashedVersionImpl getVersion() {
    return new ProtocolHashedVersionImpl(version);
  }

  @Override
  public void setVersion(ProtocolHashedVersion message) {
    this.version = new ProtocolHashedVersionImpl(message);
  }

  @Override
  public long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @Override
  public void setLastModifiedTime(long value) {
    this.lastModifiedTime = value;
  }

  @Override
  public String getCreator() {
    return creator;
  }

  @Override
  public void setCreator(String value) {
    this.creator = value;
  }

  @Override
  public long getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(long value) {
    this.creationTime = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.waveletId = null;
    this.participantId.clear();
    this.document.clear();
    this.version = null;
    this.lastModifiedTime = null;
    this.creator = null;
    this.creationTime = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof WaveletSnapshotImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof WaveletSnapshot) {
      return WaveletSnapshotUtil.isEqual(this, (WaveletSnapshot) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return WaveletSnapshotUtil.getHashCode(this);
  }

}