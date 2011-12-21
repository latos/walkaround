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

import org.waveprotocol.wave.federation.ProtocolSignerInfo.HashAlgorithm;
import org.waveprotocol.wave.federation.ProtocolSignerInfoUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolSignerInfos.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolSignerInfoBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolSignerInfo create();
  }

  private HashAlgorithm hashAlgorithm;
  private String domain;
  private final List<Blob> certificate = new ArrayList<Blob>();
  public ProtocolSignerInfoBuilder() {
  }

  public ProtocolSignerInfoBuilder setHashAlgorithm(HashAlgorithm value) {
    this.hashAlgorithm = value;
    return this;
  }

  public ProtocolSignerInfoBuilder setDomain(String value) {
    this.domain = value;
    return this;
  }

  public ProtocolSignerInfoBuilder addAllCertificate(List<Blob> values) {
    this.certificate.addAll(values);
    return this;
  }

  public ProtocolSignerInfoBuilder setCertificate(int n, Blob value) {
    this.certificate.set(n, value);
    return this;
  }

  public ProtocolSignerInfoBuilder addCertificate(Blob value) {
    this.certificate.add(value);
    return this;
  }

  public ProtocolSignerInfoBuilder clearCertificate() {
    certificate.clear();
    return this;
  }

  /** Builds a {@link ProtocolSignerInfo} using this builder and a factory. */
  public ProtocolSignerInfo build(Factory factory) {
    ProtocolSignerInfo message = factory.create();
    message.setHashAlgorithm(hashAlgorithm);
    message.setDomain(domain);
    message.clearCertificate();
    message.addAllCertificate(certificate);
    return message;
  }

}