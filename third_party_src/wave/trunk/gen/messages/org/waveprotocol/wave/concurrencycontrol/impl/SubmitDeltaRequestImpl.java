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

package org.waveprotocol.wave.concurrencycontrol.impl;

import org.waveprotocol.wave.federation.ProtocolWaveletDelta;
import org.waveprotocol.wave.federation.impl.ProtocolWaveletDeltaImpl;
import org.waveprotocol.wave.concurrencycontrol.SubmitDeltaRequest;
import org.waveprotocol.wave.concurrencycontrol.SubmitDeltaRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of SubmitDeltaRequest.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class SubmitDeltaRequestImpl implements SubmitDeltaRequest {
  private String waveId;
  private String waveletId;
  private ProtocolWaveletDeltaImpl delta;
  private String channelId;
  public SubmitDeltaRequestImpl() {
  }

  public SubmitDeltaRequestImpl(SubmitDeltaRequest message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(SubmitDeltaRequest message) {
    setWaveId(message.getWaveId());
    setWaveletId(message.getWaveletId());
    setDelta(message.getDelta());
    setChannelId(message.getChannelId());
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
  public String getWaveletId() {
    return waveletId;
  }

  @Override
  public void setWaveletId(String value) {
    this.waveletId = value;
  }

  @Override
  public ProtocolWaveletDeltaImpl getDelta() {
    return new ProtocolWaveletDeltaImpl(delta);
  }

  @Override
  public void setDelta(ProtocolWaveletDelta message) {
    this.delta = new ProtocolWaveletDeltaImpl(message);
  }

  @Override
  public String getChannelId() {
    return channelId;
  }

  @Override
  public void setChannelId(String value) {
    this.channelId = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.waveId = null;
    this.waveletId = null;
    this.delta = null;
    this.channelId = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof SubmitDeltaRequestImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof SubmitDeltaRequest) {
      return SubmitDeltaRequestUtil.isEqual(this, (SubmitDeltaRequest) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return SubmitDeltaRequestUtil.getHashCode(this);
  }

}