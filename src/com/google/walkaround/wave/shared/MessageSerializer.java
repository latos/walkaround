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
import com.google.walkaround.proto.OperationBatch;
import com.google.walkaround.proto.ProtocolWaveletOperation;
import com.google.walkaround.proto.WalkaroundWaveletSnapshot;
import com.google.walkaround.proto.WaveletDiffSnapshot;
import com.google.walkaround.slob.shared.MessageException;

/**
 * Interface for both server and client side serializer implementations. This
 * basic functionality implemented by ClientWaveSerializer and
 * ServerWaveSerializer allows to turn Wavelets and Deltas to and from JSON in
 * those two environments.
 *
 * @author piotrkaleta@google.com (Piotr Kaleta)
 */
public interface MessageSerializer {

  String serializeWavelet(WalkaroundWaveletSnapshot in);
  WalkaroundWaveletSnapshot deserializeWavelet(String in) throws MessageException;

  String serializeOp(ProtocolWaveletOperation in);
  ProtocolWaveletOperation deserializeOp(String in) throws MessageException;

  String serializeDiff(WaveletDiffSnapshot in);
  WaveletDiffSnapshot deserializeDiff(String in) throws MessageException;

  String serializeOperationBatch(OperationBatch in);
  OperationBatch deserializeOperationBatch(String in) throws MessageException;

  String serializeDelta(Delta in);
  Delta deserializeDelta(String in) throws MessageException;

}
