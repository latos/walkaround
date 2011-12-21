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

import org.waveprotocol.box.common.comms.WaveViewSnapshot.*;
import org.waveprotocol.box.common.comms.WaveletSnapshot;
import org.waveprotocol.box.common.comms.WaveletSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.Iterator;
import java.util.List;

/**
 * Compares {@link WaveViewSnapshot}s for equality.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public final class WaveViewSnapshotUtil {
  private WaveViewSnapshotUtil() {
  }

  /** Returns true if m1 and m2 are structurally equal. */
  public static boolean isEqual(WaveViewSnapshot m1, WaveViewSnapshot m2) {
    if (!m1.getWaveId().equals(m2.getWaveId())) return false;
    if (!WaveletSnapshotUtil.areAllEqual(m1.getWavelet(), m2.getWavelet())) return false;
    return true;
  }

  /** Returns true if m1 and m2 are equal according to isEqual. */
  public static boolean areAllEqual(List<? extends WaveViewSnapshot> m1,
  List<? extends WaveViewSnapshot> m2) {
    if (m1.size() != m2.size()) return false;
    Iterator<? extends WaveViewSnapshot> i1 = m1.iterator();
    Iterator<? extends WaveViewSnapshot> i2 = m2.iterator();
    while (i1.hasNext()) {
      if (!isEqual(i1.next(), i2.next())) return false;
    }
    return true;
  }

  /** Returns a structural hash code of message. */
  public static int getHashCode(WaveViewSnapshot message) {
    int result = 1;
    result = (31 * result) + message.getWaveId().hashCode();
    result = (31 * result) + message.getWavelet().hashCode();
    return result;
  }

}