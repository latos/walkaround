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

import org.waveprotocol.wave.federation.ProtocolSignature.SignatureAlgorithm;
import org.waveprotocol.wave.federation.ProtocolSignature;
import org.waveprotocol.wave.federation.ProtocolSignatureUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolSignature.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public class ProtocolSignatureImpl implements ProtocolSignature {
  private Blob signatureBytes;
  private Blob signerId;
  private SignatureAlgorithm signatureAlgorithm;
  public ProtocolSignatureImpl() {
  }

  public ProtocolSignatureImpl(ProtocolSignature message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolSignature message) {
    setSignatureBytes(message.getSignatureBytes());
    setSignerId(message.getSignerId());
    setSignatureAlgorithm(message.getSignatureAlgorithm());
  }

  @Override
  public Blob getSignatureBytes() {
    return signatureBytes;
  }

  @Override
  public void setSignatureBytes(Blob value) {
    this.signatureBytes = value;
  }

  @Override
  public Blob getSignerId() {
    return signerId;
  }

  @Override
  public void setSignerId(Blob value) {
    this.signerId = value;
  }

  @Override
  public SignatureAlgorithm getSignatureAlgorithm() {
    return signatureAlgorithm;
  }

  @Override
  public void setSignatureAlgorithm(SignatureAlgorithm value) {
    this.signatureAlgorithm = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.signatureBytes = null;
    this.signerId = null;
    this.signatureAlgorithm = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolSignatureImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolSignature) {
      return ProtocolSignatureUtil.isEqual(this, (ProtocolSignature) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolSignatureUtil.getHashCode(this);
  }

}