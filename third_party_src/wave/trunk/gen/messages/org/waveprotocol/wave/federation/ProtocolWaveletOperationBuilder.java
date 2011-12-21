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

import org.waveprotocol.wave.federation.ProtocolWaveletOperation.MutateDocument;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.ProtocolWaveletOperationBuilder.MutateDocumentBuilder;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder;
import org.waveprotocol.wave.federation.ProtocolWaveletOperationUtil;
import org.waveprotocol.wave.federation.ProtocolWaveletOperationUtil.MutateDocumentUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolWaveletOperations.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolWaveletOperationBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolWaveletOperation create();
  }

  public static final class MutateDocumentBuilder {

    /** Factory to pass to {@link #build()}. */
    public interface Factory {
      MutateDocument create();
    }

    private String documentId;
    private ProtocolDocumentOperation documentOperation;
    public MutateDocumentBuilder() {
    }

    public MutateDocumentBuilder setDocumentId(String value) {
      this.documentId = value;
      return this;
    }

    public MutateDocumentBuilder setDocumentOperation(ProtocolDocumentOperation message) {
      this.documentOperation = message;
      return this;
    }

    /** Builds a {@link MutateDocument} using this builder and a factory. */
    public MutateDocument build(Factory factory) {
      MutateDocument message = factory.create();
      message.setDocumentId(documentId);
      message.setDocumentOperation(documentOperation);
      return message;
    }

  }

  private String addParticipant;
  private String removeParticipant;
  private MutateDocument mutateDocument;
  private Boolean noOp;
  public ProtocolWaveletOperationBuilder() {
  }

  public ProtocolWaveletOperationBuilder clearAddParticipant() {
    addParticipant = null;
    return this;
  }

  public ProtocolWaveletOperationBuilder setAddParticipant(String value) {
    this.addParticipant = value;
    return this;
  }

  public ProtocolWaveletOperationBuilder clearRemoveParticipant() {
    removeParticipant = null;
    return this;
  }

  public ProtocolWaveletOperationBuilder setRemoveParticipant(String value) {
    this.removeParticipant = value;
    return this;
  }

  public ProtocolWaveletOperationBuilder clearMutateDocument() {
    mutateDocument = null;
    return this;
  }

  public ProtocolWaveletOperationBuilder setMutateDocument(MutateDocument message) {
    this.mutateDocument = message;
    return this;
  }

  public ProtocolWaveletOperationBuilder clearNoOp() {
    noOp = null;
    return this;
  }

  public ProtocolWaveletOperationBuilder setNoOp(boolean value) {
    this.noOp = value;
    return this;
  }

  /** Builds a {@link ProtocolWaveletOperation} using this builder and a factory. */
  public ProtocolWaveletOperation build(Factory factory) {
    ProtocolWaveletOperation message = factory.create();
    message.setAddParticipant(addParticipant);
    message.setRemoveParticipant(removeParticipant);
    message.setMutateDocument(mutateDocument);
    message.setNoOp(noOp);
    return message;
  }

}