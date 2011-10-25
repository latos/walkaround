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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.walkaround.slob.client.ChangeDataParser;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;
import com.google.walkaround.wave.shared.SharedConstants.Params;
import com.google.walkaround.wave.shared.SharedConstants.Services;

import org.waveprotocol.wave.client.common.util.JsoView;
import org.waveprotocol.wave.model.util.CollectionUtils;

/**
 * Handles getting a channel token for connecting a browser channel, and
 * fetching missing messages.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ChannelConnectService {
  public interface Callback {
    void onConnect(String channelToken);
    void onKnownHeadRevision(int headRevision);
    void onHistoryItem(int resultingRevision, ChangeData<JavaScriptObject> message);

    void onConnectionError(Throwable e);
    void onFatalError(Throwable e);
  }

  private final Log log = Logs.create("channel-connect");
  private final Rpc rpc;

  public ChannelConnectService(Rpc rpc) {
    this.rpc = rpc;
  }

  public void connect(String signedSessionString,
      final int revision, final Callback callback) {
    rpc.makeRequest(Rpc.Method.POST, Services.CHANNEL,
        CollectionUtils.newStringMap(
            Params.SESSION, signedSessionString,
            Params.REVISION, revision + ""),
        new Rpc.RpcCallback() {
          @Override
          public void onSuccess(String data) throws MessageException {
            JsoView json = RpcUtil.evalPrefixed(data);
            if (json.containsKey("error")) {
              onConnectionError(new Exception(json.getString("error")));
            } else {
              callback.onConnect(json.getString("token"));
              JsoView history = json.getJsoView("history");
              if (history.getNumber("length") > 0) {
                sendHistoryItems(history, callback, revision);
              }
              // The head revision might be greater than expected if some
              // history items were missed, so let's give the listener
              // as much information as possible.
              callback.onKnownHeadRevision((int) json.getNumber("head"));
            }
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

  /**
   * This has the same method signature as {@link #connect}, but does not try to
   * reconnect. Because of implementation details, the server side
   * implementation of connect is more expensive than just a history fetch (as
   * it consumes a session id, which is a finite resource), so we're keeping
   * them separate for now. But they could potentially be merged.
   *
   * TODO(ohler): We no longer consume a session id, update the above comment.
   */
  public void fetchHistory(final String signedSessionString,
      final int startRevision, final Callback callback) {
    rpc.makeRequest(Rpc.Method.POST, Services.HISTORY,
        CollectionUtils.newStringMap(
            Params.SESSION, signedSessionString,
            Params.START_REVISION, startRevision + ""),
        new Rpc.RpcCallback() {
          @Override
          public void onSuccess(String data) throws MessageException {
            JsoView json = RpcUtil.evalPrefixed(data).getJsoView("history");

            int len = sendHistoryItems(json, callback, startRevision);
            boolean hasMore = json.getBoolean("more");

            // In case the result is batched, we'll keep fetching.
            if (hasMore) {
              // TODO(danilatos): Move this logic into GaeReceiveOpChannel,
              // keep this class dumb.
              log.log(Level.INFO,
                  "fetch history returned incomplete result, retrying for the rest");
              fetchHistory(signedSessionString, startRevision + len, callback);
            }
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

  private static int sendHistoryItems(JsoView json, Callback callback, int startRevision) {
    int len = (int) json.getNumber("length");
    if (len == 0) {
      callback.onFatalError(new Exception("history fetch returned empty results"));
      return 0;
    }

    for (int i = 0; i < len; i++) {
      JsoView item = json.getJsoView(i);
      callback.onHistoryItem(startRevision + i + 1,  // + 1 for resulting revision
          ChangeDataParser.fromJson(item));
    }

    return len;
  }
}
