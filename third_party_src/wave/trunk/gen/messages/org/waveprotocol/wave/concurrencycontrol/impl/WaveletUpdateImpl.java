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
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.impl.ProtocolWaveletDeltaImpl;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.wave.concurrencycontrol.WaveletUpdate;
import org.waveprotocol.wave.concurrencycontrol.WaveletUpdateUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of WaveletUpdate.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class WaveletUpdateImpl implements WaveletUpdate {
  private ProtocolWaveletDeltaImpl delta;
  private ProtocolHashedVersionImpl resultingVersion;
  private Long applicationTimpstamp;
  public WaveletUpdateImpl() {
  }

  public WaveletUpdateImpl(WaveletUpdate message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(WaveletUpdate message) {
    setDelta(message.getDelta());
    setResultingVersion(message.getResultingVersion());
    setApplicationTimpstamp(message.getApplicationTimpstamp());
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
  public ProtocolHashedVersionImpl getResultingVersion() {
    return new ProtocolHashedVersionImpl(resultingVersion);
  }

  @Override
  public void setResultingVersion(ProtocolHashedVersion message) {
    this.resultingVersion = new ProtocolHashedVersionImpl(message);
  }

  @Override
  public long getApplicationTimpstamp() {
    return applicationTimpstamp;
  }

  @Override
  public void setApplicationTimpstamp(long value) {
    this.applicationTimpstamp = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.delta = null;
    this.resultingVersion = null;
    this.applicationTimpstamp = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof WaveletUpdateImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof WaveletUpdate) {
      return WaveletUpdateUtil.isEqual(this, (WaveletUpdate) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return WaveletUpdateUtil.getHashCode(this);
  }

}