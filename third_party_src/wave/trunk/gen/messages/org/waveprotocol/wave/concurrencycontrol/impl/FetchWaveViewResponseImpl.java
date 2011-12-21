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

import org.waveprotocol.wave.concurrencycontrol.ResponseStatus;
import org.waveprotocol.wave.concurrencycontrol.FetchWaveViewResponse.Wavelet;
import org.waveprotocol.wave.concurrencycontrol.WaveletSnapshot;
import org.waveprotocol.wave.concurrencycontrol.impl.ResponseStatusImpl;
import org.waveprotocol.wave.concurrencycontrol.impl.FetchWaveViewResponseImpl.WaveletImpl;
import org.waveprotocol.wave.concurrencycontrol.impl.WaveletSnapshotImpl;
import org.waveprotocol.wave.concurrencycontrol.FetchWaveViewResponse;
import org.waveprotocol.wave.concurrencycontrol.FetchWaveViewResponseUtil;
import org.waveprotocol.wave.concurrencycontrol.FetchWaveViewResponse.Wavelet;
import org.waveprotocol.wave.concurrencycontrol.FetchWaveViewResponseUtil.WaveletUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of FetchWaveViewResponse.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class FetchWaveViewResponseImpl implements FetchWaveViewResponse {

  public static class WaveletImpl implements Wavelet {
    private String waveletId;
    private WaveletSnapshotImpl snapshot;
    public WaveletImpl() {
    }

    public WaveletImpl(Wavelet message) {
      copyFrom(message);
    }

    @Override
    public void copyFrom(Wavelet message) {
      setWaveletId(message.getWaveletId());
      if (message.hasSnapshot()) {
        setSnapshot(message.getSnapshot());
      } else {
        clearSnapshot();
      }
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
    public boolean hasSnapshot() {
      return snapshot != null;
    }

    @Override
    public void clearSnapshot() {
      snapshot = null;
    }

    @Override
    public WaveletSnapshotImpl getSnapshot() {
      return new WaveletSnapshotImpl(snapshot);
    }

    @Override
    public void setSnapshot(WaveletSnapshot message) {
      this.snapshot = new WaveletSnapshotImpl(message);
    }

    /** Provided to subclasses to clear all fields, for example when deserializing. */
    protected void reset() {
      this.waveletId = null;
      this.snapshot = null;
    }

    @Override
    public boolean equals(Object o) {
      return (o instanceof WaveletImpl) && isEqualTo(o);
    }

    @Override
    public boolean isEqualTo(Object o) {
      if (o == this) {
        return true;
      } else if (o instanceof Wavelet) {
        return WaveletUtil.isEqual(this, (Wavelet) o);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return WaveletUtil.getHashCode(this);
    }

  }

  private ResponseStatusImpl status;
  private final List<WaveletImpl> wavelet = new ArrayList<WaveletImpl>();
  public FetchWaveViewResponseImpl() {
  }

  public FetchWaveViewResponseImpl(FetchWaveViewResponse message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(FetchWaveViewResponse message) {
    setStatus(message.getStatus());
    clearWavelet();
    for (Wavelet field : message.getWavelet()) {
      addWavelet(new WaveletImpl(field));
    }
  }

  @Override
  public ResponseStatusImpl getStatus() {
    return new ResponseStatusImpl(status);
  }

  @Override
  public void setStatus(ResponseStatus message) {
    this.status = new ResponseStatusImpl(message);
  }

  @Override
  public List<WaveletImpl> getWavelet() {
    return Collections.unmodifiableList(wavelet);
  }

  @Override
  public void addAllWavelet(List<? extends Wavelet> messages) {
    for (Wavelet message : messages) {
      addWavelet(message);
    }
  }

  @Override
  public WaveletImpl getWavelet(int n) {
    return new WaveletImpl(wavelet.get(n));
  }

  @Override
  public void setWavelet(int n, Wavelet message) {
    this.wavelet.set(n, new WaveletImpl(message));
  }

  @Override
  public int getWaveletSize() {
    return wavelet.size();
  }

  @Override
  public void addWavelet(Wavelet message) {
    this.wavelet.add(new WaveletImpl(message));
  }

  @Override
  public void clearWavelet() {
    wavelet.clear();
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.status = null;
    this.wavelet.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof FetchWaveViewResponseImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof FetchWaveViewResponse) {
      return FetchWaveViewResponseUtil.isEqual(this, (FetchWaveViewResponse) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return FetchWaveViewResponseUtil.getHashCode(this);
  }

}