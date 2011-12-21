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

import org.waveprotocol.wave.federation.ProtocolWaveletDelta;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.box.common.comms.WaveletSnapshot;
import org.waveprotocol.wave.federation.impl.ProtocolWaveletDeltaImpl;
import org.waveprotocol.wave.federation.impl.ProtocolHashedVersionImpl;
import org.waveprotocol.box.common.comms.impl.WaveletSnapshotImpl;
import org.waveprotocol.box.common.comms.ProtocolWaveletUpdate;
import org.waveprotocol.box.common.comms.ProtocolWaveletUpdateUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolWaveletUpdate.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public class ProtocolWaveletUpdateImpl implements ProtocolWaveletUpdate {
  private String waveletName;
  private final List<ProtocolWaveletDeltaImpl> appliedDelta = new ArrayList<ProtocolWaveletDeltaImpl>();
  private ProtocolHashedVersionImpl commitNotice;
  private ProtocolHashedVersionImpl resultingVersion;
  private WaveletSnapshotImpl snapshot;
  private Boolean marker;
  private String channelId;
  public ProtocolWaveletUpdateImpl() {
  }

  public ProtocolWaveletUpdateImpl(ProtocolWaveletUpdate message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolWaveletUpdate message) {
    setWaveletName(message.getWaveletName());
    clearAppliedDelta();
    for (ProtocolWaveletDelta field : message.getAppliedDelta()) {
      addAppliedDelta(new ProtocolWaveletDeltaImpl(field));
    }
    if (message.hasCommitNotice()) {
      setCommitNotice(message.getCommitNotice());
    } else {
      clearCommitNotice();
    }
    if (message.hasResultingVersion()) {
      setResultingVersion(message.getResultingVersion());
    } else {
      clearResultingVersion();
    }
    if (message.hasSnapshot()) {
      setSnapshot(message.getSnapshot());
    } else {
      clearSnapshot();
    }
    if (message.hasMarker()) {
      setMarker(message.getMarker());
    } else {
      clearMarker();
    }
    if (message.hasChannelId()) {
      setChannelId(message.getChannelId());
    } else {
      clearChannelId();
    }
  }

  @Override
  public String getWaveletName() {
    return waveletName;
  }

  @Override
  public void setWaveletName(String value) {
    this.waveletName = value;
  }

  @Override
  public List<ProtocolWaveletDeltaImpl> getAppliedDelta() {
    return Collections.unmodifiableList(appliedDelta);
  }

  @Override
  public void addAllAppliedDelta(List<? extends ProtocolWaveletDelta> messages) {
    for (ProtocolWaveletDelta message : messages) {
      addAppliedDelta(message);
    }
  }

  @Override
  public ProtocolWaveletDeltaImpl getAppliedDelta(int n) {
    return new ProtocolWaveletDeltaImpl(appliedDelta.get(n));
  }

  @Override
  public void setAppliedDelta(int n, ProtocolWaveletDelta message) {
    this.appliedDelta.set(n, new ProtocolWaveletDeltaImpl(message));
  }

  @Override
  public int getAppliedDeltaSize() {
    return appliedDelta.size();
  }

  @Override
  public void addAppliedDelta(ProtocolWaveletDelta message) {
    this.appliedDelta.add(new ProtocolWaveletDeltaImpl(message));
  }

  @Override
  public void clearAppliedDelta() {
    appliedDelta.clear();
  }

  @Override
  public boolean hasCommitNotice() {
    return commitNotice != null;
  }

  @Override
  public void clearCommitNotice() {
    commitNotice = null;
  }

  @Override
  public ProtocolHashedVersionImpl getCommitNotice() {
    return new ProtocolHashedVersionImpl(commitNotice);
  }

  @Override
  public void setCommitNotice(ProtocolHashedVersion message) {
    this.commitNotice = new ProtocolHashedVersionImpl(message);
  }

  @Override
  public boolean hasResultingVersion() {
    return resultingVersion != null;
  }

  @Override
  public void clearResultingVersion() {
    resultingVersion = null;
  }

  @Override
  public ProtocolHashedVersionImpl getResultingVersion() {
    return new ProtocolHashedVersionImpl(resultingVersion);
  }

  @Override
  public void setResultingVersion(ProtocolHashedVersion message) {
    this.resultingVersion = new ProtocolHashedVersionImpl(message);
  }

  @Override
  public boolean hasSnapshot() {
    return snapshot != null;
  }

  @Override
  public void clearSnapshot() {
    snapshot = null;
  }

  @Override
  public WaveletSnapshotImpl getSnapshot() {
    return new WaveletSnapshotImpl(snapshot);
  }

  @Override
  public void setSnapshot(WaveletSnapshot message) {
    this.snapshot = new WaveletSnapshotImpl(message);
  }

  @Override
  public boolean hasMarker() {
    return marker != null;
  }

  @Override
  public void clearMarker() {
    marker = null;
  }

  @Override
  public boolean getMarker() {
    return marker;
  }

  @Override
  public void setMarker(boolean value) {
    this.marker = value;
  }

  @Override
  public boolean hasChannelId() {
    return channelId != null;
  }

  @Override
  public void clearChannelId() {
    channelId = null;
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
    this.waveletName = null;
    this.appliedDelta.clear();
    this.commitNotice = null;
    this.resultingVersion = null;
    this.snapshot = null;
    this.marker = null;
    this.channelId = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolWaveletUpdateImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolWaveletUpdate) {
      return ProtocolWaveletUpdateUtil.isEqual(this, (ProtocolWaveletUpdate) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolWaveletUpdateUtil.getHashCode(this);
  }

}