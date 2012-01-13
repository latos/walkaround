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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.inject.Inject;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Low level attachment facilities that always operate on the raw attachment
 * data.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class RawAttachmentService {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(RawAttachmentService.class.getName());

  private static final int MAX_THUMB_HEIGHT_PX = 120;
  private static final int MAX_THUMB_WIDTH_PX = 120;

  private final BlobstoreService blobstoreService;
  private final BlobInfoFactory blobInfoFactory;
  private final ImagesService imagesService;
  private final int headerBytesUpperBound;

  @Inject
  public RawAttachmentService(BlobstoreService blobstoreService, ImagesService imagesService,
      BlobInfoFactory blobInfoFactory,
      @Flag(FlagName.ATTACHMENT_HEADER_BYTES_UPPER_BOUND) int headerBytesUpperBound) {
    this.blobstoreService = blobstoreService;
    this.imagesService = imagesService;
    this.blobInfoFactory = blobInfoFactory;
    this.headerBytesUpperBound = headerBytesUpperBound;
  }

  public AttachmentMetadata computeMetadata(AttachmentId id, BlobKey blobKey) {
    log.info("Computing attachment metadata for " + blobKey + " with id " + id);
    try {
      BlobInfo info = blobInfoFactory.loadBlobInfo(blobKey);

      if (info != null) {
        JSONObject data = new JSONObject();
        data.put("size", info.getSize());
        String mimeType = info.getContentType();
        data.put("mimeType", mimeType);
        data.put("filename", info.getFilename());

        if (mimeType.startsWith("image/")) {
          Image img = attemptGetImageMetadata(blobstoreService, info);

          if (img != null) {
            JSONObject imgData = new JSONObject();
            imgData.put("width", img.getWidth());
            imgData.put("height", img.getHeight());
            data.put("image", imgData);

            JSONObject thumbData = new JSONObject();
            double ratio = resizeRatio(img);
            thumbData.put("width", ratio * img.getWidth());
            thumbData.put("height", ratio * img.getHeight());
            data.put("thumbnail", thumbData);
          }
        } else {
          // TODO(danilatos): Thumbnails for non-images
          log.warning("Unimplemented: Thumbnails for non-images");
        }

        return new AttachmentMetadata(id, blobKey, data);
      }

      return null;
    } catch (JSONException e) {
      throw new Error(e);
    }
  }

  public byte[] getResizedImageBytes(BlobKey id, int width, int height) {
    log.info("Resizing " + id + " to " + width + "x" + height);
    // emptyImage is just a wrapper around the id.
    Image emptyImage = ImagesServiceFactory.makeImageFromBlob(id);
    Transform resize = ImagesServiceFactory.makeResize(width, height);
    return imagesService.applyTransform(resize, emptyImage).getImageData();
  }

  @Nullable private Image attemptGetImageMetadata(BlobstoreService blobstore, BlobInfo info) {
    final int readPortion = headerBytesUpperBound;
    BlobKey key = info.getBlobKey();
    byte[] data = blobstore.fetchData(key, 0, readPortion);
    try {
      Image img = ImagesServiceFactory.makeImage(data);
      // Force the image to be processed
      img.getWidth();
      img.getHeight();
      return img;
    } catch (RuntimeException e) {
      log.log(Level.SEVERE, "While getting image metadata", e);
      return null;
    }
  }

  private double resizeRatio(Image img) {
    double width = img.getWidth();
    double height = img.getHeight();
    double cmpRatio = (double) MAX_THUMB_WIDTH_PX / MAX_THUMB_HEIGHT_PX;
    double ratio = width / height;
    if (ratio > cmpRatio) {
      return Math.min(1, MAX_THUMB_WIDTH_PX / width);
    } else {
      return Math.min(1, MAX_THUMB_HEIGHT_PX / height);
    }
  }
}
