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

import org.waveprotocol.wave.federation.ProtocolSignature.SignatureAlgorithm;
import org.waveprotocol.wave.federation.ProtocolSignatureUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolSignatures.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolSignatureBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolSignature create();
  }

  private Blob signatureBytes;
  private Blob signerId;
  private SignatureAlgorithm signatureAlgorithm;
  public ProtocolSignatureBuilder() {
  }

  public ProtocolSignatureBuilder setSignatureBytes(Blob value) {
    this.signatureBytes = value;
    return this;
  }

  public ProtocolSignatureBuilder setSignerId(Blob value) {
    this.signerId = value;
    return this;
  }

  public ProtocolSignatureBuilder setSignatureAlgorithm(SignatureAlgorithm value) {
    this.signatureAlgorithm = value;
    return this;
  }

  /** Builds a {@link ProtocolSignature} using this builder and a factory. */
  public ProtocolSignature build(Factory factory) {
    ProtocolSignature message = factory.create();
    message.setSignatureBytes(signatureBytes);
    message.setSignerId(signerId);
    message.setSignatureAlgorithm(signatureAlgorithm);
    return message;
  }

}