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

import org.waveprotocol.wave.federation.ProtocolSignature;
import org.waveprotocol.wave.federation.ProtocolSignatureBuilder;
import org.waveprotocol.wave.federation.ProtocolSignedDeltaUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolSignedDeltas.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolSignedDeltaBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolSignedDelta create();
  }

  private Blob delta;
  private final List<ProtocolSignature> signature = new ArrayList<ProtocolSignature>();
  public ProtocolSignedDeltaBuilder() {
  }

  public ProtocolSignedDeltaBuilder setDelta(Blob value) {
    this.delta = value;
    return this;
  }

  public ProtocolSignedDeltaBuilder addAllSignature(List<? extends ProtocolSignature> messages) {
    for (ProtocolSignature message : messages) {
      addSignature(message);
    }
    return this;
  }

  public ProtocolSignedDeltaBuilder setSignature(int n, ProtocolSignature message) {
    this.signature.set(n, message);
    return this;
  }

  public ProtocolSignedDeltaBuilder addSignature(ProtocolSignature message) {
    this.signature.add(message);
    return this;
  }

  public ProtocolSignedDeltaBuilder clearSignature() {
    signature.clear();
    return this;
  }

  /** Builds a {@link ProtocolSignedDelta} using this builder and a factory. */
  public ProtocolSignedDelta build(Factory factory) {
    ProtocolSignedDelta message = factory.create();
    message.setDelta(delta);
    message.clearSignature();
    message.addAllSignature(signature);
    return message;
  }

}