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

package org.waveprotocol.wave.diff.impl;

import org.waveprotocol.wave.diff.DocumentDiffSnapshot;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.diff.impl.DocumentDiffSnapshotImpl;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.wave.diff.WaveletDiffSnapshot;
import org.waveprotocol.wave.diff.WaveletDiffSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of WaveletDiffSnapshot.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
public class WaveletDiffSnapshotImpl implements WaveletDiffSnapshot {
  private String waveletId;
  private final List<String> participant = new ArrayList<String>();
  private final List<String> addedParticipant = new ArrayList<String>();
  private final List<String> removedParticipant = new ArrayList<String>();
  private final List<DocumentDiffSnapshotImpl> document = new ArrayList<DocumentDiffSnapshotImpl>();
  private ProtocolHashedVersionImpl version;
  private Double lastModifiedTime;
  private String creator;
  private Double creationTime;
  public WaveletDiffSnapshotImpl() {
  }

  public WaveletDiffSnapshotImpl(WaveletDiffSnapshot message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(WaveletDiffSnapshot message) {
    setWaveletId(message.getWaveletId());
    clearParticipant();
    for (String field : message.getParticipant()) {
      addParticipant(field);
    }
    clearAddedParticipant();
    for (String field : message.getAddedParticipant()) {
      addAddedParticipant(field);
    }
    clearRemovedParticipant();
    for (String field : message.getRemovedParticipant()) {
      addRemovedParticipant(field);
    }
    clearDocument();
    for (DocumentDiffSnapshot field : message.getDocument()) {
      addDocument(new DocumentDiffSnapshotImpl(field));
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
  public List<String> getParticipant() {
    return Collections.unmodifiableList(participant);
  }

  @Override
  public void addAllParticipant(List<String> values) {
    this.participant.addAll(values);
  }

  @Override
  public String getParticipant(int n) {
    return participant.get(n);
  }

  @Override
  public void setParticipant(int n, String value) {
    this.participant.set(n, value);
  }

  @Override
  public int getParticipantSize() {
    return participant.size();
  }

  @Override
  public void addParticipant(String value) {
    this.participant.add(value);
  }

  @Override
  public void clearParticipant() {
    participant.clear();
  }

  @Override
  public List<String> getAddedParticipant() {
    return Collections.unmodifiableList(addedParticipant);
  }

  @Override
  public void addAllAddedParticipant(List<String> values) {
    this.addedParticipant.addAll(values);
  }

  @Override
  public String getAddedParticipant(int n) {
    return addedParticipant.get(n);
  }

  @Override
  public void setAddedParticipant(int n, String value) {
    this.addedParticipant.set(n, value);
  }

  @Override
  public int getAddedParticipantSize() {
    return addedParticipant.size();
  }

  @Override
  public void addAddedParticipant(String value) {
    this.addedParticipant.add(value);
  }

  @Override
  public void clearAddedParticipant() {
    addedParticipant.clear();
  }

  @Override
  public List<String> getRemovedParticipant() {
    return Collections.unmodifiableList(removedParticipant);
  }

  @Override
  public void addAllRemovedParticipant(List<String> values) {
    this.removedParticipant.addAll(values);
  }

  @Override
  public String getRemovedParticipant(int n) {
    return removedParticipant.get(n);
  }

  @Override
  public void setRemovedParticipant(int n, String value) {
    this.removedParticipant.set(n, value);
  }

  @Override
  public int getRemovedParticipantSize() {
    return removedParticipant.size();
  }

  @Override
  public void addRemovedParticipant(String value) {
    this.removedParticipant.add(value);
  }

  @Override
  public void clearRemovedParticipant() {
    removedParticipant.clear();
  }

  @Override
  public List<DocumentDiffSnapshotImpl> getDocument() {
    return Collections.unmodifiableList(document);
  }

  @Override
  public void addAllDocument(List<? extends DocumentDiffSnapshot> messages) {
    for (DocumentDiffSnapshot message : messages) {
      addDocument(message);
    }
  }

  @Override
  public DocumentDiffSnapshotImpl getDocument(int n) {
    return new DocumentDiffSnapshotImpl(document.get(n));
  }

  @Override
  public void setDocument(int n, DocumentDiffSnapshot message) {
    this.document.set(n, new DocumentDiffSnapshotImpl(message));
  }

  @Override
  public int getDocumentSize() {
    return document.size();
  }

  @Override
  public void addDocument(DocumentDiffSnapshot message) {
    this.document.add(new DocumentDiffSnapshotImpl(message));
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
  public double getLastModifiedTime() {
    return lastModifiedTime;
  }

  @Override
  public void setLastModifiedTime(double value) {
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
  public double getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(double value) {
    this.creationTime = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.waveletId = null;
    this.participant.clear();
    this.addedParticipant.clear();
    this.removedParticipant.clear();
    this.document.clear();
    this.version = null;
    this.lastModifiedTime = null;
    this.creator = null;
    this.creationTime = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof WaveletDiffSnapshotImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof WaveletDiffSnapshot) {
      return WaveletDiffSnapshotUtil.isEqual(this, (WaveletDiffSnapshot) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return WaveletDiffSnapshotUtil.getHashCode(this);
  }

}