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

import org.waveprotocol.box.common.comms.WaveletSnapshot;
import org.waveprotocol.box.common.comms.impl.WaveletSnapshotImpl;
import org.waveprotocol.box.common.comms.WaveViewSnapshot;
import org.waveprotocol.box.common.comms.WaveViewSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of WaveViewSnapshot.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public class WaveViewSnapshotImpl implements WaveViewSnapshot {
  private String waveId;
  private final List<WaveletSnapshotImpl> wavelet = new ArrayList<WaveletSnapshotImpl>();
  public WaveViewSnapshotImpl() {
  }

  public WaveViewSnapshotImpl(WaveViewSnapshot message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(WaveViewSnapshot message) {
    setWaveId(message.getWaveId());
    clearWavelet();
    for (WaveletSnapshot field : message.getWavelet()) {
      addWavelet(new WaveletSnapshotImpl(field));
    }
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
  public List<WaveletSnapshotImpl> getWavelet() {
    return Collections.unmodifiableList(wavelet);
  }

  @Override
  public void addAllWavelet(List<? extends WaveletSnapshot> messages) {
    for (WaveletSnapshot message : messages) {
      addWavelet(message);
    }
  }

  @Override
  public WaveletSnapshotImpl getWavelet(int n) {
    return new WaveletSnapshotImpl(wavelet.get(n));
  }

  @Override
  public void setWavelet(int n, WaveletSnapshot message) {
    this.wavelet.set(n, new WaveletSnapshotImpl(message));
  }

  @Override
  public int getWaveletSize() {
    return wavelet.size();
  }

  @Override
  public void addWavelet(WaveletSnapshot message) {
    this.wavelet.add(new WaveletSnapshotImpl(message));
  }

  @Override
  public void clearWavelet() {
    wavelet.clear();
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.waveId = null;
    this.wavelet.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof WaveViewSnapshotImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof WaveViewSnapshot) {
      return WaveViewSnapshotUtil.isEqual(this, (WaveViewSnapshot) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return WaveViewSnapshotUtil.getHashCode(this);
  }

}