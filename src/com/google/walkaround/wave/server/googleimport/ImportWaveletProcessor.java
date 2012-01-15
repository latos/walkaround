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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.walkaround.proto.FetchAttachmentsAndImportWaveletTask;
import com.google.walkaround.proto.FetchAttachmentsAndImportWaveletTask.RemoteAttachmentInfo;
import com.google.walkaround.proto.GoogleImport.GoogleDocument;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent.Component;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent.ElementStart;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent.KeyValuePair;
import com.google.walkaround.proto.GoogleImport.GoogleWavelet;
import com.google.walkaround.proto.ImportTaskPayload;
import com.google.walkaround.proto.ImportWaveletTask;
import com.google.walkaround.proto.ImportWaveletTask.ImportSharingMode;
import com.google.walkaround.proto.ImportWaveletTask.ImportedAttachment;
import com.google.walkaround.proto.gson.FetchAttachmentsAndImportWaveletTaskGsonImpl;
import com.google.walkaround.proto.gson.FetchAttachmentsAndImportWaveletTaskGsonImpl.RemoteAttachmentInfoGsonImpl;
import com.google.walkaround.proto.gson.ImportTaskPayloadGsonImpl;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.WaveletCreator;
import com.google.walkaround.wave.server.attachment.AttachmentId;
import com.google.walkaround.wave.server.auth.StableUserId;
import com.google.walkaround.wave.server.googleimport.conversion.AttachmentIdConverter;
import com.google.walkaround.wave.server.googleimport.conversion.HistorySynthesizer;
import com.google.walkaround.wave.server.googleimport.conversion.PrivateReplyAnchorLegacyIdConverter;
import com.google.walkaround.wave.server.googleimport.conversion.StripWColonFilter;
import com.google.walkaround.wave.server.googleimport.conversion.WaveletHistoryConverter;
import com.google.walkaround.wave.server.gxp.SourceInstance;

