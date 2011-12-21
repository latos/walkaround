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

import org.waveprotocol.wave.concurrencycontrol.WaveletVersion;
import org.waveprotocol.wave.concurrencycontrol.WaveletVersionBuilder;
import org.waveprotocol.wave.diff.FetchDiffRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for FetchDiffRequests.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
public final class FetchDiffRequestBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    FetchDiffRequest create();
  }

  private String waveId;
  private final List<WaveletVersion> knownWavelet = new ArrayList<WaveletVersion>();
  public FetchDiffRequestBuilder() {
  }

  public FetchDiffRequestBuilder setWaveId(String value) {
    this.waveId = value;
    return this;
  }

  public FetchDiffRequestBuilder addAllKnownWavelet(List<? extends WaveletVersion> messages) {
    for (WaveletVersion message : messages) {
      addKnownWavelet(message);
    }
    return this;
  }

  public FetchDiffRequestBuilder setKnownWavelet(int n, WaveletVersion message) {
    this.knownWavelet.set(n, message);
    return this;
  }

  public FetchDiffRequestBuilder addKnownWavelet(WaveletVersion message) {
    this.knownWavelet.add(message);
    return this;
  }

  public FetchDiffRequestBuilder clearKnownWavelet() {
    knownWavelet.clear();
    return this;
  }

  /** Builds a {@link FetchDiffRequest} using this builder and a factory. */
  public FetchDiffRequest build(Factory factory) {
    FetchDiffRequest message = factory.create();
    message.setWaveId(waveId);
    message.clearKnownWavelet();
    message.addAllKnownWavelet(knownWavelet);
    return message;
  }

}