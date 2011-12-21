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

package org.waveprotocol.wave.concurrencycontrol;

import org.waveprotocol.wave.federation.ProtocolWaveletDelta;
import org.waveprotocol.wave.federation.ProtocolWaveletDeltaBuilder;
import org.waveprotocol.wave.concurrencycontrol.SubmitDeltaRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for SubmitDeltaRequests.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public final class SubmitDeltaRequestBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    SubmitDeltaRequest create();
  }

  private String waveId;
  private String waveletId;
  private ProtocolWaveletDelta delta;
  private String channelId;
  public SubmitDeltaRequestBuilder() {
  }

  public SubmitDeltaRequestBuilder setWaveId(String value) {
    this.waveId = value;
    return this;
  }

  public SubmitDeltaRequestBuilder setWaveletId(String value) {
    this.waveletId = value;
    return this;
  }

  public SubmitDeltaRequestBuilder setDelta(ProtocolWaveletDelta message) {
    this.delta = message;
    return this;
  }

  public SubmitDeltaRequestBuilder setChannelId(String value) {
    this.channelId = value;
    return this;
  }

  /** Builds a {@link SubmitDeltaRequest} using this builder and a factory. */
  public SubmitDeltaRequest build(Factory factory) {
    SubmitDeltaRequest message = factory.create();
    message.setWaveId(waveId);
    message.setWaveletId(waveletId);
    message.setDelta(delta);
    message.setChannelId(channelId);
    return message;
  }

}