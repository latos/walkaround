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

package com.google.walkaround.wave.server.model;

import com.google.walkaround.proto.Delta;
import com.google.walkaround.proto.DocumentDiffSnapshot;
import com.google.walkaround.proto.OperationBatch;
import com.google.walkaround.proto.ProtocolDocumentOperation;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.AnnotationBoundary;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.ElementStart;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.KeyValuePair;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.KeyValueUpdate;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.ReplaceAttributes;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.UpdateAttributes;
import com.google.walkaround.proto.ProtocolWaveletOperation;
import com.google.walkaround.proto.ProtocolWaveletOperation.MutateDocument;
import com.google.walkaround.proto.WalkaroundDocumentSnapshot;
import com.google.walkaround.proto.WalkaroundWaveletSnapshot;
import com.google.walkaround.proto.WaveletDiffSnapshot;
import com.google.walkaround.proto.gson.DeltaGsonImpl;
import com.google.walkaround.proto.gson.DocumentDiffSnapshotGsonImpl;
import com.google.walkaround.proto.gson.OperationBatchGsonImpl;
import com.google.walkaround.proto.gson.ProtocolDocumentOperationGsonImpl;
import com.google.walkaround.proto.gson.ProtocolDocumentOperationGsonImpl.ComponentGsonImpl;
import com.google.walkaround.proto.gson.ProtocolDocumentOperationGsonImpl.ComponentGsonImpl.AnnotationBoundaryGsonImpl;
import com.google.walkaround.proto.gson.ProtocolDocumentOperationGsonImpl.ComponentGsonImpl.ElementStartGsonImpl;
import com.google.walkaround.proto.gson.ProtocolDocumentOperationGsonImpl.ComponentGsonImpl.KeyValuePairGsonImpl;
import com.google.walkaround.proto.gson.ProtocolDocumentOperationGsonImpl.ComponentGsonImpl.KeyValueUpdateGsonImpl;
import com.google.walkaround.proto.gson.ProtocolDocumentOperationGsonImpl.ComponentGsonImpl.ReplaceAttributesGsonImpl;
import com.google.walkaround.proto.gson.ProtocolDocumentOperationGsonImpl.ComponentGsonImpl.UpdateAttributesGsonImpl;
import com.google.walkaround.proto.gson.ProtocolWaveletOperationGsonImpl;
import com.google.walkaround.proto.gson.ProtocolWaveletOperationGsonImpl.MutateDocumentGsonImpl;
import com.google.walkaround.proto.gson.WalkaroundDocumentSnapshotGsonImpl;
import com.google.walkaround.proto.gson.WalkaroundWaveletSnapshotGsonImpl;
import com.google.walkaround.proto.gson.WaveletDiffSnapshotGsonImpl;
import com.google.walkaround.wave.shared.MessageFactory;

public class MessageFactoryServer implements MessageFactory {

  @Override
  public OperationBatch createOperationBatch() {
    return new OperationBatchGsonImpl();
  }

  @Override
  public ProtocolWaveletOperation createWaveOp() {
    return new ProtocolWaveletOperationGsonImpl();
  }

  @Override
  public ElementStart createDocumentElementStart() {
    return new ElementStartGsonImpl();
  }

  @Override
  public ReplaceAttributes createDocumentReplaceAttributes() {
    return new ReplaceAttributesGsonImpl();
  }

  @Override
  public UpdateAttributes createDocumentUpdateAttributes() {
    return new UpdateAttributesGsonImpl();
  }

  @Override
  public AnnotationBoundary createDocumentAnnotationBoundary() {
    return new AnnotationBoundaryGsonImpl();
  }

  @Override
  public Component createDocumentOperationComponent() {
    return new ComponentGsonImpl();
  }

  @Override
  public ProtocolDocumentOperation createDocumentOperation() {
    return new ProtocolDocumentOperationGsonImpl();
  }

  @Override
  public MutateDocument createMutateDocument() {
    return new MutateDocumentGsonImpl();
  }

  @Override
  public KeyValuePair createDocumentKeyValuePair() {
    return new KeyValuePairGsonImpl();
  }

  @Override
  public KeyValueUpdate createDocumentKeyValueUpdate() {
    return new KeyValueUpdateGsonImpl();
  }

  @Override
  public WalkaroundDocumentSnapshot createDocumentSnapshot() {
    return new WalkaroundDocumentSnapshotGsonImpl();
  }

  @Override
  public WalkaroundWaveletSnapshot createWaveletSnapshot() {
    return new WalkaroundWaveletSnapshotGsonImpl();
  }

  @Override
  public DocumentDiffSnapshot createDocumentDiffSnapshot() {
    return new DocumentDiffSnapshotGsonImpl();
  }

  @Override
  public WaveletDiffSnapshot createWaveletDiffSnapshot() {
    return new WaveletDiffSnapshotGsonImpl();
  }

  @Override
  public Delta createDelta() {
    return new DeltaGsonImpl();
  }

}
