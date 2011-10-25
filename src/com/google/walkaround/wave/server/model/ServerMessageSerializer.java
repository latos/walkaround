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

import com.google.common.base.Preconditions;
import com.google.walkaround.proto.Delta;
import com.google.walkaround.proto.OperationBatch;
import com.google.walkaround.proto.ProtocolWaveletOperation;
import com.google.walkaround.proto.WalkaroundWaveletSnapshot;
import com.google.walkaround.proto.WaveletDiffSnapshot;
import com.google.walkaround.proto.gson.DeltaGsonImpl;
import com.google.walkaround.proto.gson.OperationBatchGsonImpl;
import com.google.walkaround.proto.gson.ProtocolWaveletOperationGsonImpl;
import com.google.walkaround.proto.gson.WalkaroundWaveletSnapshotGsonImpl;
import com.google.walkaround.proto.gson.WaveletDiffSnapshotGsonImpl;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.wave.shared.MessageFactoryHelper;
import com.google.walkaround.wave.shared.MessageSerializer;

import org.waveprotocol.wave.communication.gson.GsonSerializable;

/**
 * Server-side version of serializer. It (de)serializes wavelets and deltas to
 * and from JSON in server environment.
 *
 * @author piotrkaleta@google.com (Piotr Kaleta)
 */
public class ServerMessageSerializer implements MessageSerializer {

  /**
   * Constructs new server-side serializer
   */
  public ServerMessageSerializer() {
    if (MessageFactoryHelper.getFactory() == null) {
      MessageFactoryHelper.setFactory(new MessageFactoryServer());
    } else {
      Preconditions.checkState(MessageFactoryHelper.getFactory() instanceof MessageFactoryServer);
    }
  }

  @Override
  public String serializeWavelet(WalkaroundWaveletSnapshot waveletSnapshot) {
    return GsonProto.toJson((GsonSerializable) waveletSnapshot);
  }

  @Override
  public WalkaroundWaveletSnapshot deserializeWavelet(String serializedSnapshot)
      throws MessageException {
    return GsonProto.fromGson(new WalkaroundWaveletSnapshotGsonImpl(), serializedSnapshot);
  }

  @Override
  public String serializeOp(ProtocolWaveletOperation waveletOp) {
    return GsonProto.toJson((GsonSerializable) waveletOp);
  }

  @Override
  public ProtocolWaveletOperation deserializeOp(String serializedOp) throws MessageException {
    return GsonProto.fromGson(new ProtocolWaveletOperationGsonImpl(), serializedOp);
  }

  @Override
  public String serializeDiff(WaveletDiffSnapshot diff) {
    return GsonProto.toJson((GsonSerializable) diff);
  }

  @Override
  public WaveletDiffSnapshot deserializeDiff(String serializedDiff) throws MessageException {
    return GsonProto.fromGson(new WaveletDiffSnapshotGsonImpl(), serializedDiff);
  }

  @Override
  public String serializeOperationBatch(OperationBatch batch) {
    return GsonProto.toJson((OperationBatchGsonImpl) batch);
  }

  @Override
  public OperationBatch deserializeOperationBatch(String serialized) throws MessageException {
    return GsonProto.fromGson(new OperationBatchGsonImpl(), serialized);
  }

  @Override
  public String serializeDelta(Delta input) {
    return GsonProto.toJson((DeltaGsonImpl) input);
  }

  @Override
  public Delta deserializeDelta(String input) throws MessageException {
    return GsonProto.fromGson(new DeltaGsonImpl(), input);
  }

}
