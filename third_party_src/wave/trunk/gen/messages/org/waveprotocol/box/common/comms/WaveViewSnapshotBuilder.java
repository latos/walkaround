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

import org.waveprotocol.box.common.comms.WaveletSnapshot;
import org.waveprotocol.box.common.comms.WaveletSnapshotBuilder;
import org.waveprotocol.box.common.comms.WaveViewSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for WaveViewSnapshots.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public final class WaveViewSnapshotBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    WaveViewSnapshot create();
  }

  private String waveId;
  private final List<WaveletSnapshot> wavelet = new ArrayList<WaveletSnapshot>();
  public WaveViewSnapshotBuilder() {
  }

  public WaveViewSnapshotBuilder setWaveId(String value) {
    this.waveId = value;
    return this;
  }

  public WaveViewSnapshotBuilder addAllWavelet(List<? extends WaveletSnapshot> messages) {
    for (WaveletSnapshot message : messages) {
      addWavelet(message);
    }
    return this;
  }

  public WaveViewSnapshotBuilder setWavelet(int n, WaveletSnapshot message) {
    this.wavelet.set(n, message);
    return this;
  }

  public WaveViewSnapshotBuilder addWavelet(WaveletSnapshot message) {
    this.wavelet.add(message);
    return this;
  }

  public WaveViewSnapshotBuilder clearWavelet() {
    wavelet.clear();
    return this;
  }

  /** Builds a {@link WaveViewSnapshot} using this builder and a factory. */
  public WaveViewSnapshot build(Factory factory) {
    WaveViewSnapshot message = factory.create();
    message.setWaveId(waveId);
    message.clearWavelet();
    message.addAllWavelet(wavelet);
    return message;
  }

}