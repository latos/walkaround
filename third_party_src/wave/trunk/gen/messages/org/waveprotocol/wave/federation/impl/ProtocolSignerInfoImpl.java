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

package org.waveprotocol.wave.federation.impl;

import org.waveprotocol.wave.federation.ProtocolSignerInfo.HashAlgorithm;
import org.waveprotocol.wave.federation.ProtocolSignerInfo;
import org.waveprotocol.wave.federation.ProtocolSignerInfoUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolSignerInfo.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public class ProtocolSignerInfoImpl implements ProtocolSignerInfo {
  private HashAlgorithm hashAlgorithm;
  private String domain;
  private final List<Blob> certificate = new ArrayList<Blob>();
  public ProtocolSignerInfoImpl() {
  }

  public ProtocolSignerInfoImpl(ProtocolSignerInfo message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolSignerInfo message) {
    setHashAlgorithm(message.getHashAlgorithm());
    setDomain(message.getDomain());
    clearCertificate();
    for (Blob field : message.getCertificate()) {
      addCertificate(field);
    }
  }

  @Override
  public HashAlgorithm getHashAlgorithm() {
    return hashAlgorithm;
  }

  @Override
  public void setHashAlgorithm(HashAlgorithm value) {
    this.hashAlgorithm = value;
  }

  @Override
  public String getDomain() {
    return domain;
  }

  @Override
  public void setDomain(String value) {
    this.domain = value;
  }

  @Override
  public List<Blob> getCertificate() {
    return Collections.unmodifiableList(certificate);
  }

  @Override
  public void addAllCertificate(List<Blob> values) {
    this.certificate.addAll(values);
  }

  @Override
  public Blob getCertificate(int n) {
    return certificate.get(n);
  }

  @Override
  public void setCertificate(int n, Blob value) {
    this.certificate.set(n, value);
  }

  @Override
  public int getCertificateSize() {
    return certificate.size();
  }

  @Override
  public void addCertificate(Blob value) {
    this.certificate.add(value);
  }

  @Override
  public void clearCertificate() {
    certificate.clear();
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.hashAlgorithm = null;
    this.domain = null;
    this.certificate.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolSignerInfoImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolSignerInfo) {
      return ProtocolSignerInfoUtil.isEqual(this, (ProtocolSignerInfo) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolSignerInfoUtil.getHashCode(this);
  }

}