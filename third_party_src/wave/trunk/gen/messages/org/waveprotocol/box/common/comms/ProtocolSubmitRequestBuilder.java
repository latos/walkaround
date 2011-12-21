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

import org.waveprotocol.wave.federation.ProtocolWaveletDelta;
import org.waveprotocol.wave.federation.ProtocolWaveletDeltaBuilder;
import org.waveprotocol.box.common.comms.ProtocolSubmitRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolSubmitRequests.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public final class ProtocolSubmitRequestBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolSubmitRequest create();
  }

  private String waveletName;
  private ProtocolWaveletDelta delta;
  private String channelId;
  public ProtocolSubmitRequestBuilder() {
  }

  public ProtocolSubmitRequestBuilder setWaveletName(String value) {
    this.waveletName = value;
    return this;
  }

  public ProtocolSubmitRequestBuilder setDelta(ProtocolWaveletDelta message) {
    this.delta = message;
    return this;
  }

  public ProtocolSubmitRequestBuilder clearChannelId() {
    channelId = null;
    return this;
  }

  public ProtocolSubmitRequestBuilder setChannelId(String value) {
    this.channelId = value;
    return this;
  }

  /** Builds a {@link ProtocolSubmitRequest} using this builder and a factory. */
  public ProtocolSubmitRequest build(Factory factory) {
    ProtocolSubmitRequest message = factory.create();
    message.setWaveletName(waveletName);
    message.setDelta(delta);
    message.setChannelId(channelId);
    return message;
  }

}