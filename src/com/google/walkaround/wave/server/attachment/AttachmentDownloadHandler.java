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

import com.google.inject.Inject;
import com.google.walkaround.util.server.servlet.AbstractHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Downloads and thumbnails attachments
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class AttachmentDownloadHandler extends AbstractHandler {
  private final AttachmentService attachments;

  @Inject
  public AttachmentDownloadHandler(AttachmentService attachments) {
    this.attachments = attachments;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = requireParameter(req, "attachment");
    if (req.getRequestURI().startsWith("/thumbnail")) {
      attachments.serveThumbnail(id, req, resp);
    } else {
      attachments.serveDownload(id, req, resp);
    }
  }
}
