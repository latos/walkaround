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
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.MemcacheTable;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.attachment.AttachmentMetadata.ImageMetadata;
import com.google.walkaround.wave.server.attachment.ThumbnailDirectory.ThumbnailData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

  private static final String MEMCACHE_TAG = "AT";

  private final RawAttachmentService rawService;
  private final BlobstoreService blobstore;
  private final MetadataDirectory metadataDirectory;
  private final MemcacheTable<BlobKey, AttachmentMetadata> metadataCache;
  private final ThumbnailDirectory thumbnailDirectory;
  private final int maxThumbnailSavedSizeBytes;

  @Inject
  public AttachmentService(RawAttachmentService rawService, BlobstoreService blobStore,
      CheckedDatastore datastore, MemcacheService memcache,
      @Flag(FlagName.MAX_THUMBNAIL_SAVED_SIZE_BYTES) int maxThumbnailSavedSizeBytes) {
    this.rawService = rawService;
    this.blobstore = blobStore;
    this.metadataDirectory = new MetadataDirectory(datastore);
    this.metadataCache = new MemcacheTable<BlobKey, AttachmentMetadata>(memcache, MEMCACHE_TAG);
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
   * @throws IOException
   */
  public void serveDownload(String id,
      HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (maybeCached(req, resp, "download, id=" + id)) {
      return;
    }

    BlobKey key = new BlobKey(id);

    // TODO(danilatos): Verify that the blobstoreService fills in all the appropriate
    // headers including file name.
    blobstore.serve(key, resp);
  }

  public Void serveThumbnail(String id,
      HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (maybeCached(req, resp, "thumbnail, id=" + id)) {
      return null;
    }

    BlobKey key = new BlobKey(id);

    // TODO(danilatos): Factor out some of this code into a separate method so that
    // thumbnails can be eagerly created at upload time.
    ThumbnailData thumbnail = thumbnailDirectory.get(key);

    if (thumbnail == null) {
      log.info("Generating and storing thumbnail for " + key);

      AttachmentMetadata metadata;
      metadata = getMetadata(Arrays.asList(id), -1).get(id);
      assert metadata != null;

      if (!metadata.isValid()) {
        return send404(resp, id, "Attachment not found");
      }

      ImageMetadata thumbDimensions = metadata.getThumbnail();

      if (thumbDimensions == null) {
        // TODO(danilatos): Provide a default thumbnail
        return send404(resp, id, "No thumbnail available");
      }

      byte[] thumbnailBytes = rawService.getResizedImageBytes(key,
          thumbDimensions.getWidth(), thumbDimensions.getHeight());

      thumbnail = new ThumbnailData(key, thumbnailBytes);

      if (thumbnailBytes.length > maxThumbnailSavedSizeBytes) {
        log.warning("Thumbnail for " + key + " too large to store " +
            "(" + thumbnailBytes.length + " bytes)");
        // TODO(danilatos): Cache this condition in memcache.
        return send404(resp, id, "Thumbnail too large");
      }

      thumbnailDirectory.getOrAdd(thumbnail);

    } else {
      log.info("Using already stored thumbnail for " + key);
    }

    // TODO(danilatos): Other headers for mime type, fileName + "Thumbnail", etc?
    resp.getOutputStream().write(thumbnail.getBytes());

    return null;
  }

  private Void send404(HttpServletResponse resp, String id, String message) throws IOException {
    log.info("Serving 404 for id " + id + ": " + message);
    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    resp.getWriter().println(message);
    return null;
  }

  /**
   * @param ids attachment ids
   * @param maxTimeMillis Maximum time to take. -1 for indefinite. If the time
   *          runs out, some data may not be returned, so the resulting map may
   *          be missing some of the input ids. Callers may retry to get the
   *          remaining data for the missing ids.
   *
   * @return a map of input id to attachment metadata for each id. invalid ids
   *         will still map to a metadata object whose
   *         {@link AttachmentMetadata#isValid()} method returns false. Some ids
   *         may be missing due to the time limit.
   *
   *         At least one id is guaranteed to be returned.
   *
   * @throws IOException
   */
  public Map<String, AttachmentMetadata> getMetadata(List<String> ids, int maxTimeMillis)
      throws IOException {

    Stopwatch stopwatch = new Stopwatch().start();
    Map<String, AttachmentMetadata> result = Maps.newHashMap();

    for (String id : ids) {
      // TODO(danilatos): To optimise, re-arrange the code so that
      //   1. Query all the ids from memcache in one go
      //   2. Those that failed, query all remaining ids from the data store in one go
      //   3. Finally, query all remaining ids from the raw service in one go (the
      //      raw service api should be changed to accept a list, and it needs to
      //      query the __BlobInfo__ entities directly.

      final BlobKey key = new BlobKey(id);

      // First, try memcache
      AttachmentMetadata metadata = metadataCache.get(key);

      // Next, try the datastore, and save back to memcache if successfull
      if (metadata == null) {
        metadata = metadataDirectory.get(key);

        if (metadata != null) {
          metadataCache.put(key, metadata);

        } else {
          // Finally, if all else fails, use the raw data.
          metadata = rawService.getMetadata(key);

          if (metadata == null) {
            // This should not normally happen.
            // Let's cache failure for now for a few minutes, but we never
            // want to record this in the datastore.
            metadata = AttachmentMetadata.createInvalid(key);
            metadataCache.put(key, metadata,
                Expiration.byDeltaSeconds(INVALID_ID_CACHE_EXPIRY_SECONDS),
                MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
          } else {
            metadataCache.put(key, metadata);
            metadataDirectory.getOrAdd(metadata);
          }
        }
      }

      assert metadata != null : "Even invalid metadata should not result in null";
      result.put(id, metadata);

      if (maxTimeMillis != -1 && stopwatch.elapsedMillis() > maxTimeMillis) {
        break;
      }
    }

    assert !result.isEmpty() : "Should return at least one id";

    return result;
  }
}
