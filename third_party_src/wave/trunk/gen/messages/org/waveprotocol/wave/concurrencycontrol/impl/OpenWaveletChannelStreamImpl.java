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

import org.waveprotocol.wave.concurrencycontrol.WaveletUpdate;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.concurrencycontrol.WaveletChannelTerminator;
import org.waveprotocol.wave.concurrencycontrol.impl.WaveletUpdateImpl;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.wave.concurrencycontrol.impl.WaveletChannelTerminatorImpl;
import org.waveprotocol.wave.concurrencycontrol.OpenWaveletChannelStream;
import org.waveprotocol.wave.concurrencycontrol.OpenWaveletChannelStreamUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of OpenWaveletChannelStream.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class OpenWaveletChannelStreamImpl implements OpenWaveletChannelStream {
  private String channelId;
  private WaveletUpdateImpl delta;
  private ProtocolHashedVersionImpl commitVersion;
  private WaveletChannelTerminatorImpl terminator;
  public OpenWaveletChannelStreamImpl() {
  }

  public OpenWaveletChannelStreamImpl(OpenWaveletChannelStream message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(OpenWaveletChannelStream message) {
    if (message.hasChannelId()) {
      setChannelId(message.getChannelId());
    } else {
      clearChannelId();
    }
    if (message.hasDelta()) {
      setDelta(message.getDelta());
    } else {
      clearDelta();
    }
    if (message.hasCommitVersion()) {
      setCommitVersion(message.getCommitVersion());
    } else {
      clearCommitVersion();
    }
    if (message.hasTerminator()) {
      setTerminator(message.getTerminator());
    } else {
      clearTerminator();
    }
  }

  @Override
  public boolean hasChannelId() {
    return channelId != null;
  }

  @Override
  public void clearChannelId() {
    channelId = null;
  }

  @Override
  public String getChannelId() {
    return channelId;
  }

  @Override
  public void setChannelId(String value) {
    this.channelId = value;
  }

  @Override
  public boolean hasDelta() {
    return delta != null;
  }

  @Override
  public void clearDelta() {
    delta = null;
  }

  @Override
  public WaveletUpdateImpl getDelta() {
    return new WaveletUpdateImpl(delta);
  }

  @Override
  public void setDelta(WaveletUpdate message) {
    this.delta = new WaveletUpdateImpl(message);
  }

  @Override
  public boolean hasCommitVersion() {
    return commitVersion != null;
  }

  @Override
  public void clearCommitVersion() {
    commitVersion = null;
  }

  @Override
  public ProtocolHashedVersionImpl getCommitVersion() {
    return new ProtocolHashedVersionImpl(commitVersion);
  }

  @Override
  public void setCommitVersion(ProtocolHashedVersion message) {
    this.commitVersion = new ProtocolHashedVersionImpl(message);
  }

  @Override
  public boolean hasTerminator() {
    return terminator != null;
  }

  @Override
  public void clearTerminator() {
    terminator = null;
  }

  @Override
  public WaveletChannelTerminatorImpl getTerminator() {
    return new WaveletChannelTerminatorImpl(terminator);
  }

  @Override
  public void setTerminator(WaveletChannelTerminator message) {
    this.terminator = new WaveletChannelTerminatorImpl(message);
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.channelId = null;
    this.delta = null;
    this.commitVersion = null;
    this.terminator = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof OpenWaveletChannelStreamImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof OpenWaveletChannelStream) {
      return OpenWaveletChannelStreamUtil.isEqual(this, (OpenWaveletChannelStream) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return OpenWaveletChannelStreamUtil.getHashCode(this);
  }

}