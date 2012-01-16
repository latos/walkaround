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

import com.google.common.base.Preconditions;
import com.google.walkaround.proto.ConnectResponse;
import com.google.walkaround.proto.WalkaroundWaveletSnapshot;
import com.google.walkaround.proto.WaveletDiffSnapshot;
import com.google.walkaround.proto.jso.ConnectResponseJsoImpl;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;
import com.google.walkaround.wave.client.ClientMessageSerializer;
import com.google.walkaround.wave.client.rpc.WaveletMap.WaveletEntry;
import com.google.walkaround.wave.shared.IdHack;
import com.google.walkaround.wave.shared.SharedConstants.Params;
import com.google.walkaround.wave.shared.SharedConstants.Services;
import com.google.walkaround.wave.shared.WaveSerializer;

import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.StringMap;
import org.waveprotocol.wave.model.wave.data.DocumentFactory;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;

/**
 * Service that checks current version of wave.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(ohler): rename to something like WaveletConnectService
public class LoadWaveService {
  private static final Log log = Logs.create("loadwave");

  public interface LoadWaveCallback {
    void onData(WaveletEntry data);
    void onConnectionError(Throwable e);
  }

  public interface ConnectCallback {
    void onData(ConnectResponse data);
    void onConnectionError(Throwable e);
  }

  private final Rpc rpc;

  public LoadWaveService(Rpc rpc) {
    this.rpc = rpc;
  }

  // TODO(ohler): rename to connect?
  public void fetchWaveRevision(String signedSessionString,
      final ConnectCallback callback) {
    rpc.makeRequest(Rpc.Method.GET, Services.CONNECT,
        CollectionUtils.newStringMap(Params.SESSION, signedSessionString),
        new Rpc.RpcCallback() {
          @Override
          public void onSuccess(String data) throws MessageException {
            log.log(Level.DEBUG, data);
            ConnectResponseJsoImpl connectResponse = RpcUtil.evalPrefixed(data).cast();
            callback.onData(connectResponse);
          }

          @Override
          public void onFatalError(Throwable e) {
            throw new RuntimeException(e);
          }

          @Override
          public void onConnectionError(Throwable e) {
            callback.onConnectionError(e);
          }
        });
  }
}
