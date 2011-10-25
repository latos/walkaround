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

package com.google.walkaround.wave.client;

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
import com.google.walkaround.proto.jso.DeltaJsoImpl;
import com.google.walkaround.proto.jso.DocumentDiffSnapshotJsoImpl;
import com.google.walkaround.proto.jso.OperationBatchJsoImpl;
import com.google.walkaround.proto.jso.ProtocolDocumentOperationJsoImpl;
import com.google.walkaround.proto.jso.ProtocolDocumentOperationJsoImpl.ComponentJsoImpl;
import com.google.walkaround.proto.jso.ProtocolDocumentOperationJsoImpl.ComponentJsoImpl.AnnotationBoundaryJsoImpl;
import com.google.walkaround.proto.jso.ProtocolDocumentOperationJsoImpl.ComponentJsoImpl.ElementStartJsoImpl;
import com.google.walkaround.proto.jso.ProtocolDocumentOperationJsoImpl.ComponentJsoImpl.KeyValuePairJsoImpl;
import com.google.walkaround.proto.jso.ProtocolDocumentOperationJsoImpl.ComponentJsoImpl.KeyValueUpdateJsoImpl;
import com.google.walkaround.proto.jso.ProtocolDocumentOperationJsoImpl.ComponentJsoImpl.ReplaceAttributesJsoImpl;
import com.google.walkaround.proto.jso.ProtocolDocumentOperationJsoImpl.ComponentJsoImpl.UpdateAttributesJsoImpl;
import com.google.walkaround.proto.jso.ProtocolWaveletOperationJsoImpl;
import com.google.walkaround.proto.jso.ProtocolWaveletOperationJsoImpl.MutateDocumentJsoImpl;
import com.google.walkaround.proto.jso.WalkaroundDocumentSnapshotJsoImpl;
import com.google.walkaround.proto.jso.WalkaroundWaveletSnapshotJsoImpl;
import com.google.walkaround.proto.jso.WaveletDiffSnapshotJsoImpl;
import com.google.walkaround.wave.shared.MessageFactory;

/**
 * Creates client side implementation of messages.
 *
 * @author zdwang@google.com (David Wang)
 */
public class MessageFactoryClient implements MessageFactory {

  @Override
  public OperationBatch createOperationBatch() {
    return OperationBatchJsoImpl.create();
  }

  @Override
  public ProtocolWaveletOperation createWaveOp() {
    return ProtocolWaveletOperationJsoImpl.create();
  }

  @Override
  public AnnotationBoundary createDocumentAnnotationBoundary() {
    return AnnotationBoundaryJsoImpl.create();
  }

  @Override
  public KeyValuePair createDocumentKeyValuePair() {
    return KeyValuePairJsoImpl.create();
  }

  @Override
  public KeyValueUpdate createDocumentKeyValueUpdate() {
    return KeyValueUpdateJsoImpl.create();
  }

  @Override
  public ProtocolDocumentOperation createDocumentOperation() {
    return ProtocolDocumentOperationJsoImpl.create();
  }

  @Override
  public Component createDocumentOperationComponent() {
    return ComponentJsoImpl.create();
  }

  @Override
  public ReplaceAttributes createDocumentReplaceAttributes() {
    return ReplaceAttributesJsoImpl.create();
  }

  @Override
  public MutateDocument createMutateDocument() {
    return MutateDocumentJsoImpl.create();
  }

  @Override
  public ElementStart createDocumentElementStart() {
    return ElementStartJsoImpl.create();
  }

  @Override
  public UpdateAttributes createDocumentUpdateAttributes() {
    return UpdateAttributesJsoImpl.create();
  }

  @Override
  public WalkaroundDocumentSnapshot createDocumentSnapshot() {
    return WalkaroundDocumentSnapshotJsoImpl.create();
  }

  @Override
  public WalkaroundWaveletSnapshot createWaveletSnapshot() {
    return WalkaroundWaveletSnapshotJsoImpl.create();
  }

  @Override
  public DocumentDiffSnapshot createDocumentDiffSnapshot() {
    return DocumentDiffSnapshotJsoImpl.create();
  }

  @Override
  public WaveletDiffSnapshot createWaveletDiffSnapshot() {
    return WaveletDiffSnapshotJsoImpl.create();
  }

  @Override
  public Delta createDelta() {
    return DeltaJsoImpl.create();
  }

}
