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

package org.waveprotocol.wave.diff.impl;

import org.waveprotocol.wave.concurrencycontrol.WaveletVersion;
import org.waveprotocol.wave.concurrencycontrol.impl.WaveletVersionImpl;
import org.waveprotocol.wave.diff.FetchDiffRequest;
import org.waveprotocol.wave.diff.FetchDiffRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of FetchDiffRequest.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
public class FetchDiffRequestImpl implements FetchDiffRequest {
  private String waveId;
  private final List<WaveletVersionImpl> knownWavelet = new ArrayList<WaveletVersionImpl>();
  public FetchDiffRequestImpl() {
  }

  public FetchDiffRequestImpl(FetchDiffRequest message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(FetchDiffRequest message) {
    setWaveId(message.getWaveId());
    clearKnownWavelet();
    for (WaveletVersion field : message.getKnownWavelet()) {
      addKnownWavelet(new WaveletVersionImpl(field));
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
    this.waveId = null;
    this.knownWavelet.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof FetchDiffRequestImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof FetchDiffRequest) {
      return FetchDiffRequestUtil.isEqual(this, (FetchDiffRequest) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return FetchDiffRequestUtil.getHashCode(this);
  }

}