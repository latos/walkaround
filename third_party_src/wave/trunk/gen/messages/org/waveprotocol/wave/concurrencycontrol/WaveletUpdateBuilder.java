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
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.ProtocolWaveletDeltaBuilder;
import org.waveprotocol.wave.federation.ProtocolHashedVersionBuilder;
import org.waveprotocol.wave.concurrencycontrol.WaveletUpdateUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for WaveletUpdates.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public final class WaveletUpdateBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    WaveletUpdate create();
  }

  private ProtocolWaveletDelta delta;
  private ProtocolHashedVersion resultingVersion;
  private Long applicationTimpstamp;
  public WaveletUpdateBuilder() {
  }

  public WaveletUpdateBuilder setDelta(ProtocolWaveletDelta message) {
    this.delta = message;
    return this;
  }

  public WaveletUpdateBuilder setResultingVersion(ProtocolHashedVersion message) {
    this.resultingVersion = message;
    return this;
  }

  public WaveletUpdateBuilder setApplicationTimpstamp(long value) {
    this.applicationTimpstamp = value;
    return this;
  }

  /** Builds a {@link WaveletUpdate} using this builder and a factory. */
  public WaveletUpdate build(Factory factory) {
    WaveletUpdate message = factory.create();
    message.setDelta(delta);
    message.setResultingVersion(resultingVersion);
    message.setApplicationTimpstamp(applicationTimpstamp);
    return message;
  }

}