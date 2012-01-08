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

package com.google.walkaround.wave.server.rpc;

import com.google.inject.Inject;
import com.google.walkaround.slob.server.AccessDeniedException;
import com.google.walkaround.slob.server.SlobNotFoundException;
import com.google.walkaround.slob.server.SlobStore;
import com.google.walkaround.slob.server.SlobStore.ConnectResult;
import com.google.walkaround.slob.server.SlobStore.HistoryResult;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.ObjectSession;
import com.google.walkaround.wave.server.ObjectStoreSelector;
import com.google.walkaround.wave.server.servlet.ServletUtil;
import com.google.walkaround.wave.shared.SharedConstants.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used by the client to notify the server of connection to an object at a
 * specific revision, or to refresh a channel.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ChannelHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ChannelHandler.class.getName());

  @Inject ObjectStoreSelector storeSelector;
  @Inject ObjectSession session;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      inner(req, resp);
    } catch (JSONException e) {
      throw new Error(e);
    }
  }

  private void inner(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, JSONException {
    JSONObject result = new JSONObject();
    SlobStore store = storeSelector.get(session.getStoreType());
    try {
      int revision = Integer.parseInt(requireParameter(req, Params.REVISION));
      ConnectResult r = store.reconnect(session.getObjectId(), session.getClientId());
      if (r.getChannelToken() != null) {
        result.put("token", r.getChannelToken());
        HistoryResult history = store.loadHistory(session.getObjectId(), revision, null);
        result.put("history", HistoryHandler.serializeHistory(revision, history.getData()));
        result.put("head", r.getVersion());
      } else {
        // TODO(ohler): Figure out and document how the client-server protocol
        // works and what the different endpoints do.  It's not clear to me why
        // the above case returns history and head version (even though the
        // javadoc doesn't mention this) and this case doesn't (even though it
        // could).
        result.put("error", "Too many concurrent connections");
      }
    } catch (SlobNotFoundException e) {
      throw new BadRequestException("Object not found or access denied", e);
    } catch (AccessDeniedException e) {
      throw new BadRequestException("Object not found or access denied", e);
    } catch (NumberFormatException nfe) {
      throw new BadRequestException("Parse error", nfe);
    }

    resp.setContentType("application/json");
    ServletUtil.writeJsonResult(resp.getWriter(), result.toString());
  }

}
