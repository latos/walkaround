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

import org.waveprotocol.wave.concurrencycontrol.CloseWaveletChannelRequest;
import org.waveprotocol.wave.concurrencycontrol.CloseWaveletChannelRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of CloseWaveletChannelRequest.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class CloseWaveletChannelRequestImpl implements CloseWaveletChannelRequest {
  private String channelId;
  public CloseWaveletChannelRequestImpl() {
  }

  public CloseWaveletChannelRequestImpl(CloseWaveletChannelRequest message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(CloseWaveletChannelRequest message) {
    setChannelId(message.getChannelId());
  }

  @Override
  public String getChannelId() {
    return channelId;
  }

  @Override
  public void setChannelId(String value) {
    this.channelId = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.channelId = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof CloseWaveletChannelRequestImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof CloseWaveletChannelRequest) {
      return CloseWaveletChannelRequestUtil.isEqual(this, (CloseWaveletChannelRequest) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return CloseWaveletChannelRequestUtil.getHashCode(this);
  }

}