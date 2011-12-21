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

package org.waveprotocol.box.common.comms;

import org.waveprotocol.box.common.comms.ProtocolOpenRequest.*;
import org.waveprotocol.box.common.comms.WaveletVersion;
import org.waveprotocol.box.common.comms.WaveletVersionUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.Iterator;
import java.util.List;

/**
 * Compares {@link ProtocolOpenRequest}s for equality.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public final class ProtocolOpenRequestUtil {
  private ProtocolOpenRequestUtil() {
  }

  /** Returns true if m1 and m2 are structurally equal. */
  public static boolean isEqual(ProtocolOpenRequest m1, ProtocolOpenRequest m2) {
    if (!m1.getParticipantId().equals(m2.getParticipantId())) return false;
    if (!m1.getWaveId().equals(m2.getWaveId())) return false;
    if (!m1.getWaveletIdPrefix().equals(m2.getWaveletIdPrefix())) return false;
    if (!WaveletVersionUtil.areAllEqual(m1.getKnownWavelet(), m2.getKnownWavelet())) return false;
    return true;
  }

  /** Returns true if m1 and m2 are equal according to isEqual. */
  public static boolean areAllEqual(List<? extends ProtocolOpenRequest> m1,
  List<? extends ProtocolOpenRequest> m2) {
    if (m1.size() != m2.size()) return false;
    Iterator<? extends ProtocolOpenRequest> i1 = m1.iterator();
    Iterator<? extends ProtocolOpenRequest> i2 = m2.iterator();
    while (i1.hasNext()) {
      if (!isEqual(i1.next(), i2.next())) return false;
    }
    return true;
  }

  /** Returns a structural hash code of message. */
  public static int getHashCode(ProtocolOpenRequest message) {
    int result = 1;
    result = (31 * result) + message.getParticipantId().hashCode();
    result = (31 * result) + message.getWaveId().hashCode();
    result = (31 * result) + message.getWaveletIdPrefix().hashCode();
    result = (31 * result) + message.getKnownWavelet().hashCode();
    return result;
  }

}