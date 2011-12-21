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

import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.ProtocolHashedVersionUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolHashedVersion.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public class ProtocolHashedVersionImpl implements ProtocolHashedVersion {
  private Double version;
  private Blob historyHash;
  public ProtocolHashedVersionImpl() {
  }

  public ProtocolHashedVersionImpl(ProtocolHashedVersion message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolHashedVersion message) {
    setVersion(message.getVersion());
    setHistoryHash(message.getHistoryHash());
  }

  @Override
  public double getVersion() {
    return version;
  }

  @Override
  public void setVersion(double value) {
    this.version = value;
  }

  @Override
  public Blob getHistoryHash() {
    return historyHash;
  }

  @Override
  public void setHistoryHash(Blob value) {
    this.historyHash = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.version = null;
    this.historyHash = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolHashedVersionImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolHashedVersion) {
      return ProtocolHashedVersionUtil.isEqual(this, (ProtocolHashedVersion) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolHashedVersionUtil.getHashCode(this);
  }

}