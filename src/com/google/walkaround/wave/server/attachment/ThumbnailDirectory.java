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
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.walkaround.util.server.appengine.AbstractDirectory;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.DatastoreUtil;
import com.google.walkaround.wave.server.attachment.ThumbnailDirectory.ThumbnailData;

/**
 * Image thumbnails. They are small enough to be stored directly in the data
 * store rather than in the blob store (by a very large margin, 2-3KB thumbnails
 * vs 1MB entities).
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
class ThumbnailDirectory extends AbstractDirectory<ThumbnailData, BlobKey> {

  static class ThumbnailData {
    final BlobKey id;
    final byte[] thumbnailBytes;

    ThumbnailData(BlobKey id, byte[] thumbnailData) {
      this.id = id;
      this.thumbnailBytes = thumbnailData;
    }

    byte[] getBytes() {
      return thumbnailBytes;
    }
  }

  static final String THUMBNAIL_BYTES_PROPERTY = "ThumbnailBytes";

  public ThumbnailDirectory(CheckedDatastore datastore) {
    super(datastore, "ThumbnailDataEntity");
  }

  @Override
  protected BlobKey getId(ThumbnailData e) {
    return e.id;
  }

  @Override
  protected ThumbnailData parse(Entity e) {
    return new ThumbnailData(new BlobKey(e.getKey().getName()),
        DatastoreUtil.getExistingProperty(e, THUMBNAIL_BYTES_PROPERTY, Blob.class).getBytes());
  }

  @Override
  protected void populateEntity(ThumbnailData in, Entity out) {
    DatastoreUtil.setNonNullUnindexedProperty(
        out, THUMBNAIL_BYTES_PROPERTY, new Blob(in.thumbnailBytes));
  }

  @Override
  protected String serializeId(BlobKey id) {
    return id.getKeyString();
  }
}
