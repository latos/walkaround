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

package org.waveprotocol.wave.federation.impl;

import org.waveprotocol.wave.federation.ProtocolWaveletOperation.MutateDocument;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.impl.ProtocolWaveletOperationImpl.MutateDocumentImpl;
import org.waveprotocol.wave.federation.impl.ProtocolDocumentOperationImpl;
import org.waveprotocol.wave.federation.ProtocolWaveletOperation;
import org.waveprotocol.wave.federation.ProtocolWaveletOperationUtil;
import org.waveprotocol.wave.federation.ProtocolWaveletOperation.MutateDocument;
import org.waveprotocol.wave.federation.ProtocolWaveletOperationUtil.MutateDocumentUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProtocolWaveletOperation.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public class ProtocolWaveletOperationImpl implements ProtocolWaveletOperation {

  public static class MutateDocumentImpl implements MutateDocument {
    private String documentId;
    private ProtocolDocumentOperationImpl documentOperation;
    public MutateDocumentImpl() {
    }

    public MutateDocumentImpl(MutateDocument message) {
      copyFrom(message);
    }

    @Override
    public void copyFrom(MutateDocument message) {
      setDocumentId(message.getDocumentId());
      setDocumentOperation(message.getDocumentOperation());
    }

    @Override
    public String getDocumentId() {
      return documentId;
    }

    @Override
    public void setDocumentId(String value) {
      this.documentId = value;
    }

    @Override
    public ProtocolDocumentOperationImpl getDocumentOperation() {
      return new ProtocolDocumentOperationImpl(documentOperation);
    }

    @Override
    public void setDocumentOperation(ProtocolDocumentOperation message) {
      this.documentOperation = new ProtocolDocumentOperationImpl(message);
    }

    /** Provided to subclasses to clear all fields, for example when deserializing. */
    protected void reset() {
      this.documentId = null;
      this.documentOperation = null;
    }

    @Override
    public boolean equals(Object o) {
      return (o instanceof MutateDocumentImpl) && isEqualTo(o);
    }

    @Override
    public boolean isEqualTo(Object o) {
      if (o == this) {
        return true;
      } else if (o instanceof MutateDocument) {
        return MutateDocumentUtil.isEqual(this, (MutateDocument) o);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return MutateDocumentUtil.getHashCode(this);
    }

  }

  private String addParticipant;
  private String removeParticipant;
  private MutateDocumentImpl mutateDocument;
  private Boolean noOp;
  public ProtocolWaveletOperationImpl() {
  }

  public ProtocolWaveletOperationImpl(ProtocolWaveletOperation message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProtocolWaveletOperation message) {
    if (message.hasAddParticipant()) {
      setAddParticipant(message.getAddParticipant());
    } else {
      clearAddParticipant();
    }
    if (message.hasRemoveParticipant()) {
      setRemoveParticipant(message.getRemoveParticipant());
    } else {
      clearRemoveParticipant();
    }
    if (message.hasMutateDocument()) {
      setMutateDocument(message.getMutateDocument());
    } else {
      clearMutateDocument();
    }
    if (message.hasNoOp()) {
      setNoOp(message.getNoOp());
    } else {
      clearNoOp();
    }
  }

  @Override
  public boolean hasAddParticipant() {
    return addParticipant != null;
  }

  @Override
  public void clearAddParticipant() {
    addParticipant = null;
  }

  @Override
  public String getAddParticipant() {
    return addParticipant;
  }

  @Override
  public void setAddParticipant(String value) {
    this.addParticipant = value;
  }

  @Override
  public boolean hasRemoveParticipant() {
    return removeParticipant != null;
  }

  @Override
  public void clearRemoveParticipant() {
    removeParticipant = null;
  }

  @Override
  public String getRemoveParticipant() {
    return removeParticipant;
  }

  @Override
  public void setRemoveParticipant(String value) {
    this.removeParticipant = value;
  }

  @Override
  public boolean hasMutateDocument() {
    return mutateDocument != null;
  }

  @Override
  public void clearMutateDocument() {
    mutateDocument = null;
  }

  @Override
  public MutateDocumentImpl getMutateDocument() {
    return new MutateDocumentImpl(mutateDocument);
  }

  @Override
  public void setMutateDocument(MutateDocument message) {
    this.mutateDocument = new MutateDocumentImpl(message);
  }

  @Override
  public boolean hasNoOp() {
    return noOp != null;
  }

  @Override
  public void clearNoOp() {
    noOp = null;
  }

  @Override
  public boolean getNoOp() {
    return noOp;
  }

  @Override
  public void setNoOp(boolean value) {
    this.noOp = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.addParticipant = null;
    this.removeParticipant = null;
    this.mutateDocument = null;
    this.noOp = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProtocolWaveletOperationImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProtocolWaveletOperation) {
      return ProtocolWaveletOperationUtil.isEqual(this, (ProtocolWaveletOperation) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProtocolWaveletOperationUtil.getHashCode(this);
  }

}