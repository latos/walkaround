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

package com.google.walkaround.wave.client.rpc;

import com.google.walkaround.slob.client.GenericOperationChannel.SendOpService;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.wave.client.ClientMessageSerializer;
import com.google.walkaround.wave.shared.SharedConstants.Params;
import com.google.walkaround.wave.shared.SharedConstants.Services;
import com.google.walkaround.wave.shared.WaveSerializer;

import org.waveprotocol.wave.client.common.util.JsoView;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.util.CollectionUtils;

import java.util.List;

/**
 * Low-level service that submits a delta to a wave. Does not handle being
 * called while another delta is still in flight, that is a job for the channel
 * layer above.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public abstract class SubmitDeltaService implements SendOpService<WaveletOperation> {

  private final Rpc rpc;
  private final WaveletMap wavelets;
  private final SlobId objectId;
  private final WaveSerializer serializer;

  public SubmitDeltaService(Rpc rpc, WaveletMap wavelets, SlobId objectId) {
    this.rpc = rpc;
    this.wavelets = wavelets;
    this.objectId = objectId;
    this.serializer = new WaveSerializer(new ClientMessageSerializer());
  }

  @Override
  public void submitOperations(int revision, List<WaveletOperation> operations,
      final Callback callback) {
    rpc.makeRequest(Rpc.Method.POST, Services.SUBMIT_DELTA,
        CollectionUtils.newStringMap(
            Params.SESSION, wavelets.get(objectId).getSignedSessionString(),
            "v", revision + "",
            "operations", serializer.serializeOperationBatch(operations)),
        new Rpc.RpcCallback() {
          @Override
          public void onSuccess(String data) throws MessageException {
            JsoView json = RpcUtil.evalPrefixed(data);
            callback.onSuccess((int) json.getNumber("version"));
          }

          @Override
          public void onConnectionError(Throwable e) {
            callback.onConnectionError(e);
          }

          @Override
          public void onFatalError(Throwable e) {
            callback.onFatalError(e);
          }
        });
  }

  @Override
  public void callbackNotNeeded(SendOpService.Callback callback) {
    // nothing
  }

}
