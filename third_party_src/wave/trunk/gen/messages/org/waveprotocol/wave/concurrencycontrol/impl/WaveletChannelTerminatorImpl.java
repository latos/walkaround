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
import org.waveprotocol.wave.concurrencycontrol.impl.ResponseStatusImpl;
import org.waveprotocol.wave.concurrencycontrol.WaveletChannelTerminator;
import org.waveprotocol.wave.concurrencycontrol.WaveletChannelTerminatorUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of WaveletChannelTerminator.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class WaveletChannelTerminatorImpl implements WaveletChannelTerminator {
  private ResponseStatusImpl status;
  public WaveletChannelTerminatorImpl() {
  }

  public WaveletChannelTerminatorImpl(WaveletChannelTerminator message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(WaveletChannelTerminator message) {
    setStatus(message.getStatus());
  }

  @Override
  public ResponseStatusImpl getStatus() {
    return new ResponseStatusImpl(status);
  }

  @Override
  public void setStatus(ResponseStatus message) {
    this.status = new ResponseStatusImpl(message);
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.status = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof WaveletChannelTerminatorImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof WaveletChannelTerminator) {
      return WaveletChannelTerminatorUtil.isEqual(this, (WaveletChannelTerminator) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return WaveletChannelTerminatorUtil.getHashCode(this);
  }

}