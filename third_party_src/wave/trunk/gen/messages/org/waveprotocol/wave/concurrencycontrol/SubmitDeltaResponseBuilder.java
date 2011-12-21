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

import org.waveprotocol.wave.concurrencycontrol.ResponseStatus;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.concurrencycontrol.ResponseStatusBuilder;
import org.waveprotocol.wave.federation.ProtocolHashedVersionBuilder;
import org.waveprotocol.wave.concurrencycontrol.SubmitDeltaResponseUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for SubmitDeltaResponses.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public final class SubmitDeltaResponseBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    SubmitDeltaResponse create();
  }

  private ResponseStatus status;
  private Integer operationsApplied;
  private ProtocolHashedVersion hashedVersionAfterApplication;
  private Long timestampAfterApplication;
  public SubmitDeltaResponseBuilder() {
  }

  public SubmitDeltaResponseBuilder setStatus(ResponseStatus message) {
    this.status = message;
    return this;
  }

  public SubmitDeltaResponseBuilder setOperationsApplied(int value) {
    this.operationsApplied = value;
    return this;
  }

  public SubmitDeltaResponseBuilder clearHashedVersionAfterApplication() {
    hashedVersionAfterApplication = null;
    return this;
  }

  public SubmitDeltaResponseBuilder setHashedVersionAfterApplication(ProtocolHashedVersion message) {
    this.hashedVersionAfterApplication = message;
    return this;
  }

  public SubmitDeltaResponseBuilder clearTimestampAfterApplication() {
    timestampAfterApplication = null;
    return this;
  }

  public SubmitDeltaResponseBuilder setTimestampAfterApplication(long value) {
    this.timestampAfterApplication = value;
    return this;
  }

  /** Builds a {@link SubmitDeltaResponse} using this builder and a factory. */
  public SubmitDeltaResponse build(Factory factory) {
    SubmitDeltaResponse message = factory.create();
    message.setStatus(status);
    message.setOperationsApplied(operationsApplied);
    message.setHashedVersionAfterApplication(hashedVersionAfterApplication);
    message.setTimestampAfterApplication(timestampAfterApplication);
    return message;
  }

}