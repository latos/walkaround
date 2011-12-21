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

import org.waveprotocol.box.common.comms.WaveletVersion;
import org.waveprotocol.box.common.comms.impl.WaveletVersionImpl;
import org.waveprotocol.box.common.comms.ProtocolOpenRequest;
import org.waveprotocol.box.common.comms.ProtocolOpenRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolOpenRequest.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public class ProtocolOpenRequestImpl implements ProtocolOpenRequest {
  private String participantId;
  private String waveId;
  private final List<String> waveletIdPrefix = new ArrayList<String>();
  private final List<WaveletVersionImpl> knownWavelet = new ArrayList<WaveletVersionImpl>();
  public ProtocolOpenRequestImpl() {
  }

  public ProtocolOpenRequestImpl(ProtocolOpenRequest message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolOpenRequest message) {
    setParticipantId(message.getParticipantId());
    setWaveId(message.getWaveId());
    clearWaveletIdPrefix();
    for (String field : message.getWaveletIdPrefix()) {
      addWaveletIdPrefix(field);
    }
    clearKnownWavelet();
    for (WaveletVersion field : message.getKnownWavelet()) {
      addKnownWavelet(new WaveletVersionImpl(field));
    }
  }

  @Override
  public String getParticipantId() {
    return participantId;
  }

  @Override
  public void setParticipantId(String value) {
    this.participantId = value;
  }

  @Override
  public String getWaveId() {
    return waveId;
  }

  @Override
  public void setWaveId(String value) {
    this.waveId = value;
  }

  @Override
  public List<String> getWaveletIdPrefix() {
    return Collections.unmodifiableList(waveletIdPrefix);
  }

  @Override
  public void addAllWaveletIdPrefix(List<String> values) {
    this.waveletIdPrefix.addAll(values);
  }

  @Override
  public String getWaveletIdPrefix(int n) {
    return waveletIdPrefix.get(n);
  }

  @Override
  public void setWaveletIdPrefix(int n, String value) {
    this.waveletIdPrefix.set(n, value);
  }

  @Override
  public int getWaveletIdPrefixSize() {
    return waveletIdPrefix.size();
  }

  @Override
  public void addWaveletIdPrefix(String value) {
    this.waveletIdPrefix.add(value);
  }

  @Override
  public void clearWaveletIdPrefix() {
    waveletIdPrefix.clear();
  }

  @Override
  public List<WaveletVersionImpl> getKnownWavelet() {
    return Collections.unmodifiableList(knownWavelet);
  }

  @Override
  public void addAllKnownWavelet(List<? extends WaveletVersion> messages) {
    for (WaveletVersion message : messages) {
      addKnownWavelet(message);
    }
  }

  @Override
  public WaveletVersionImpl getKnownWavelet(int n) {
    return new WaveletVersionImpl(knownWavelet.get(n));
  }

  @Override
  public void setKnownWavelet(int n, WaveletVersion message) {
    this.knownWavelet.set(n, new WaveletVersionImpl(message));
  }

  @Override
  public int getKnownWaveletSize() {
    return knownWavelet.size();
  }

  @Override
  public void addKnownWavelet(WaveletVersion message) {
    this.knownWavelet.add(new WaveletVersionImpl(message));
  }

  @Override
  public void clearKnownWavelet() {
    knownWavelet.clear();
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.participantId = null;
    this.waveId = null;
    this.waveletIdPrefix.clear();
    this.knownWavelet.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolOpenRequestImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolOpenRequest) {
      return ProtocolOpenRequestUtil.isEqual(this, (ProtocolOpenRequest) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolOpenRequestUtil.getHashCode(this);
  }

}