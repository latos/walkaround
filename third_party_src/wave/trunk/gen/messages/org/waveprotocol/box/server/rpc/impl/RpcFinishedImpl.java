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

package org.waveprotocol.box.server.rpc.impl;

import org.waveprotocol.box.server.rpc.RpcFinished;
import org.waveprotocol.box.server.rpc.RpcFinishedUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of RpcFinished.
 *
 * Generated from org/waveprotocol/box/server/rpc/rpc.proto. Do not edit.
 */
public class RpcFinishedImpl implements RpcFinished {
  private Boolean failed;
  private String errorText;
  public RpcFinishedImpl() {
  }

  public RpcFinishedImpl(RpcFinished message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(RpcFinished message) {
    setFailed(message.getFailed());
    if (message.hasErrorText()) {
      setErrorText(message.getErrorText());
    } else {
      clearErrorText();
    }
  }

  @Override
  public boolean getFailed() {
    return failed;
  }

  @Override
  public void setFailed(boolean value) {
    this.failed = value;
  }

  @Override
  public boolean hasErrorText() {
    return errorText != null;
  }

  @Override
  public void clearErrorText() {
    errorText = null;
  }

  @Override
  public String getErrorText() {
    return errorText;
  }

  @Override
  public void setErrorText(String value) {
    this.errorText = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.failed = null;
    this.errorText = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof RpcFinishedImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof RpcFinished) {
      return RpcFinishedUtil.isEqual(this, (RpcFinished) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return RpcFinishedUtil.getHashCode(this);
  }

}