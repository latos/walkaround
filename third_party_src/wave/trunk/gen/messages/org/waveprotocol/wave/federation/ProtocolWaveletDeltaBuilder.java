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

import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.ProtocolWaveletOperation;
import org.waveprotocol.wave.federation.ProtocolHashedVersionBuilder;
import org.waveprotocol.wave.federation.ProtocolWaveletOperationBuilder;
import org.waveprotocol.wave.federation.ProtocolWaveletDeltaUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolWaveletDeltas.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolWaveletDeltaBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolWaveletDelta create();
  }

  private ProtocolHashedVersion hashedVersion;
  private String author;
  private final List<ProtocolWaveletOperation> operation = new ArrayList<ProtocolWaveletOperation>();
  private final List<String> addressPath = new ArrayList<String>();
  public ProtocolWaveletDeltaBuilder() {
  }

  public ProtocolWaveletDeltaBuilder setHashedVersion(ProtocolHashedVersion message) {
    this.hashedVersion = message;
    return this;
  }

  public ProtocolWaveletDeltaBuilder setAuthor(String value) {
    this.author = value;
    return this;
  }

  public ProtocolWaveletDeltaBuilder addAllOperation(List<? extends ProtocolWaveletOperation> messages) {
    for (ProtocolWaveletOperation message : messages) {
      addOperation(message);
    }
    return this;
  }

  public ProtocolWaveletDeltaBuilder setOperation(int n, ProtocolWaveletOperation message) {
    this.operation.set(n, message);
    return this;
  }

  public ProtocolWaveletDeltaBuilder addOperation(ProtocolWaveletOperation message) {
    this.operation.add(message);
    return this;
  }

  public ProtocolWaveletDeltaBuilder clearOperation() {
    operation.clear();
    return this;
  }

  public ProtocolWaveletDeltaBuilder addAllAddressPath(List<String> values) {
    this.addressPath.addAll(values);
    return this;
  }

  public ProtocolWaveletDeltaBuilder setAddressPath(int n, String value) {
    this.addressPath.set(n, value);
    return this;
  }

  public ProtocolWaveletDeltaBuilder addAddressPath(String value) {
    this.addressPath.add(value);
    return this;
  }

  public ProtocolWaveletDeltaBuilder clearAddressPath() {
    addressPath.clear();
    return this;
  }

  /** Builds a {@link ProtocolWaveletDelta} using this builder and a factory. */
  public ProtocolWaveletDelta build(Factory factory) {
    ProtocolWaveletDelta message = factory.create();
    message.setHashedVersion(hashedVersion);
    message.setAuthor(author);
    message.clearOperation();
    message.addAllOperation(operation);
    message.clearAddressPath();
    message.addAllAddressPath(addressPath);
    return message;
  }

}