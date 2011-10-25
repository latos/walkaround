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
import com.google.common.base.Preconditions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Metadata associated with an attachment. Should not be changed for a given
 * attachment, so that it can be cached indefinitely.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(danilatos) Merge this with WalkaroundAttachment, once we have a json interface.
// Same goes for "ImageMetadata"
// TODO(danilatos) Consider using a proto-based implementation?
public class AttachmentMetadata implements Serializable {
  public interface ImageMetadata {
    public int getWidth();
    public int getHeight();
  }

  private final BlobKey id;
  // String for Serializable
  private final String rawMetadata;
  // Might be null despite Preconditions checks, thanks to Serializable.
  private transient JSONObject maybeMetadata;
  // We don't want to persist this to the data store, it's just for memcache.
  private boolean isValid = true;

  /**
   * @return "Invalid" metadata. Useful as a marker.
   */
  public static AttachmentMetadata createInvalid(BlobKey id) {
    try {
      AttachmentMetadata m = new AttachmentMetadata(id, new JSONObject("{}"));
      m.isValid = false;
      return m;
    } catch (JSONException e) {
      throw new Error(e);
    }
  }

  public AttachmentMetadata(BlobKey id, JSONObject metadata) {
    this.id = Preconditions.checkNotNull(id, "null id");
    this.maybeMetadata = Preconditions.checkNotNull(metadata, "null metadata");
    this.rawMetadata = metadata.toString();
  }

  public BlobKey getId() {
    return id;
  }

  private JSONObject getMetadata() {
    if (maybeMetadata == null) {
      assert rawMetadata != null;
      try {
        maybeMetadata = new JSONObject(rawMetadata);
      } catch (JSONException e) {
        throw new RuntimeException("Bad JSON: " + rawMetadata, e);
      }
    }
    return maybeMetadata;
  }

  public String getMetadataJsonString() {
    return rawMetadata;
  }

  public boolean isValid() {
    return isValid;
  }

  public ImageMetadata getImage() {
    return createImageMetadata("image");
  }

  public ImageMetadata getThumbnail() {
    return createImageMetadata("thumbnail");
  }

  private ImageMetadata createImageMetadata(String key) {
    final JSONObject imgData;
    try {
      imgData = getMetadata().has(key) ? getMetadata().getJSONObject(key) : null;
    } catch (JSONException e) {
      throw new Error(e);
    }

    if (imgData == null) {
      return null;
    }

    return new ImageMetadata() {
      @Override public int getWidth() {
        try {
          return imgData.getInt("width");
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
      }
      @Override public int getHeight() {
        try {
          return imgData.getInt("height");
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  @Override
  public String toString() {
    return "AttachmentMetadata" + (isValid ? rawMetadata : "(invalid)");
  }
}
