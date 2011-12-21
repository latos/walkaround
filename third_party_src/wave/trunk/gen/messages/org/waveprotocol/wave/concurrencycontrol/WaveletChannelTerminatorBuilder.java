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

import org.waveprotocol.wave.concurrencycontrol.ResponseStatus;
import org.waveprotocol.wave.concurrencycontrol.ResponseStatusBuilder;
import org.waveprotocol.wave.concurrencycontrol.WaveletChannelTerminatorUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for WaveletChannelTerminators.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public final class WaveletChannelTerminatorBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    WaveletChannelTerminator create();
  }

  private ResponseStatus status;
  public WaveletChannelTerminatorBuilder() {
  }

  public WaveletChannelTerminatorBuilder setStatus(ResponseStatus message) {
    this.status = message;
    return this;
  }

  /** Builds a {@link WaveletChannelTerminator} using this builder and a factory. */
  public WaveletChannelTerminator build(Factory factory) {
    WaveletChannelTerminator message = factory.create();
    message.setStatus(status);
    return message;
  }

}