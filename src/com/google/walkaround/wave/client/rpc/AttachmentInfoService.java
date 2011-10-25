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

import com.google.walkaround.slob.shared.MessageException;

import org.waveprotocol.wave.client.common.util.JsoView;
import org.waveprotocol.wave.model.util.CollectionUtils;

import java.util.List;

/**
 * Fetches attachment information.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class AttachmentInfoService {

  public interface Callback {
    void onSuccess(JsoView data);
    void onConnectionError(Throwable e);
    void onFatalError(Throwable e);
  }

  private final Rpc rpc;
  private final String serviceName = "attachmentinfo";

  public AttachmentInfoService(Rpc rpc) {
    this.rpc = rpc;
  }

  public void fetchInfo(List<String> attachmentIds, final Callback callback) {
    StringBuilder b = new StringBuilder();
    for (String id : attachmentIds) {
      if (b.length() > 0) {
        b.append(',');
      }
      if (id.contains(",")) {
        throw new IllegalArgumentException("Attachment id '" + id + "' contains a comma");
      }
      b.append(id);
    }

    rpc.makeRequest(Rpc.Method.POST, serviceName, CollectionUtils.newStringMap(
          "ids", b.toString()),
        new Rpc.RpcCallback() {
          @Override
          public void onSuccess(String data) throws MessageException {
            JsoView json = RpcUtil.evalPrefixed(data);
            callback.onSuccess(json);
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
}
