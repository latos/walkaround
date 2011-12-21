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

package org.waveprotocol.box.common.comms;

import org.waveprotocol.box.common.comms.WaveletVersion;
import org.waveprotocol.box.common.comms.WaveletVersionBuilder;
import org.waveprotocol.box.common.comms.ProtocolOpenRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolOpenRequests.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public final class ProtocolOpenRequestBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolOpenRequest create();
  }

  private String participantId;
  private String waveId;
  private final List<String> waveletIdPrefix = new ArrayList<String>();
  private final List<WaveletVersion> knownWavelet = new ArrayList<WaveletVersion>();
  public ProtocolOpenRequestBuilder() {
  }

  public ProtocolOpenRequestBuilder setParticipantId(String value) {
    this.participantId = value;
    return this;
  }

  public ProtocolOpenRequestBuilder setWaveId(String value) {
    this.waveId = value;
    return this;
  }

  public ProtocolOpenRequestBuilder addAllWaveletIdPrefix(List<String> values) {
    this.waveletIdPrefix.addAll(values);
    return this;
  }

  public ProtocolOpenRequestBuilder setWaveletIdPrefix(int n, String value) {
    this.waveletIdPrefix.set(n, value);
    return this;
  }

  public ProtocolOpenRequestBuilder addWaveletIdPrefix(String value) {
    this.waveletIdPrefix.add(value);
    return this;
  }

  public ProtocolOpenRequestBuilder clearWaveletIdPrefix() {
    waveletIdPrefix.clear();
    return this;
  }

  public ProtocolOpenRequestBuilder addAllKnownWavelet(List<? extends WaveletVersion> messages) {
    for (WaveletVersion message : messages) {
      addKnownWavelet(message);
    }
    return this;
  }

  public ProtocolOpenRequestBuilder setKnownWavelet(int n, WaveletVersion message) {
    this.knownWavelet.set(n, message);
    return this;
  }

  public ProtocolOpenRequestBuilder addKnownWavelet(WaveletVersion message) {
    this.knownWavelet.add(message);
    return this;
  }

  public ProtocolOpenRequestBuilder clearKnownWavelet() {
    knownWavelet.clear();
    return this;
  }

  /** Builds a {@link ProtocolOpenRequest} using this builder and a factory. */
  public ProtocolOpenRequest build(Factory factory) {
    ProtocolOpenRequest message = factory.create();
    message.setParticipantId(participantId);
    message.setWaveId(waveId);
    message.clearWaveletIdPrefix();
    message.addAllWaveletIdPrefix(waveletIdPrefix);
    message.clearKnownWavelet();
    message.addAllKnownWavelet(knownWavelet);
    return message;
  }

}