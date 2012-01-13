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
import com.google.appengine.api.datastore.Entity;
import com.google.inject.Inject;
import com.google.walkaround.util.server.appengine.AbstractDirectory;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.DatastoreUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 * Attachment metadata
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
class MetadataDirectory extends AbstractDirectory<AttachmentMetadata, AttachmentId> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(MetadataDirectory.class.getName());

  private static final String BLOB_KEY_PROPERTY = "BlobKey";
  private static final String JSON_METADATA_PROPERTY = "Metadata";

  @Inject public MetadataDirectory(CheckedDatastore datastore) {
    super(datastore, "AttachmentMetadataEntity");
  }

  @Override
  protected AttachmentId getId(AttachmentMetadata e) {
    return e.getId();
  }

  @Override
  protected AttachmentMetadata parse(Entity e) {
    AttachmentId id = new AttachmentId(e.getKey().getName());
    String blobKey = DatastoreUtil.getOptionalProperty(e, BLOB_KEY_PROPERTY, String.class);
    if (blobKey == null) {
      // For some legacy data, the attachment id is the blob key.
      log.info("Legacy attachment without blob key: " + id);
      blobKey = id.getId();
    }
    String metadata = DatastoreUtil.getExistingProperty(e, JSON_METADATA_PROPERTY, String.class);
    try {
      return new AttachmentMetadata(id, new BlobKey(blobKey), new JSONObject(metadata));
    } catch (JSONException je) {
      throw new DatastoreUtil.InvalidPropertyException(e, JSON_METADATA_PROPERTY,
          "Invalid json metadata: " + metadata, je);
    }
  }

  @Override
  protected void populateEntity(AttachmentMetadata in, Entity out) {
    DatastoreUtil.setNonNullIndexedProperty(
        out, BLOB_KEY_PROPERTY, in.getBlobKey().getKeyString());
    DatastoreUtil.setNonNullUnindexedProperty(
        out, JSON_METADATA_PROPERTY, in.getMetadataJsonString());
  }

  @Override
  protected String serializeId(AttachmentId id) {
    return id.getId();
  }

}
