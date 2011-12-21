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

import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.wave.concurrencycontrol.OpenWaveletChannelRequest;
import org.waveprotocol.wave.concurrencycontrol.OpenWaveletChannelRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of OpenWaveletChannelRequest.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class OpenWaveletChannelRequestImpl implements OpenWaveletChannelRequest {
  private String waveId;
  private String waveletId;
  private ProtocolHashedVersionImpl beginVersion;
  public OpenWaveletChannelRequestImpl() {
  }

  public OpenWaveletChannelRequestImpl(OpenWaveletChannelRequest message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(OpenWaveletChannelRequest message) {
    setWaveId(message.getWaveId());
    setWaveletId(message.getWaveletId());
    setBeginVersion(message.getBeginVersion());
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
  public ProtocolHashedVersionImpl getBeginVersion() {
    return new ProtocolHashedVersionImpl(beginVersion);
  }

  @Override
  public void setBeginVersion(ProtocolHashedVersion message) {
    this.beginVersion = new ProtocolHashedVersionImpl(message);
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.waveId = null;
    this.waveletId = null;
    this.beginVersion = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof OpenWaveletChannelRequestImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof OpenWaveletChannelRequest) {
      return OpenWaveletChannelRequestUtil.isEqual(this, (OpenWaveletChannelRequest) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return OpenWaveletChannelRequestUtil.getHashCode(this);
  }

}