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

import org.waveprotocol.wave.federation.ProtocolSignedDelta;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.impl.ProtocolSignedDeltaImpl;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.wave.federation.ProtocolAppliedWaveletDelta;
import org.waveprotocol.wave.federation.ProtocolAppliedWaveletDeltaUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolAppliedWaveletDelta.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public class ProtocolAppliedWaveletDeltaImpl implements ProtocolAppliedWaveletDelta {
  private ProtocolSignedDeltaImpl signedOriginalDelta;
  private ProtocolHashedVersionImpl hashedVersionAppliedAt;
  private Integer operationsApplied;
  private Double applicationTimestamp;
  public ProtocolAppliedWaveletDeltaImpl() {
  }

  public ProtocolAppliedWaveletDeltaImpl(ProtocolAppliedWaveletDelta message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolAppliedWaveletDelta message) {
    setSignedOriginalDelta(message.getSignedOriginalDelta());
    if (message.hasHashedVersionAppliedAt()) {
      setHashedVersionAppliedAt(message.getHashedVersionAppliedAt());
    } else {
      clearHashedVersionAppliedAt();
    }
    setOperationsApplied(message.getOperationsApplied());
    setApplicationTimestamp(message.getApplicationTimestamp());
  }

  @Override
  public ProtocolSignedDeltaImpl getSignedOriginalDelta() {
    return new ProtocolSignedDeltaImpl(signedOriginalDelta);
  }

  @Override
  public void setSignedOriginalDelta(ProtocolSignedDelta message) {
    this.signedOriginalDelta = new ProtocolSignedDeltaImpl(message);
  }

  @Override
  public boolean hasHashedVersionAppliedAt() {
    return hashedVersionAppliedAt != null;
  }

  @Override
  public void clearHashedVersionAppliedAt() {
    hashedVersionAppliedAt = null;
  }

  @Override
  public ProtocolHashedVersionImpl getHashedVersionAppliedAt() {
    return new ProtocolHashedVersionImpl(hashedVersionAppliedAt);
  }

  @Override
  public void setHashedVersionAppliedAt(ProtocolHashedVersion message) {
    this.hashedVersionAppliedAt = new ProtocolHashedVersionImpl(message);
  }

  @Override
  public int getOperationsApplied() {
    return operationsApplied;
  }

  @Override
  public void setOperationsApplied(int value) {
    this.operationsApplied = value;
  }

  @Override
  public double getApplicationTimestamp() {
    return applicationTimestamp;
  }

  @Override
  public void setApplicationTimestamp(double value) {
    this.applicationTimestamp = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.signedOriginalDelta = null;
    this.hashedVersionAppliedAt = null;
    this.operationsApplied = null;
    this.applicationTimestamp = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolAppliedWaveletDeltaImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolAppliedWaveletDelta) {
      return ProtocolAppliedWaveletDeltaUtil.isEqual(this, (ProtocolAppliedWaveletDelta) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolAppliedWaveletDeltaUtil.getHashCode(this);
  }

}