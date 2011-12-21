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

package org.waveprotocol.wave.concurrencycontrol;

import org.waveprotocol.wave.concurrencycontrol.WaveletUpdate;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.concurrencycontrol.WaveletChannelTerminator;
import org.waveprotocol.wave.communication.Blob;
import org.waveprotocol.wave.communication.ProtoEnums;
import java.util.List;

/**
 * Model interface for OpenWaveletChannelStream.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public interface OpenWaveletChannelStream {

  /** Does a deep copy from model. */
  void copyFrom(OpenWaveletChannelStream model);

  /**
   * Tests if this model is equal to another object.
   * "Equal" is recursively defined as:
   * <ul>
   * <li>both objects implement this interface,</li>
   * <li>all corresponding primitive fields of both objects have the same value, and</li>
   * <li>all corresponding nested-model fields of both objects are "equal".</li>
   * </ul>
   *
   * This is a coarser equivalence than provided by the equals() methods. Two
   * objects may not be equal() to each other, but may be isEqualTo() each other.
   */
  boolean isEqualTo(Object o);

  /** Returns whether channelId has been set. */
  boolean hasChannelId();

  /** Clears the value of channelId. */
  void clearChannelId();

  /** Returns channelId, or null if hasn't been set. */
  String getChannelId();

  /** Sets channelId. */
  void setChannelId(String channelId);

  /** Returns whether delta has been set. */
  boolean hasDelta();

  /** Clears the value of delta. */
  void clearDelta();

  /** Returns delta, or null if hasn't been set. */
  WaveletUpdate getDelta();

  /** Sets delta. */
  void setDelta(WaveletUpdate delta);

  /** Returns whether commitVersion has been set. */
  boolean hasCommitVersion();

  /** Clears the value of commitVersion. */
  void clearCommitVersion();

  /** Returns commitVersion, or null if hasn't been set. */
  ProtocolHashedVersion getCommitVersion();

  /** Sets commitVersion. */
  void setCommitVersion(ProtocolHashedVersion commitVersion);

  /** Returns whether terminator has been set. */
  boolean hasTerminator();

  /** Clears the value of terminator. */
  void clearTerminator();

  /** Returns terminator, or null if hasn't been set. */
  WaveletChannelTerminator getTerminator();

  /** Sets terminator. */
  void setTerminator(WaveletChannelTerminator terminator);
}