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

import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.wave.client.rpc.Rpc;
import com.google.walkaround.wave.client.rpc.Rpc.ConnectionState;
import com.google.walkaround.wave.client.rpc.Rpc.Method;
import com.google.walkaround.wave.client.rpc.Rpc.RpcCallback;

import org.waveprotocol.wave.client.scheduler.Scheduler.IncrementalTask;
import org.waveprotocol.wave.model.util.CollectionUtils;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class VersionChecker implements IncrementalTask {

  private final Rpc rpc;
  private final int thisClientVersion;

  public VersionChecker(Rpc rpc, int thisClientVersion) {
    this.rpc = rpc;
    this.thisClientVersion = thisClientVersion;
  }

  @Override
  public boolean execute() {
    rpc.makeRequest(Method.GET, "version",
        CollectionUtils.newStringMap("version", "" + thisClientVersion),
        new RpcCallback() {
          @Override
          public void onSuccess(String data) throws MessageException {
            int requiredClientVersion;
            try {
              requiredClientVersion = Integer.parseInt(data);
            } catch (NumberFormatException nfe) {
              throw new MessageException(nfe);
            }

            if (requiredClientVersion != thisClientVersion) {
              // TODO(danilatos): Automatically refresh (after a random delay),
              // if there's no unsaved data.
              rpc.maybeSetConnectionState(ConnectionState.HARD_RELOAD);
            }
          }

          @Override
          public void onConnectionError(Throwable e) {
            // do nothing
            // TODO(danilatos): Back off
          }

          @Override
          public void onFatalError(Throwable e) {
            // do nothing
            // TODO(danilatos): Back off
          }
        });

    return true;
  }
}
