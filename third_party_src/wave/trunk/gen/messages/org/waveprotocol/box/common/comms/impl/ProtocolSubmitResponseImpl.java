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

package org.waveprotocol.box.common.comms.impl;

import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.box.common.comms.ProtocolSubmitResponse;
import org.waveprotocol.box.common.comms.ProtocolSubmitResponseUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolSubmitResponse.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public class ProtocolSubmitResponseImpl implements ProtocolSubmitResponse {
  private Integer operationsApplied;
  private String errorMessage;
  private ProtocolHashedVersionImpl hashedVersionAfterApplication;
  public ProtocolSubmitResponseImpl() {
  }

  public ProtocolSubmitResponseImpl(ProtocolSubmitResponse message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolSubmitResponse message) {
    setOperationsApplied(message.getOperationsApplied());
    if (message.hasErrorMessage()) {
      setErrorMessage(message.getErrorMessage());
    } else {
      clearErrorMessage();
    }
    if (message.hasHashedVersionAfterApplication()) {
      setHashedVersionAfterApplication(message.getHashedVersionAfterApplication());
    } else {
      clearHashedVersionAfterApplication();
    }
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
  public boolean hasErrorMessage() {
    return errorMessage != null;
  }

  @Override
  public void clearErrorMessage() {
    errorMessage = null;
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public void setErrorMessage(String value) {
    this.errorMessage = value;
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

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.operationsApplied = null;
    this.errorMessage = null;
    this.hashedVersionAfterApplication = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolSubmitResponseImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolSubmitResponse) {
      return ProtocolSubmitResponseUtil.isEqual(this, (ProtocolSubmitResponse) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolSubmitResponseUtil.getHashCode(this);
  }

}