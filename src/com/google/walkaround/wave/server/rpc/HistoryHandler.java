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
import com.google.walkaround.slob.server.ChangeDataSerializer;
import com.google.walkaround.slob.server.SlobStoreSelector;
import com.google.walkaround.slob.server.SlobNotFoundException;
import com.google.walkaround.slob.server.SlobStore;
import com.google.walkaround.slob.server.SlobStore.HistoryResult;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.ObjectSession;
import com.google.walkaround.wave.server.servlet.ServletUtil;
import com.google.walkaround.wave.shared.SharedConstants.Params;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class HistoryHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(HistoryHandler.class.getName());

  @Inject SlobStoreSelector storeSelector;
  @Inject ObjectSession session;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      inner(req, resp);
    } catch (JSONException e) {
      throw new Error(e);
    } catch (SlobNotFoundException e) {
      throw new BadRequestException("Object not found or access denied", e);
    } catch (AccessDeniedException e) {
      throw new BadRequestException("Object not found or access denied", e);
    }
  }

  private void inner(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, JSONException, SlobNotFoundException, AccessDeniedException {
    long startVersion;
    @Nullable Long endVersion;
    try {
      startVersion = Long.parseLong(requireParameter(req, Params.START_REVISION));
      String endVersionString = req.getParameter(Params.END_REVISION);
      if (endVersionString == null) {
        endVersion = null;
      } else {
        endVersion = Long.parseLong(endVersionString);
      }
    } catch (NumberFormatException nfe) {
      throw new BadRequestException(nfe);
    }

    SlobId objectId = session.getObjectId();
    SlobStore store = storeSelector.get(session.getStoreType()).getSlobStore();
    JSONObject result = new JSONObject();
    HistoryResult history = store.loadHistory(objectId, startVersion, endVersion);
    result.put("history", serializeHistory(startVersion, history.getData()));
    result.put("more", history.hasMore());

    resp.setContentType("application/json");
    ServletUtil.writeJsonResult(resp.getWriter(), result.toString());
  }

  static JSONArray serializeHistory(long startVersion, List<ChangeData<String>> entries)
      throws JSONException {
    JSONArray history = new JSONArray();
    int index = 0;
    for (ChangeData<String> data : entries) {
      history.put(index, ChangeDataSerializer.dataToClientJson(data, startVersion + index + 1));
      index++;
    }
    return history;
  }
}
