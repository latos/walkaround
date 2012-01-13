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
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.MemcacheTable;
import com.google.walkaround.util.server.servlet.NotFoundException;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.attachment.AttachmentMetadata.ImageMetadata;
import com.google.walkaround.wave.server.attachment.ThumbnailDirectory.ThumbnailData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Facilities for dealing with attachments. Caches various results in memcache
 * and the datastore.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class AttachmentService {
  // Don't save thumbnails larger than 50K (entity max size is 1MB).
  // They should usually be around 2-3KB each.

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AttachmentService.class.getName());

  static final int INVALID_ID_CACHE_EXPIRY_SECONDS = 600;

  private static final String MEMCACHE_TAG = "AT2";

  private final RawAttachmentService rawService;
  private final BlobstoreService blobstore;
  private final MetadataDirectory metadataDirectory;
  // We cache Optional.absent() for invalid attachment ids.
  private final MemcacheTable<AttachmentId, Optional<AttachmentMetadata>> metadataCache;
  private final ThumbnailDirectory thumbnailDirectory;
  private final int maxThumbnailSavedSizeBytes;

  @Inject
  public AttachmentService(RawAttachmentService rawService, BlobstoreService blobStore,
      CheckedDatastore datastore, MemcacheTable.Factory memcacheFactory,
      @Flag(FlagName.MAX_THUMBNAIL_SAVED_SIZE_BYTES) int maxThumbnailSavedSizeBytes) {
    this.rawService = rawService;
    this.blobstore = blobStore;
    this.metadataDirectory = new MetadataDirectory(datastore);
    this.metadataCache = memcacheFactory.create(MEMCACHE_TAG);
    this.thumbnailDirectory = new ThumbnailDirectory(datastore);
    this.maxThumbnailSavedSizeBytes = maxThumbnailSavedSizeBytes;
  }

  /**
   * Checks if the browser has the data cached.
   * @return true if it was, and there's nothing more to do in serving this request.
   */
  private boolean maybeCached(HttpServletRequest req, HttpServletResponse resp, String context) {
    // Attachments are immutable, so we don't need to check the date.
    // If the id was previously requested and yielded a 404, the browser shouldn't be
    // using this header.
    String ifModifiedSinceStr = req.getHeader("If-Modified-Since");
    if (ifModifiedSinceStr != null) {
      log.info("Telling browser to use cached attachment (" + context + ")");
      resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return true;
    }
    return false;
  }

  /**
   * Serves the attachment with cache control.
   *
   * @param req Only used to check the If-Modified-Since header.
   */
  public void serveDownload(AttachmentId id,
      HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (maybeCached(req, resp, "download, id=" + id)) {
      return;
    }
    AttachmentMetadata metadata = getMetadata(id);
    if (metadata == null) {
      throw NotFoundException.withInternalMessage("Attachment id unknown: " + id);
    }
    BlobKey key = metadata.getBlobKey();
    BlobInfo info = new BlobInfoFactory().loadBlobInfo(key);
    String disposition = "attachment; filename=\""
        // TODO(ohler): Investigate what escaping we need here, and whether the
        // blobstore service has already done some escaping that we need to undo
        // (it seems to do percent-encoding on " characters).
        + info.getFilename().replace("\"", "\\\"").replace("\\", "\\\\")
        + "\"";
    log.info("Serving " + info + " with Content-Disposition: " + disposition);
    resp.setHeader("Content-Disposition", disposition);
    blobstore.serve(key, resp);
  }

  public Void serveThumbnail(AttachmentId id,
      HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (maybeCached(req, resp, "thumbnail, id=" + id)) {
      return null;
    }
    AttachmentMetadata metadata = getMetadata(id);
    if (metadata == null) {
      throw NotFoundException.withInternalMessage("Attachment id unknown: " + id);
    }
    BlobKey key = metadata.getBlobKey();
    // TODO(danilatos): Factor out some of this code into a separate method so that
    // thumbnails can be eagerly created at upload time.
    ThumbnailData thumbnail = thumbnailDirectory.get(key);

    if (thumbnail == null) {
      log.info("Generating and storing thumbnail for " + key);

      ImageMetadata thumbDimensions = metadata.getThumbnail();

      if (thumbDimensions == null) {
        // TODO(danilatos): Provide a default thumbnail
        throw NotFoundException.withInternalMessage("No thumbnail available for attachment " + id);
      }

      byte[] thumbnailBytes = rawService.getResizedImageBytes(key,
          thumbDimensions.getWidth(), thumbDimensions.getHeight());

      thumbnail = new ThumbnailData(key, thumbnailBytes);

      if (thumbnailBytes.length > maxThumbnailSavedSizeBytes) {
        log.warning("Thumbnail for " + key + " too large to store " +
            "(" + thumbnailBytes.length + " bytes)");
        // TODO(danilatos): Cache this condition in memcache.
        throw NotFoundException.withInternalMessage("Thumbnail too large for attachment " + id);
      }

      thumbnailDirectory.getOrAdd(thumbnail);

    } else {
      log.info("Using already stored thumbnail for " + key);
    }

    // TODO(danilatos): Other headers for mime type, fileName + "Thumbnail", etc?
    resp.getOutputStream().write(thumbnail.getBytes());

    return null;
  }

  @Nullable public AttachmentMetadata getMetadata(AttachmentId id) throws IOException {
    Preconditions.checkNotNull(id, "Null id");
    Map<AttachmentId, Optional<AttachmentMetadata>> result =
        getMetadata(ImmutableList.of(id), null);
    return result.get(id).isPresent() ? result.get(id).get() : null;
  }

  /**
   * @param maxTimeMillis Maximum time to take. -1 for indefinite. If the time
   *          runs out, some data may not be returned, so the resulting map may
   *          be missing some of the input ids. Callers may retry to get the
   *          remaining data for the missing ids.
   *
   * @return a map of input id to attachment metadata for each id. invalid ids
   *         will map to Optional.absent(). Some ids may be missing due to the time limit.
   *
   *         At least one id is guaranteed to be returned.
   */
  public Map<AttachmentId, Optional<AttachmentMetadata>> getMetadata(List<AttachmentId> ids,
      @Nullable Long maxTimeMillis) throws IOException {
    Stopwatch stopwatch = new Stopwatch().start();
    Map<AttachmentId, Optional<AttachmentMetadata>> result = Maps.newHashMap();
    for (AttachmentId id : ids) {
      // TODO(danilatos): To optimise, re-arrange the code so that
      //   1. Query all the ids from memcache in one go
      //   2. Those that failed, query all remaining ids from the data store in one go
      //   3. Finally, query all remaining ids from the raw service in one go (the
      //      raw service api should be changed to accept a list, and it needs to
      //      query the __BlobInfo__ entities directly.
      Optional<AttachmentMetadata> metadata = metadataCache.get(id);
      if (metadata == null) {
        AttachmentMetadata storedMetadata = metadataDirectory.get(id);
        if (storedMetadata != null) {
          metadata = Optional.of(storedMetadata);
          metadataCache.put(id, metadata);
        } else {
          metadata = Optional.absent();
          metadataCache.put(id, metadata,
                Expiration.byDeltaSeconds(INVALID_ID_CACHE_EXPIRY_SECONDS),
                MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
        }
      }
      Assert.check(metadata != null, "Null metadata");
      result.put(id, metadata);

      if (maxTimeMillis != null && stopwatch.elapsedMillis() > maxTimeMillis) {
        break;
      }
    }
    Assert.check(!result.isEmpty(), "Should return at least one id");
    return result;
  }
}
