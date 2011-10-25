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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.inject.Inject;
import com.google.walkaround.wave.server.util.AbstractHandler;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles attachment uploads, then redirects to
 * {@link AttachmentUploadResultHandler}
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class AttachmentUploadHandler extends AbstractHandler {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AttachmentUploadHandler.class.getName());

  /**
   * Attachment file upload form field name.
   */
  static final String ATTACHMENT_UPLOAD_PARAM = "attachment";

  private final BlobstoreService blobstoreService;

  @Inject
  public AttachmentUploadHandler(BlobstoreService blobstoreService) {
    this.blobstoreService = blobstoreService;
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
    BlobKey blobKey = blobs.get(ATTACHMENT_UPLOAD_PARAM);

    if (blobKey != null) {
      log.info("Uploaded with blob id " + blobKey.getKeyString());
    } else {
      log.warning("Null blobKey after upload");
    }

    String idParam = blobKey != null ? "?attachmentId=" + blobKey.getKeyString() : "";
    resp.sendRedirect("/uploadresult" + idParam);
  }
}
