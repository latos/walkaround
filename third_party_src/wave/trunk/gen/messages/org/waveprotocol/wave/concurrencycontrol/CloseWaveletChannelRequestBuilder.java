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

import org.waveprotocol.wave.concurrencycontrol.CloseWaveletChannelRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for CloseWaveletChannelRequests.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public final class CloseWaveletChannelRequestBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    CloseWaveletChannelRequest create();
  }

  private String channelId;
  public CloseWaveletChannelRequestBuilder() {
  }

  public CloseWaveletChannelRequestBuilder setChannelId(String value) {
    this.channelId = value;
    return this;
  }

  /** Builds a {@link CloseWaveletChannelRequest} using this builder and a factory. */
  public CloseWaveletChannelRequest build(Factory factory) {
    CloseWaveletChannelRequest message = factory.create();
    message.setChannelId(channelId);
    return message;
  }

}