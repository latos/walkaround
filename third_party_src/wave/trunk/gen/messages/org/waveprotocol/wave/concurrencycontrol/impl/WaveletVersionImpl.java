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
import org.waveprotocol.wave.concurrencycontrol.WaveletVersion;
import org.waveprotocol.wave.concurrencycontrol.WaveletVersionUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of WaveletVersion.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class WaveletVersionImpl implements WaveletVersion {
  private String waveletId;
  private ProtocolHashedVersionImpl version;
  public WaveletVersionImpl() {
  }

  public WaveletVersionImpl(WaveletVersion message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(WaveletVersion message) {
    setWaveletId(message.getWaveletId());
    setVersion(message.getVersion());
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
  public ProtocolHashedVersionImpl getVersion() {
    return new ProtocolHashedVersionImpl(version);
  }

  @Override
  public void setVersion(ProtocolHashedVersion message) {
    this.version = new ProtocolHashedVersionImpl(message);
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.waveletId = null;
    this.version = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof WaveletVersionImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof WaveletVersion) {
      return WaveletVersionUtil.isEqual(this, (WaveletVersion) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return WaveletVersionUtil.getHashCode(this);
  }

}