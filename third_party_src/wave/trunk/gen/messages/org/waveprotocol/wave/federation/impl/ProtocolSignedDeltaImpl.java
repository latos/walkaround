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

import org.waveprotocol.wave.federation.ProtocolSignature;
import org.waveprotocol.wave.federation.impl.ProtocolSignatureImpl;
import org.waveprotocol.wave.federation.ProtocolSignedDelta;
import org.waveprotocol.wave.federation.ProtocolSignedDeltaUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolSignedDelta.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public class ProtocolSignedDeltaImpl implements ProtocolSignedDelta {
  private Blob delta;
  private final List<ProtocolSignatureImpl> signature = new ArrayList<ProtocolSignatureImpl>();
  public ProtocolSignedDeltaImpl() {
  }

  public ProtocolSignedDeltaImpl(ProtocolSignedDelta message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolSignedDelta message) {
    setDelta(message.getDelta());
    clearSignature();
    for (ProtocolSignature field : message.getSignature()) {
      addSignature(new ProtocolSignatureImpl(field));
    }
  }

  @Override
  public Blob getDelta() {
    return delta;
  }

  @Override
  public void setDelta(Blob value) {
    this.delta = value;
  }

  @Override
  public List<ProtocolSignatureImpl> getSignature() {
    return Collections.unmodifiableList(signature);
  }

  @Override
  public void addAllSignature(List<? extends ProtocolSignature> messages) {
    for (ProtocolSignature message : messages) {
      addSignature(message);
    }
  }

  @Override
  public ProtocolSignatureImpl getSignature(int n) {
    return new ProtocolSignatureImpl(signature.get(n));
  }

  @Override
  public void setSignature(int n, ProtocolSignature message) {
    this.signature.set(n, new ProtocolSignatureImpl(message));
  }

  @Override
  public int getSignatureSize() {
    return signature.size();
  }

  @Override
  public void addSignature(ProtocolSignature message) {
    this.signature.add(new ProtocolSignatureImpl(message));
  }

  @Override
  public void clearSignature() {
    signature.clear();
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.delta = null;
    this.signature.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolSignedDeltaImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolSignedDelta) {
      return ProtocolSignedDeltaUtil.isEqual(this, (ProtocolSignedDelta) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolSignedDeltaUtil.getHashCode(this);
  }

}