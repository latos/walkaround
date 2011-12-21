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

package org.waveprotocol.wave.concurrencycontrol.impl;

import org.waveprotocol.wave.concurrencycontrol.ResponseStatus;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.concurrencycontrol.impl.ResponseStatusImpl;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.wave.concurrencycontrol.SubmitDeltaResponse;
import org.waveprotocol.wave.concurrencycontrol.SubmitDeltaResponseUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of SubmitDeltaResponse.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class SubmitDeltaResponseImpl implements SubmitDeltaResponse {
  private ResponseStatusImpl status;
  private Integer operationsApplied;
  private ProtocolHashedVersionImpl hashedVersionAfterApplication;
  private Long timestampAfterApplication;
  public SubmitDeltaResponseImpl() {
  }

  public SubmitDeltaResponseImpl(SubmitDeltaResponse message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(SubmitDeltaResponse message) {
    setStatus(message.getStatus());
    setOperationsApplied(message.getOperationsApplied());
    if (message.hasHashedVersionAfterApplication()) {
      setHashedVersionAfterApplication(message.getHashedVersionAfterApplication());
    } else {
      clearHashedVersionAfterApplication();
    }
    if (message.hasTimestampAfterApplication()) {
      setTimestampAfterApplication(message.getTimestampAfterApplication());
    } else {
      clearTimestampAfterApplication();
    }
  }

  @Override
  public ResponseStatusImpl getStatus() {
    return new ResponseStatusImpl(status);
  }

  @Override
  public void setStatus(ResponseStatus message) {
    this.status = new ResponseStatusImpl(message);
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
  public boolean hasHashedVersionAfterApplication() {
    return hashedVersionAfterApplication != null;
  }

  @Override
  public void clearHashedVersionAfterApplication() {
    hashedVersionAfterApplication = null;
  }

  @Override
  public ProtocolHashedVersionImpl getHashedVersionAfterApplication() {
    return new ProtocolHashedVersionImpl(hashedVersionAfterApplication);
  }

  @Override
  public void setHashedVersionAfterApplication(ProtocolHashedVersion message) {
    this.hashedVersionAfterApplication = new ProtocolHashedVersionImpl(message);
  }

  @Override
  public boolean hasTimestampAfterApplication() {
    return timestampAfterApplication != null;
  }

  @Override
  public void clearTimestampAfterApplication() {
    timestampAfterApplication = null;
  }

  @Override
  public long getTimestampAfterApplication() {
    return timestampAfterApplication;
  }

  @Override
  public void setTimestampAfterApplication(long value) {
    this.timestampAfterApplication = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.status = null;
    this.operationsApplied = null;
    this.hashedVersionAfterApplication = null;
    this.timestampAfterApplication = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof SubmitDeltaResponseImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof SubmitDeltaResponse) {
      return SubmitDeltaResponseUtil.isEqual(this, (SubmitDeltaResponse) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return SubmitDeltaResponseUtil.getHashCode(this);
  }

}