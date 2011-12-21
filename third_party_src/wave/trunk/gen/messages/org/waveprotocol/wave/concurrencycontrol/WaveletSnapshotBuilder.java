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

package org.waveprotocol.wave.concurrencycontrol;

import org.waveprotocol.wave.concurrencycontrol.DocumentSnapshot;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.concurrencycontrol.DocumentSnapshotBuilder;
import org.waveprotocol.wave.federation.ProtocolHashedVersionBuilder;
import org.waveprotocol.wave.concurrencycontrol.WaveletSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for WaveletSnapshots.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public final class WaveletSnapshotBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    WaveletSnapshot create();
  }

  private String waveletId;
  private final List<String> participant = new ArrayList<String>();
  private final List<DocumentSnapshot> document = new ArrayList<DocumentSnapshot>();
  private ProtocolHashedVersion version;
  private Long lastModifiedTime;
  private String creator;
  private Long creationTime;
  public WaveletSnapshotBuilder() {
  }

  public WaveletSnapshotBuilder setWaveletId(String value) {
    this.waveletId = value;
    return this;
  }

  public WaveletSnapshotBuilder addAllParticipant(List<String> values) {
    this.participant.addAll(values);
    return this;
  }

  public WaveletSnapshotBuilder setParticipant(int n, String value) {
    this.participant.set(n, value);
    return this;
  }

  public WaveletSnapshotBuilder addParticipant(String value) {
    this.participant.add(value);
    return this;
  }

  public WaveletSnapshotBuilder clearParticipant() {
    participant.clear();
    return this;
  }

  public WaveletSnapshotBuilder addAllDocument(List<? extends DocumentSnapshot> messages) {
    for (DocumentSnapshot message : messages) {
      addDocument(message);
    }
    return this;
  }

  public WaveletSnapshotBuilder setDocument(int n, DocumentSnapshot message) {
    this.document.set(n, message);
    return this;
  }

  public WaveletSnapshotBuilder addDocument(DocumentSnapshot message) {
    this.document.add(message);
    return this;
  }

  public WaveletSnapshotBuilder clearDocument() {
    document.clear();
    return this;
  }

  public WaveletSnapshotBuilder setVersion(ProtocolHashedVersion message) {
    this.version = message;
    return this;
  }

  public WaveletSnapshotBuilder setLastModifiedTime(long value) {
    this.lastModifiedTime = value;
    return this;
  }

  public WaveletSnapshotBuilder setCreator(String value) {
    this.creator = value;
    return this;
  }

  public WaveletSnapshotBuilder setCreationTime(long value) {
    this.creationTime = value;
    return this;
  }

  /** Builds a {@link WaveletSnapshot} using this builder and a factory. */
  public WaveletSnapshot build(Factory factory) {
    WaveletSnapshot message = factory.create();
    message.setWaveletId(waveletId);
    message.clearParticipant();
    message.addAllParticipant(participant);
    message.clearDocument();
    message.addAllDocument(document);
    message.setVersion(version);
    message.setLastModifiedTime(lastModifiedTime);
    message.setCreator(creator);
    message.setCreationTime(creationTime);
    return message;
  }

}