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

import com.google.common.base.Preconditions;
import com.google.walkaround.proto.Delta;
import com.google.walkaround.proto.OperationBatch;
import com.google.walkaround.proto.ProtocolWaveletOperation;
import com.google.walkaround.proto.WalkaroundWaveletSnapshot;
import com.google.walkaround.proto.WaveletDiffSnapshot;
import com.google.walkaround.proto.jso.DeltaJsoImpl;
import com.google.walkaround.proto.jso.OperationBatchJsoImpl;
import com.google.walkaround.proto.jso.ProtocolWaveletOperationJsoImpl;
import com.google.walkaround.proto.jso.WalkaroundWaveletSnapshotJsoImpl;
import com.google.walkaround.proto.jso.WaveletDiffSnapshotJsoImpl;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.wave.shared.MessageFactoryHelper;
import com.google.walkaround.wave.shared.MessageSerializer;

/**
 * ClientWaveSerializer is capable of both serializing and deserializing
 * Wavelets and Deltas to and from JSON in client environment.
 *
 * @author piotrkaleta@google.com (Piotr Kaleta)
 */
public class ClientMessageSerializer implements MessageSerializer {

  public ClientMessageSerializer() {
    if (MessageFactoryHelper.getFactory() == null) {
      MessageFactoryHelper.setFactory(new MessageFactoryClient());
    } else {
      Preconditions.checkState(MessageFactoryHelper.getFactory() instanceof MessageFactoryClient);
    }
  }

  @Override
  public String serializeOp(ProtocolWaveletOperation waveletOp) {
    return ((ProtocolWaveletOperationJsoImpl) waveletOp).toJson();
  }

  @Override
  public ProtocolWaveletOperationJsoImpl deserializeOp(String serializedDelta)
      throws MessageException {
    return JsUtil.eval(serializedDelta).cast();
  }

  @Override
  public String serializeWavelet(WalkaroundWaveletSnapshot waveletSnapshot) {
    return ((WalkaroundWaveletSnapshotJsoImpl) waveletSnapshot).toJson();
  }

  @Override
  public WalkaroundWaveletSnapshotJsoImpl deserializeWavelet(String serializedSnapshot)
      throws MessageException {
    return JsUtil.eval(serializedSnapshot).cast();
  }

  @Override
  public String serializeDiff(WaveletDiffSnapshot diff) {
    return ((WaveletDiffSnapshotJsoImpl) diff).toJson();
  }

  @Override
  public WaveletDiffSnapshotJsoImpl deserializeDiff(String input) throws MessageException {
    return JsUtil.eval(input).cast();
  }

  @Override
  public String serializeOperationBatch(OperationBatch operationBatch) {
    return ((OperationBatchJsoImpl) operationBatch).toJson();
  }

  @Override
  public OperationBatchJsoImpl deserializeOperationBatch(String input) throws MessageException {
    return JsUtil.eval(input).cast();
  }

  @Override
  public String serializeDelta(Delta input) {
    return ((DeltaJsoImpl) input).toJson();
  }

  @Override
  public DeltaJsoImpl deserializeDelta(String input) throws MessageException {
    return JsUtil.eval(input).cast();
  }

}
