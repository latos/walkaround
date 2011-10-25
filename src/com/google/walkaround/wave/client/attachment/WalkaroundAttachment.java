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

package com.google.walkaround.wave.client.attachment;

import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.wave.client.JsUtil;

import org.waveprotocol.wave.client.common.util.JsoView;
import org.waveprotocol.wave.client.doodad.attachment.SimpleAttachmentManager.Attachment;
import org.waveprotocol.wave.client.doodad.attachment.SimpleAttachmentManager.UploadStatusCode;

/**
 * Attachment object.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
class WalkaroundAttachment implements Attachment {
  private final String id;
  private UploadStatusCode status = UploadStatusCode.NOT_UPLOADING;
  private JsoView data;
  private ImageMetadata img = null;
  private ImageMetadata thumbnail = null;

  public WalkaroundAttachment(String id) {
    this.id = id;
    try {
      setData(JsUtil.eval("{thumbnail:{width:120,height:90}}"));
    } catch (MessageException e) {
      throw new RuntimeException(e);
    }
  }

  public void setStatus(UploadStatusCode status) {
    this.status = status;
  }

  public void setData(JsoView newData) {
    data = newData;
    img = null;
    thumbnail = null;

    if (data.containsKey("image")) {
      img = createImageMetadata(data.getJso("image").<JsoView>cast());
    }
    if (data.containsKey("thumbnail")) {
      thumbnail = createImageMetadata(data.getJso("thumbnail").<JsoView>cast());
    }
  }

  private ImageMetadata createImageMetadata(final JsoView imgData) {
    return new ImageMetadata() {
      @Override public int getWidth() {
        return (int) imgData.getNumber("width");
      }
      @Override public int getHeight() {
        return (int) imgData.getNumber("height");
      }
    };
  }

  @Override
  public UploadStatusCode getUploadStatusCode() {
    return status;
  }

  @Override
  public String getAttachmentUrl() {
    return data.getString("url");
  }

  @Override
  public String getCreator() {
    return data.getString("creator");
  }

  @Override
  public String getFilename() {
    return data.getString("filename");
  }

  @Override
  public String getMimeType() {
    return data.getString("mimeType");
  }

  @Override
  public Long getSize() {
    // TODO(danilatos) Fix the waveprotocol interface to be long not Long.
    return data.containsKey("size") ? (long) data.getNumber("size") : null;
  }

  @Override
  public boolean isMalware() {
    return data.containsKey("isMalware") ? data.getBoolean("isMalware") : false;
  }

  @Override
  public String getAttachmentId() {
    return id;
  }

  @Override
  public ImageMetadata getContentImageMetadata() {
    return img;
  }

  @Override
  public ImageMetadata getThumbnailImageMetadata() {
    return thumbnail;
  }

  @Override
  public String getThumbnailUrl() {
    return data.getString("thumbnailUrl");
  }

  ////
  // hacks

  @Override
  public double getUploadStatusProgress() {
    return 0.2;
  }

  @Override
  public long getUploadedByteCount() {
    return 0;
  }

  ////

  @Override
  public String getStatus() {
    throw new AssertionError("Not implemented");
  }

  @Override
  public long getUploadRetryCount() {
    throw new AssertionError("Not implemented");
  }

  @Override
  public Thumbnail getThumbnail() {
    throw new AssertionError("Not implemented");
  }

  @Override
  public Image getImage() {
    throw new AssertionError("Not implemented");
  }
}
