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

import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.federation.ProtocolHashedVersionBuilder;
import org.waveprotocol.box.common.comms.ProtocolSubmitResponseUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolSubmitResponses.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public final class ProtocolSubmitResponseBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolSubmitResponse create();
  }

  private Integer operationsApplied;
  private String errorMessage;
  private ProtocolHashedVersion hashedVersionAfterApplication;
  public ProtocolSubmitResponseBuilder() {
  }

  public ProtocolSubmitResponseBuilder setOperationsApplied(int value) {
    this.operationsApplied = value;
    return this;
  }

  public ProtocolSubmitResponseBuilder clearErrorMessage() {
    errorMessage = null;
    return this;
  }

  public ProtocolSubmitResponseBuilder setErrorMessage(String value) {
    this.errorMessage = value;
    return this;
  }

  public ProtocolSubmitResponseBuilder clearHashedVersionAfterApplication() {
    hashedVersionAfterApplication = null;
    return this;
  }

  public ProtocolSubmitResponseBuilder setHashedVersionAfterApplication(ProtocolHashedVersion message) {
    this.hashedVersionAfterApplication = message;
    return this;
  }

  /** Builds a {@link ProtocolSubmitResponse} using this builder and a factory. */
  public ProtocolSubmitResponse build(Factory factory) {
    ProtocolSubmitResponse message = factory.create();
    message.setOperationsApplied(operationsApplied);
    message.setErrorMessage(errorMessage);
    message.setHashedVersionAfterApplication(hashedVersionAfterApplication);
    return message;
  }

}