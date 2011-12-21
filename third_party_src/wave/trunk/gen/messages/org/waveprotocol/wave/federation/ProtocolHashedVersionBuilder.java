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

package org.waveprotocol.wave.federation;

import org.waveprotocol.wave.federation.ProtocolHashedVersionUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolHashedVersions.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolHashedVersionBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolHashedVersion create();
  }

  private Double version;
  private Blob historyHash;
  public ProtocolHashedVersionBuilder() {
  }

  public ProtocolHashedVersionBuilder setVersion(double value) {
    this.version = value;
    return this;
  }

  public ProtocolHashedVersionBuilder setHistoryHash(Blob value) {
    this.historyHash = value;
    return this;
  }

  /** Builds a {@link ProtocolHashedVersion} using this builder and a factory. */
  public ProtocolHashedVersion build(Factory factory) {
    ProtocolHashedVersion message = factory.create();
    message.setVersion(version);
    message.setHistoryHash(historyHash);
    return message;
  }

}