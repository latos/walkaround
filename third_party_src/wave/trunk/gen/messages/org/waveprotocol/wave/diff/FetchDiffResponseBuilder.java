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

package org.waveprotocol.wave.diff;

import org.waveprotocol.wave.concurrencycontrol.ResponseStatus;
import org.waveprotocol.wave.diff.FetchDiffResponse.WaveletDiff;
import org.waveprotocol.wave.diff.WaveletDiffSnapshot;
import org.waveprotocol.wave.concurrencycontrol.ResponseStatusBuilder;
import org.waveprotocol.wave.diff.FetchDiffResponseBuilder.WaveletDiffBuilder;
import org.waveprotocol.wave.diff.WaveletDiffSnapshotBuilder;
import org.waveprotocol.wave.diff.FetchDiffResponseUtil;
import org.waveprotocol.wave.diff.FetchDiffResponseUtil.WaveletDiffUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for FetchDiffResponses.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
public final class FetchDiffResponseBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    FetchDiffResponse create();
  }

  public static final class WaveletDiffBuilder {

    /** Factory to pass to {@link #build()}. */
    public interface Factory {
      WaveletDiff create();
    }

    private String waveletId;
    private WaveletDiffSnapshot snapshot;
    public WaveletDiffBuilder() {
    }

    public WaveletDiffBuilder setWaveletId(String value) {
      this.waveletId = value;
      return this;
    }

    public WaveletDiffBuilder clearSnapshot() {
      snapshot = null;
      return this;
    }

    public WaveletDiffBuilder setSnapshot(WaveletDiffSnapshot message) {
      this.snapshot = message;
      return this;
    }

    /** Builds a {@link WaveletDiff} using this builder and a factory. */
    public WaveletDiff build(Factory factory) {
      WaveletDiff message = factory.create();
      message.setWaveletId(waveletId);
      message.setSnapshot(snapshot);
      return message;
    }

  }

  private ResponseStatus status;
  private final List<WaveletDiff> wavelet = new ArrayList<WaveletDiff>();
  public FetchDiffResponseBuilder() {
  }

  public FetchDiffResponseBuilder setStatus(ResponseStatus message) {
    this.status = message;
    return this;
  }

  public FetchDiffResponseBuilder addAllWavelet(List<? extends WaveletDiff> messages) {
    for (WaveletDiff message : messages) {
      addWavelet(message);
    }
    return this;
  }

  public FetchDiffResponseBuilder setWavelet(int n, WaveletDiff message) {
    this.wavelet.set(n, message);
    return this;
  }

  public FetchDiffResponseBuilder addWavelet(WaveletDiff message) {
    this.wavelet.add(message);
    return this;
  }

  public FetchDiffResponseBuilder clearWavelet() {
    wavelet.clear();
    return this;
  }

  /** Builds a {@link FetchDiffResponse} using this builder and a factory. */
  public FetchDiffResponse build(Factory factory) {
    FetchDiffResponse message = factory.create();
    message.setStatus(status);
    message.clearWavelet();
    message.addAllWavelet(wavelet);
    return message;
  }

}