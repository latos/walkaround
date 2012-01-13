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
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.util.shared.RandomBase64Generator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gxp.base.GxpContext;
import com.google.inject.Inject;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.gxp.UploadResult;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

  @Inject BlobstoreService blobstoreService;
  @Inject MetadataDirectory metadataDirectory;
  @Inject RawAttachmentService rawAttachmentService;
  @Inject RandomBase64Generator random64;
  @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
    List<BlobKey> blobKeys = blobs.get(ATTACHMENT_UPLOAD_PARAM);
    log.info("blobKeys: " + blobKeys);
    BlobKey blobKey = Iterables.getOnlyElement(blobKeys);
    AttachmentId newId = new AttachmentId(random64.next(
        // 115 * 6 random bits which should be unguessable.  (6 bits per random64 char.)
        115));
    Assert.check(metadataDirectory.get(newId) == null,
        "Random attachment id already taken: %s", newId);
    log.info("Computing metadata for " + newId + " (" + blobKey + ")");
    AttachmentMetadata metadata = rawAttachmentService.computeMetadata(newId, blobKey);
    AttachmentMetadata existingMetadata = metadataDirectory.getOrAdd(metadata);
    if (existingMetadata != null) {
      // This is expected if, during getOrAdd, a commit times out from our
      // perspective but succeeded in the datatstore, and we notice the existing
      // data during a retry.  Still, we log severe until we confirm that this
      // is indeed harmless.
      log.severe("Metadata for new attachment " + metadata
          + " already exists: " + existingMetadata);
    }
    log.info("Wrote metadata " + metadata);
    UploadResult.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, newId.getId());
  }

}
