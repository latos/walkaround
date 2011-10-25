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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.walkaround.proto.RobotSearchDigest;
import com.google.walkaround.wave.server.gxp.SourceInstance;

/**
 * Identifies a wave on a remote instance.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class RemoteWave {

  private final SourceInstance sourceInstance;
  private final RobotSearchDigest digest;

  public RemoteWave(SourceInstance sourceInstance,
      RobotSearchDigest digest) {
    Preconditions.checkNotNull(sourceInstance, "Null sourceInstance");
    Preconditions.checkNotNull(digest, "Null digest");
    this.sourceInstance = sourceInstance;
    this.digest = digest;
  }

  public SourceInstance getSourceInstance() {
    return sourceInstance;
  }

  public RobotSearchDigest getDigest() {
    return digest;
  }

  @Override public String toString() {
    return "RemoteWave("
        + sourceInstance + ", "
        + digest
        + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof RemoteWave)) { return false; }
    RemoteWave other = (RemoteWave) o;
    return Objects.equal(sourceInstance, other.sourceInstance)
        && Objects.equal(digest, other.digest);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(sourceInstance, digest);
  }

}
