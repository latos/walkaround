/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.walkaround.wave.server.googleimport;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.walkaround.proto.RobotSearchDigest;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.wave.server.gxp.SourceInstance;

import org.waveprotocol.wave.model.id.WaveletId;

import javax.annotation.Nullable;

/**
 * Identifies a wave on a remote instance.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class RemoteConvWavelet {

  private final SourceInstance sourceInstance;
  private final RobotSearchDigest digest;
  // WaveId is in the digest.
  private final WaveletId waveletId;
  @Nullable private SlobId privateLocalId;
  @Nullable private SlobId sharedLocalId;

  public RemoteConvWavelet(SourceInstance sourceInstance,
      RobotSearchDigest digest,
      WaveletId waveletId,
      @Nullable SlobId privateLocalId,
      @Nullable SlobId sharedLocalId) {
    this.sourceInstance = checkNotNull(sourceInstance, "Null sourceInstance");
    this.digest = checkNotNull(digest, "Null digest");
    this.waveletId = checkNotNull(waveletId, "Null waveletId");
    this.privateLocalId = privateLocalId;
    this.sharedLocalId = sharedLocalId;
  }

  public SourceInstance getSourceInstance() {
    return sourceInstance;
  }

  public RobotSearchDigest getDigest() {
    return digest;
  }

  public WaveletId getWaveletId() {
    return waveletId;
  }

  @Nullable public SlobId getPrivateLocalId() {
    return privateLocalId;
  }

  public RemoteConvWavelet setPrivateLocalId(@Nullable SlobId privateLocalId) {
    this.privateLocalId = privateLocalId;
    return this;
  }

  @Nullable public SlobId getSharedLocalId() {
    return sharedLocalId;
  }

  public RemoteConvWavelet setSharedLocalId(@Nullable SlobId sharedLocalId) {
    this.sharedLocalId = sharedLocalId;
    return this;
  }

  @Override public String toString() {
    return getClass().getSimpleName() + "("
        + sourceInstance + ", "
        + digest + ", "
        + waveletId + ", "
        + privateLocalId + ", "
        + sharedLocalId
        + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof RemoteConvWavelet)) { return false; }
    RemoteConvWavelet other = (RemoteConvWavelet) o;
    return Objects.equal(sourceInstance, other.sourceInstance)
        && Objects.equal(digest, other.digest)
        && Objects.equal(waveletId, other.waveletId)
        && Objects.equal(privateLocalId, other.privateLocalId)
        && Objects.equal(sharedLocalId, other.sharedLocalId);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(sourceInstance, digest, waveletId, privateLocalId, sharedLocalId);
  }

}
