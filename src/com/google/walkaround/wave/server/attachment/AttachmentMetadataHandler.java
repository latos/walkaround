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

package com.google.walkaround.wave.server.attachment;

import com.google.common.net.UriEscapers;
import com.google.inject.Inject;
import com.google.walkaround.wave.server.servlet.ServletUtil;
import com.google.walkaround.wave.server.util.AbstractHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Serves attachment metadata as JSON.
 *
 * We support POST because the client could legitimately ask for a very large
 * number of attachment ids at once, which may not fit into a GET request.
 *
 * At the same time, we should support GET because it is appropriate for small
 * requests and convenient for debugging.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class AttachmentMetadataHandler extends AbstractHandler {
  /** Avoid doing too much work at once so the client can get incremental being loaded */
  private static final int MAX_REQUEST_TIME_MS = 4 * 1000;


  private final AttachmentService attachments;

  @Inject
  public AttachmentMetadataHandler(AttachmentService attachments) {
    this.attachments = attachments;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doRequest(req, resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doRequest(req, resp);
  }

  private void doRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Map<String, AttachmentMetadata> result = attachments.getMetadata(
        Arrays.asList(requireParameter(req, "ids").split(",")), MAX_REQUEST_TIME_MS);

    JSONObject json = new JSONObject();
    try {
      for (Map.Entry<String, AttachmentMetadata> entry : result.entrySet()) {
        JSONObject metadata = new JSONObject(entry.getValue().getMetadataJsonString());
        String queryParams = "attachment=" +
            UriEscapers.uriQueryStringEscaper(false).escape(entry.getKey());
        metadata.put("url", "/download?" + queryParams);
        metadata.put("thumbnailUrl", "/thumbnail?" + queryParams);
        json.put(entry.getKey(), metadata);
      }
    } catch (JSONException e) {
      throw new Error(e);
    }
    ServletUtil.writeJsonResult(resp.getWriter(), json.toString());
  }
}
