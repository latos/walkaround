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

package com.google.walkaround.wave.shared;

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

/**
 * A factory for creating message objects.
 *
 * @author zdwang@google.com (David Wang)
 */
public abstract class MessageFactoryHelper {

  /**
   * The delegate factory.
   */
  protected static MessageFactory factory = null;

  public static OperationBatch createOperationBatch() {
    return factory.createOperationBatch();
  }

  public static WaveletDiffSnapshot createWaveletDiffSnapshot() {
    return factory.createWaveletDiffSnapshot();
  }

  public static DocumentDiffSnapshot createDocumentDiffSnapshot() {
    return factory.createDocumentDiffSnapshot();
  }

  public static ProtocolWaveletOperation createWaveOp() {
    return factory.createWaveOp();
  }

  public static KeyValuePair createDocumentKeyValuePair() {
    return factory.createDocumentKeyValuePair();
  }

  public static KeyValueUpdate createDocumentKeyValueUpdate() {
    return factory.createDocumentKeyValueUpdate();
  }

  public static ElementStart createDocumentElementStart() {
    return factory.createDocumentElementStart();
  }

  public static ReplaceAttributes createDocumentReplaceAttributes() {
    return factory.createDocumentReplaceAttributes();
  }

  public static UpdateAttributes createDocumentUpdateAttributes() {
    return factory.createDocumentUpdateAttributes();
  }

  public static AnnotationBoundary createDocumentAnnotationBoundary() {
    return factory.createDocumentAnnotationBoundary();
  }

  public static Component createDocumentOperationComponent() {
    return factory.createDocumentOperationComponent();
  }

  public static ProtocolDocumentOperation createDocumentOperation() {
    return factory.createDocumentOperation();
  }

  public static MutateDocument createBlipContentMutation() {
    return factory.createMutateDocument();
  }

  /**
   * Gets the factory singleton.
   *
   * @return the global factory.
   */
  public static MessageFactory getFactory() {
    return factory;
  }

  /**
   * Sets the factory singleton
   *
   * @param f the global factory to use.
   */
  public static void setFactory(MessageFactory f) {
    factory = f;
  }

  public static WalkaroundWaveletSnapshot createWaveletSnapshot() {
    return factory.createWaveletSnapshot();
  }

  public static WalkaroundDocumentSnapshot createDocumentSnapshot() {
    return factory.createDocumentSnapshot();
  }

  public static Delta createDelta() {
    return factory.createDelta();
  }

}
