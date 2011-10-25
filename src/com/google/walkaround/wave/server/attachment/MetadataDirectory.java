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
import com.google.walkaround.util.server.appengine.AbstractDirectory;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.DatastoreUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Attachment metadata
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
class MetadataDirectory extends AbstractDirectory<AttachmentMetadata, BlobKey> {
  static final String JSON_METADATA_PROPERTY = "Metadata";

  public MetadataDirectory(CheckedDatastore datastore) {
    super(datastore, "AttachmentMetadataEntity");
  }

  @Override
  protected BlobKey getId(AttachmentMetadata e) {
    return e.getId();
  }

  @Override
  protected AttachmentMetadata parse(Entity e) {
    String metadata = DatastoreUtil.getExistingProperty(e, JSON_METADATA_PROPERTY, String.class);
    try {
      return new AttachmentMetadata(
          new BlobKey(e.getKey().getName()), new JSONObject(metadata));
    } catch (JSONException je) {
      throw new DatastoreUtil.InvalidPropertyException(e, JSON_METADATA_PROPERTY,
          "Invalid json metadata: " + metadata, je);
    }
  }

  @Override
  protected void populateEntity(AttachmentMetadata in, Entity out) {
    DatastoreUtil.setNonNullUnindexedProperty(
        out, JSON_METADATA_PROPERTY, in.getMetadataJsonString());
  }

  @Override
  protected String serializeId(BlobKey id) {
    return id.getKeyString();
  }
}
