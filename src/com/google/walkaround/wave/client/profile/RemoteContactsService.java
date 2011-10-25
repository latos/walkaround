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

package com.google.walkaround.wave.client.profile;

import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;
import com.google.walkaround.wave.client.rpc.Rpc;
import com.google.walkaround.wave.client.rpc.Rpc.Method;
import com.google.walkaround.wave.client.rpc.RpcUtil;
import com.google.walkaround.wave.shared.ContactsService;

import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.StringMap;

/**
 * Provides a contacts service using AJAX rpcs.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class RemoteContactsService implements ContactsService {
  private static final Log LOG = Logs.create("contacts");
  private static final String SERVICE_NAME = "contacts";

  /** RPC worker. */
  private final Rpc rpc;

  public RemoteContactsService(Rpc rpc) {
    this.rpc = rpc;
  }

  @Override
  public void fetch(int from, int to, final Callback callback) {
    StringMap<String> params = CollectionUtils.createStringMap();
    params.put("from", Integer.toString(from));
    params.put("to", Integer.toString(to));
    rpc.makeRequest(Method.GET, SERVICE_NAME, params, new Rpc.RpcCallback() {
      @Override
      public void onSuccess(String data) throws MessageException {
        callback.onSuccess(RpcUtil.evalPrefixed(data).<ContactListJsoImpl>cast());
      }

      @Override
      public void onFatalError(Throwable e) {
        LOG.log(Level.WARNING, "Contact fetch failed", e);
        callback.onFailure();
      }

      @Override
      public void onConnectionError(Throwable e) {
        LOG.log(Level.WARNING, "Contact fetch failed", e);
        callback.onFailure();
      }
    });
  }
}
