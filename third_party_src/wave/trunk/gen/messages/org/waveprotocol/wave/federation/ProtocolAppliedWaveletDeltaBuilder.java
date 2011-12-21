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

package org.waveprotocol.wave.federation;

import org.waveprotocol.wave.federation.ProtocolSignedDelta;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.ProtocolSignedDeltaBuilder;
import org.waveprotocol.wave.federation.ProtocolHashedVersionBuilder;
import org.waveprotocol.wave.federation.ProtocolAppliedWaveletDeltaUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolAppliedWaveletDeltas.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolAppliedWaveletDeltaBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolAppliedWaveletDelta create();
  }

  private ProtocolSignedDelta signedOriginalDelta;
  private ProtocolHashedVersion hashedVersionAppliedAt;
  private Integer operationsApplied;
  private Double applicationTimestamp;
  public ProtocolAppliedWaveletDeltaBuilder() {
  }

  public ProtocolAppliedWaveletDeltaBuilder setSignedOriginalDelta(ProtocolSignedDelta message) {
    this.signedOriginalDelta = message;
    return this;
  }

  public ProtocolAppliedWaveletDeltaBuilder clearHashedVersionAppliedAt() {
    hashedVersionAppliedAt = null;
    return this;
  }

  public ProtocolAppliedWaveletDeltaBuilder setHashedVersionAppliedAt(ProtocolHashedVersion message) {
    this.hashedVersionAppliedAt = message;
    return this;
  }

  public ProtocolAppliedWaveletDeltaBuilder setOperationsApplied(int value) {
    this.operationsApplied = value;
    return this;
  }

  public ProtocolAppliedWaveletDeltaBuilder setApplicationTimestamp(double value) {
    this.applicationTimestamp = value;
    return this;
  }

  /** Builds a {@link ProtocolAppliedWaveletDelta} using this builder and a factory. */
  public ProtocolAppliedWaveletDelta build(Factory factory) {
    ProtocolAppliedWaveletDelta message = factory.create();
    message.setSignedOriginalDelta(signedOriginalDelta);
    message.setHashedVersionAppliedAt(hashedVersionAppliedAt);
    message.setOperationsApplied(operationsApplied);
    message.setApplicationTimestamp(applicationTimestamp);
    return message;
  }

}