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

package com.google.walkaround.wave.server.googleimport;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.walkaround.proto.FetchAttachmentsAndImportWaveletTask;
import com.google.walkaround.proto.ImportTaskPayload;
import com.google.walkaround.proto.ImportWaveletTask;
import com.google.walkaround.proto.FetchAttachmentsAndImportWaveletTask.ImportedAttachmentInfo;
import com.google.walkaround.proto.FetchAttachmentsAndImportWaveletTask.RemoteAttachmentInfo;
import com.google.walkaround.proto.ImportWaveletTask.ImportedAttachment;
import com.google.walkaround.proto.gson.ImportTaskPayloadGsonImpl;
import com.google.walkaround.proto.gson.FetchAttachmentsAndImportWaveletTaskGsonImpl.ImportedAttachmentInfoGsonImpl;
import com.google.walkaround.proto.gson.ImportWaveletTaskGsonImpl.ImportedAttachmentGsonImpl;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.attachment.AttachmentId;
import com.google.walkaround.wave.server.attachment.RawAttachmentService;
import com.google.walkaround.wave.server.gxp.SourceInstance;

import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;

import javax.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Processes {@link FetchAttachmentsAndImportWaveletTask}s.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class FetchAttachmentsProcessor {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(FetchAttachmentsProcessor.class.getName());

  private final SourceInstance.Factory sourceInstanceFactory;
  private final RawAttachmentService rawAttachmentService;
  private final URLFetchService urlfetch;

  @Inject
  public FetchAttachmentsProcessor(SourceInstance.Factory sourceInstanceFactory,
      RawAttachmentService rawAttachmentService,
      URLFetchService urlfetch) {
    this.sourceInstanceFactory = sourceInstanceFactory;
    this.rawAttachmentService = rawAttachmentService;
    this.urlfetch = urlfetch;
  }

  private static final String DEFAULT_FILE_NAME = "attachment";
  private static final int MAX_FETCHES_PER_TASK = 5;

  private static final int MAX_FILE_BYTES_TRANSFERRED_PER_RPC = 972800;
  private static final int BUFFER_BYTES = MAX_FILE_BYTES_TRANSFERRED_PER_RPC;

  private static Object prettyBytes(final byte[] bytes, final int offset, final int length) {
    return new Object() {
      @Override public String toString() {
        StringBuilder out = new StringBuilder("[");
        for (int i = offset; i < length; i++) {
          if (i != offset) {
            out.append(" ");
          }
          out.append(String.format("%02x", bytes[i]));
        }
        return out + "]@" + String.format("%x", System.identityHashCode(bytes));
      }
    };
  }

  private static Object prettyBytes(byte[] bytes) {
    return prettyBytes(bytes, 0, bytes.length);
  }

  private static FileService getFileService() {
    return FileServiceFactory.getFileService();
  }

  private static AppEngineFile newBlob(@Nullable String mimeType, String downloadFilename)
      throws IOException {
    return getFileService().createNewBlobFile(mimeType, downloadFilename);
  }

  private static byte[] slurp(AppEngineFile file) throws IOException {
    FileReadChannel in = getFileService().openReadChannel(file, false);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteBuffer buf = ByteBuffer.allocate(BUFFER_BYTES);
    while (true) {
      buf.clear();
      int bytesRead = in.read(buf);
      if (bytesRead < 0) {
        break;
      }
      out.write(buf.array(), 0, bytesRead);
    }
    return out.toByteArray();
  }

  private static byte[] getBytes(ByteBuffer buf) {
    byte[] bytes = new byte[buf.remaining()];
    buf.get(bytes);
    return bytes;
  }

  private static AppEngineFile dump(@Nullable String mimeType, String downloadFilename,
      ByteBuffer bytes) throws IOException {
    bytes.rewind();
    AppEngineFile file = newBlob(mimeType, downloadFilename);
    FileWriteChannel out = getFileService().openWriteChannel(file, true);
    while (bytes.hasRemaining()) {
      out.write(bytes);
    }
    out.closeFinally();
    // Verify if what's in the file matches what we wanted to write -- the Files
    // API is still experimental and I've seen it misbehave.
    bytes.rewind();
    byte[] expected = getBytes(bytes);
    byte[] actual= slurp(file);
    if (!Arrays.equals(expected, actual)) {
      // These may be big log messages, but we need to log something that helps debugging.
      log.warning("Tried to write: " + prettyBytes(expected));
      log.warning("Bytes found in file: " + prettyBytes(actual));
      throw new IOException("File " + file + " does not contain the bytes we intended to write");
    }
    return file;
  }

  @Nullable private AttachmentId fetchAttachment(
      SourceInstance instance, RemoteAttachmentInfo info) {
    final String url = instance.getFullAttachmentUrl(info.getPath());
    final Long expectedBytes = info.hasSizeBytes() ? info.getSizeBytes() : null;
    final String mimeType = info.hasMimeType() ? info.getMimeType() : null;
    final String filename = info.hasFilename() ? info.getFilename() : DEFAULT_FILE_NAME;
    try {
      return new RetryHelper().run(
          new RetryHelper.Body<AttachmentId>() {
            @Override public AttachmentId run() throws RetryableFailure, PermanentFailure {
              log.info("Fetching attachment " + url);
              try {
                // We could fetch several attachments asynchronously in parallel to save instance
                // hours, but let's hope that the connection between App Engine and Google Wave
                // is fast enough to make that irrelevant.
                HTTPResponse response = urlfetch.fetch(
                    new HTTPRequest(new URL(url), HTTPMethod.GET));
                if (response.getResponseCode() != 200) {
                  throw new RetryableFailure("Unexpected response code: "
                      + response.getResponseCode());
                }
                byte[] bytes = response.getContent();
                if (expectedBytes != null) {
                  Assert.check(expectedBytes == bytes.length, "Expected %s bytes, got %s: %s",
                      expectedBytes, bytes.length, prettyBytes(bytes));
                }
                AppEngineFile file = dump(mimeType, filename, ByteBuffer.wrap(bytes));
                log.info("Wrote file " + file);
                BlobKey blobKey = getFileService().getBlobKey(file);
                return rawAttachmentService.turnBlobIntoAttachment(blobKey);
              } catch (IOException e) {
                throw new RetryableFailure("IOException fetching " + url);
              }
            }
          });
    } catch (PermanentFailure e) {
      log.severe("Failed to fetch attachment " + url + ", skipping");
      return null;
    }
  }

  public List<ImportTaskPayload> fetchAttachments(FetchAttachmentsAndImportWaveletTask task)
      throws PermanentFailure {
    ImportWaveletTask waveletTask = task.getOriginalImportTask();
    SourceInstance instance = sourceInstanceFactory.parseUnchecked(waveletTask.getInstance());
    WaveletName waveletName = WaveletName.of(
        WaveId.deserialise(waveletTask.getWaveId()),
        WaveletId.deserialise(waveletTask.getWaveletId()));
    LinkedList<RemoteAttachmentInfo> toImport = Lists.newLinkedList(task.getToImport());
    int infosFetchedThisTask = 0;
    while (!toImport.isEmpty() && infosFetchedThisTask < MAX_FETCHES_PER_TASK) {
      RemoteAttachmentInfo info = toImport.removeFirst();
      AttachmentId localId = fetchAttachment(instance, info);
      log.info("Local id: " + localId);
      ImportedAttachmentInfo imported = new ImportedAttachmentInfoGsonImpl();
      imported.setRemoteInfo(info);
      if (localId != null) {
        imported.setLocalId(localId.getId());
      }
      infosFetchedThisTask++;
      task.addImported(imported);
    }
    if (toImport.isEmpty()) {
      log.info("All attachments imported, will import wavelet");
      for (ImportedAttachmentInfo info : task.getImported()) {
        ImportedAttachment attachment = new ImportedAttachmentGsonImpl();
        attachment.setRemoteId(info.getRemoteInfo().getRemoteId());
        if (info.hasLocalId()) {
          attachment.setLocalId(info.getLocalId());
        }
        waveletTask.addAttachment(attachment);
      }
      ImportTaskPayload payload = new ImportTaskPayloadGsonImpl();
      payload.setImportWaveletTask(waveletTask);
      return ImmutableList.of(payload);
    } else {
      log.info("Will fetch remaining attachments in new task");
      task.clearToImport();
      task.addAllToImport(toImport);
      ImportTaskPayload payload = new ImportTaskPayloadGsonImpl();
      payload.setFetchAttachmentsTask(task);
      return ImmutableList.of(payload);
    }
  }

}