import org.waveprotocol.wave.migration.helpers.FixLinkAnnotationsFilter;
import org.waveprotocol.wave.model.document.operation.Nindo;
import org.waveprotocol.wave.model.id.IdConstants;
import org.waveprotocol.wave.model.id.IdUtil;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.util.Pair;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Processes {@link ImportWaveletTask}s.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ImportWaveletProcessor {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ImportWaveletProcessor.class.getName());

  private static final boolean EXCESSIVE_LOGGING = false;

  private final RobotApi.Factory robotApiFactory;
  private final SourceInstance.Factory sourceInstanceFactory;
  private final WaveletCreator waveletCreator;
  private final ParticipantId importingUser;
  private final StableUserId userId;
  private final PerUserTable perUserTable;
  private final CheckedDatastore datastore;

  @Inject
  public ImportWaveletProcessor(RobotApi.Factory robotApiFactory,
      SourceInstance.Factory sourceInstanceFactory,
      WaveletCreator waveletCreator,
      ParticipantId importingUser,
      StableUserId userId,
      PerUserTable perUserTable,
      CheckedDatastore datastore) {
    this.robotApiFactory = robotApiFactory;
    this.sourceInstanceFactory = sourceInstanceFactory;
    this.waveletCreator = waveletCreator;
    this.importingUser = importingUser;
    this.userId = userId;
    this.perUserTable = perUserTable;
    this.datastore = datastore;
  }

  /** Returns a nindo converter for conversational wavelets. */
  private Function<Pair<String, Nindo>, Nindo> getConvNindoConverter(
      final Map<String, AttachmentId> attachmentIdMapping) {
    return new Function<Pair<String, Nindo>, Nindo>() {
      @Override public Nindo apply(Pair<String, Nindo> in) {
        String documentId = in.getFirst();
        Nindo.Builder out = new Nindo.Builder();
        in.getSecond().apply(
            // StripWColonFilter must be before AttachmentIdConverter.
            new StripWColonFilter(
                new FixLinkAnnotationsFilter(
                    new PrivateReplyAnchorLegacyIdConverter(documentId,
                        new AttachmentIdConverter(attachmentIdMapping,
                            out)))));
        return out.build();
      }
    };
  }

  private String convertGooglewaveToGmail(String participantId) {
    return participantId.replace("@googlewave.com", "@gmail.com");
  }

  private Pair<GoogleWavelet, ImmutableList<GoogleDocument>> convertGooglewaveToGmail(
      Pair<GoogleWavelet, ? extends List<GoogleDocument>> pair) {
    GoogleWavelet w = pair.getFirst();
    List<GoogleDocument> docs = pair.getSecond();
    GoogleWavelet.Builder w2 = GoogleWavelet.newBuilder(w);
    w2.setCreator(convertGooglewaveToGmail(w.getCreator()));
    w2.clearParticipant();
    for (String p : w.getParticipantList()) {
      w2.addParticipant(convertGooglewaveToGmail(p));
    }
    ImmutableList.Builder<GoogleDocument> docs2 = ImmutableList.builder();
    for (GoogleDocument doc : docs) {
      GoogleDocument.Builder doc2 = GoogleDocument.newBuilder(doc);
      doc2.setAuthor(convertGooglewaveToGmail(doc.getAuthor()));
      doc2.clearContributor();
      for (String p : doc.getContributorList()) {
        doc2.addContributor(convertGooglewaveToGmail(p));
      }
      docs2.add(doc2.build());
    }
    return Pair.of(w2.build(), docs2.build());
  }

  private List<WaveletOperation> convertConvHistory(List<WaveletOperation> in,
      Map<String, AttachmentId> attachmentIdMapping) {
    List<WaveletOperation> out = Lists.newArrayList();
    WaveletHistoryConverter m = new WaveletHistoryConverter(
        getConvNindoConverter(attachmentIdMapping));
    for (WaveletOperation op : in) {
      out.add(m.convertAndApply(op));
    }
    return out;
  }

  private Map<String, AttachmentId> buildAttachmentMapping(ImportWaveletTask task) {
    ImmutableMap.Builder<String, AttachmentId> out = ImmutableMap.builder();
    for (ImportedAttachment attachment : task.getAttachment()) {
      if (attachment.hasLocalId()) {
        out.put(attachment.getRemoteId(), new AttachmentId(attachment.getLocalId()));
      }
    }
    return out.build();
  }

  // Example attachment metadata document:
  // document id: attach+foo
  // begin doc
  //   <node key="upload_progress" value="678"></node>
  //   <node key="attachment_size" value="678"></node>
  //   <node key="mime_type" value="application/octet-stream"></node>
  //   <node key="filename" value="the-file-name"></node>
  //   <node key="attachment_url" value="/attachment/the-file-name?id=a-short-id&key=a-long-key"></node>
  // end doc
  private Map<String, String> makeMapFromDocument(GoogleDocumentContent doc,
      String elementType, String keyAttributeName, String valueAttributeName) {
    Preconditions.checkNotNull(elementType, "Null elementType");
    Preconditions.checkNotNull(keyAttributeName, "Null keyAttributeName");
    Preconditions.checkNotNull(valueAttributeName, "Null valueAttributeName");
    Map<String, String> out = Maps.newHashMap();
    for (Component component : doc.getComponentList()) {
      if (component.hasElementStart()) {
        ElementStart start = component.getElementStart();
        Assert.check(elementType.equals(start.getType()), "Unexpected element type: %s", doc);
        Map<String, String> attrs = Maps.newHashMap();
        for (KeyValuePair attr : start.getAttributeList()) {
          attrs.put(attr.getKey(), attr.getValue());
        }
        Assert.check(attrs.size() == 2, "Need two attrs: %s", doc);
        String key = attrs.get(keyAttributeName);
        String value = attrs.get(valueAttributeName);
        Assert.check(key != null, "Key not found: %s", doc);
        Assert.check(value != null, "Value not found: %s", doc);
        // If already exists, overwrite.  Last entry wins.  TODO(ohler): confirm that this is
        // consistent with how the documents are interpreted by Google Wave.
        out.put(key, value);
      }
    }
    return out;
  }

  private void populateAttachmentInfo(FetchAttachmentsAndImportWaveletTask newTask,
      List<GoogleDocument> documentList, Map<String, GoogleDocument> attachmentDocs) {
    for (Map.Entry<String, GoogleDocument> entry : attachmentDocs.entrySet()) {
      String attachmentId = entry.getKey();
      GoogleDocumentContent metadataDoc = entry.getValue().getContent();
      log.info("metadataDoc=" + metadataDoc);
      Map<String, String> map = makeMapFromDocument(metadataDoc, "node", "key", "value");
      log.info("Attachment metadata for " + attachmentId + ": " + map + ")");
      RemoteAttachmentInfo info = new RemoteAttachmentInfoGsonImpl();
      info.setRemoteId(attachmentId);
      if (map.get("attachment_url") == null) {
        log.warning("Attachment " + attachmentId + " has no URL (incomplete upload?), skipping: "
            + map);
        continue;
      }
      info.setPath(map.get("attachment_url"));
      if (map.get("filename") != null) {
        info.setFilename(map.get("filename"));
      }
      if (map.get("mime_type") != null) {
        info.setMimeType(map.get("mime_type"));
      }
      if (map.get("size") != null) {
        info.setSizeBytes(Long.parseLong(map.get("size")));
      }
      newTask.addToImport(info);
    }
  }

  private Map<String, GoogleDocument> getAttachmentDocs(List<GoogleDocument> docs) {
    Map<String, GoogleDocument> out = Maps.newHashMap();
    for (GoogleDocument doc : docs) {
      String docId = doc.getDocumentId();
      Assert.check(!out.containsKey(docId), "Duplicate doc id %s: %s", docId, docs);
      if (IdUtil.isAttachmentDataDocument(docId)) {
        String[] components = IdUtil.split(docId);
        if (components == null) {
          throw new RuntimeException("Failed to split attachment doc id: " + docId);
        }
        if (components.length != 2) {
          throw new RuntimeException("Bad number of components in attachment doc id " + docId
              + ": " + Arrays.toString(components));
        }
        if (!IdConstants.ATTACHMENT_METADATA_PREFIX.equals(components[0])) {
          throw new RuntimeException("Bad first component in attachment doc id " + docId
              + ": " + Arrays.toString(components));
        }
        String attachmentId = components[1];
        out.put(attachmentId, doc);
      }
    }
    return ImmutableMap.copyOf(out);
  }

  public List<ImportTaskPayload> importWavelet(ImportWaveletTask task)
      throws IOException, PermanentFailure {
    final SourceInstance instance = sourceInstanceFactory.parseUnchecked(task.getInstance());
    final WaveletName waveletName = WaveletName.of(
        WaveId.deserialise(task.getWaveId()),
        WaveletId.deserialise(task.getWaveletId()));
    ImportSharingMode sharingMode = task.getSharingMode();
    RobotApi api = robotApiFactory.create(instance.getApiUrl());
    Pair<GoogleWavelet, ImmutableList<GoogleDocument>> snapshot =
        convertGooglewaveToGmail(api.getSnapshot(waveletName));
    GoogleWavelet wavelet = snapshot.getFirst();
    List<GoogleDocument> documents = snapshot.getSecond();
    log.info("Got snapshot for " + waveletName + ": "
        + wavelet.getParticipantCount() + " participants, "
        + documents.size() + " documents");
    log.info("Document ids: " + Lists.transform(documents,
        new Function<GoogleDocument, String>() {
          @Override public String apply(GoogleDocument doc) { return doc.getDocumentId(); }
        }));
    // Maps attachment ids (not document ids) to documents.
    Map<String, GoogleDocument> attachmentDocs = getAttachmentDocs(documents);
    Map<String, AttachmentId> attachmentMapping;
    if (attachmentDocs.isEmpty()) {
      log.info("No attachments");
      attachmentMapping = ImmutableMap.of();
    } else if (task.getAttachmentSize() > 0) {
      attachmentMapping = buildAttachmentMapping(task);
      log.info("Attachments already imported; importing with attachment mapping "
          + attachmentMapping);
    } else {
      log.info("Found attachmend ids " + attachmentDocs.keySet());
      // Replace this task with one that fetches attachments and then imports.
      FetchAttachmentsAndImportWaveletTask newTask =
          new FetchAttachmentsAndImportWaveletTaskGsonImpl();
      newTask.setOriginalImportTask(task);
      populateAttachmentInfo(newTask, documents, attachmentDocs);
      ImportTaskPayload payload = new ImportTaskPayloadGsonImpl();
      payload.setFetchAttachmentsTask(newTask);
      return ImmutableList.of(payload);
    }
    List<WaveletOperation> history = new HistorySynthesizer().synthesizeHistory(wavelet, documents);
    history = convertConvHistory(history, attachmentMapping);
    if (EXCESSIVE_LOGGING) {
      log.info("Synthesized history: " + history);
    }
    switch (sharingMode) {
      case PRIVATE:
        for (String participant : ImmutableList.copyOf(wavelet.getParticipantList())) {
          history.add(
              HistorySynthesizer.newRemoveParticipant(importingUser.getAddress(),
                  wavelet.getLastModifiedTimeMillis(), participant));
        }
        history.add(
            HistorySynthesizer.newAddParticipant(importingUser.getAddress(),
                wavelet.getLastModifiedTimeMillis(), importingUser.getAddress()));
        break;
      case SHARED:
        if (!wavelet.getParticipantList().contains(importingUser.getAddress())) {
          log.info(
              importingUser + " is not a participant, adding: " + wavelet.getParticipantList());
          history.add(
              HistorySynthesizer.newAddParticipant(importingUser.getAddress(),
                  wavelet.getLastModifiedTimeMillis(), importingUser.getAddress()));
        }
        throw new RuntimeException("not implemented");
      default:
        throw new AssertionError("Unexpected ImportSharingMode: " + sharingMode);
    }
    if (EXCESSIVE_LOGGING) {
      log.info("Converted synthesized history: " + history);
    }
    final SlobId newId = waveletCreator.newConvWithGeneratedId(history);
    log.info("Imported wavelet " + waveletName + " as local id " + newId);
    new RetryHelper().run(
        new RetryHelper.VoidBody() {
          @Override public void run() throws RetryableFailure, PermanentFailure {
            CheckedTransaction tx = datastore.beginTransaction();
            try {
              RemoteConvWavelet entry = perUserTable.getWavelet(tx, userId, instance, waveletName);
              entry.setPrivateLocalId(newId);
              perUserTable.putWavelet(tx, userId, entry);
              tx.commit();
            } finally {
              tx.close();
            }
          }
        });
    return ImmutableList.of();
  }

}
