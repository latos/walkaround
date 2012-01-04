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

package com.google.walkaround.wave.server.googleimport.conversion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.walkaround.proto.GoogleImport.GoogleDocument;
import com.google.walkaround.proto.GoogleImport.GoogleWavelet;
import com.google.walkaround.util.shared.Assert;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMapBuilder;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;
import org.waveprotocol.wave.model.document.operation.impl.DocOpUtil;
import org.waveprotocol.wave.model.id.IdUtil;
import org.waveprotocol.wave.model.operation.wave.AddParticipant;
import org.waveprotocol.wave.model.operation.wave.BlipContentOperation;
import org.waveprotocol.wave.model.operation.wave.NoOp;
import org.waveprotocol.wave.model.operation.wave.RemoveParticipant;
import org.waveprotocol.wave.model.operation.wave.WaveletBlipOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperationContext;
import org.waveprotocol.wave.model.wave.InvalidParticipantAddress;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Synthesizes a history for a wavelet given a snapshot.  Works for conversation
 * wavelets and UDWs.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class HistorySynthesizer {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(HistorySynthesizer.class.getName());

  public HistorySynthesizer() {
  }

  private static WaveletOperationContext getContext(String author, long timestampMillis) {
    return new WaveletOperationContext(ParticipantId.ofUnsafe(author), timestampMillis, 1);
  }

  private static WaveletOperation newNoOp(String author, long timestampMillis) {
    return new NoOp(getContext(author, timestampMillis));
  }

  public static WaveletOperation newAddParticipant(
      String author, long timestampMillis, String userId) {
    return new AddParticipant(getContext(author, timestampMillis), ParticipantId.ofUnsafe(userId));
  }

  private WaveletOperation newRemoveParticipant(
      String author, long timestampMillis, String userId) {
    return new RemoveParticipant(getContext(author, timestampMillis),
        ParticipantId.ofUnsafe(userId));
  }

  private WaveletOperation newMutateDocument(String author, long timestampMillis,
      String documentId, DocOp op) {
    return new WaveletBlipOperation(documentId,
        new BlipContentOperation(getContext(author, timestampMillis), op));
  }

  private boolean containsAnnotationKey(DocInitialization content, final String key) {
    final boolean[] found = { false };
    content.apply(new DocInitializationCursor() {
        @Override public void characters(String chars) {}
        @Override public void elementStart(String type, Attributes attrs) {}
        @Override public void elementEnd() {}
        @Override public void annotationBoundary(AnnotationBoundaryMap map) {
          for (int i = 0; i < map.changeSize(); i++) {
            if (key.equals(map.getChangeKey(i))) {
              found[0] = true;
            }
          }
        }
      });
    return found[0];
  }

  // We produce worthy doc ops in pairs: first we set an annotation from null to
  // "a", then we set it from "a" back to "null".
  private DocOp newWorthyDocumentOperation(DocInitialization content, boolean first) {
    int size = DocOpUtil.resultingDocumentLength(content);
    String key = "walkaround-irrelevant-annotation-change-to-force-contribution";
    while (containsAnnotationKey(content, key)) {
      key += "1";
    }
    return new DocOpBuilder()
        .annotationBoundary(
            new AnnotationBoundaryMapBuilder().change(key,
                first ? null : "a",
                first ? "a" : null)
            .build())
        .retain(size)
        .annotationBoundary(new AnnotationBoundaryMapBuilder().end(key).build())
        .build();
  }

  private List<WaveletOperation> newWorthyContribution(String author, long timestampMillis,
      String documentId, DocInitialization content) {
    return ImmutableList.of(
        newMutateDocument(author, timestampMillis, documentId,
            newWorthyDocumentOperation(content, true)),
        newMutateDocument(author, timestampMillis, documentId,
            newWorthyDocumentOperation(content, false)));
  }

  private List<GoogleDocument> selectNonEmptyDocuments(List<GoogleDocument> docs) {
    ImmutableList.Builder<GoogleDocument> out = ImmutableList.builder();
    for (GoogleDocument doc : docs) {
      if (DocOpUtil.resultingDocumentLength(
              GoogleDeserializer.deserializeContent(doc.getContent())) > 0) {
        out.add(doc);
      }
    }
    return out.build();
  }

  private List<GoogleDocument> selectBlips(List<GoogleDocument> docs) {
    ImmutableList.Builder<GoogleDocument> out = ImmutableList.builder();
    for (GoogleDocument doc : docs) {
      if (IdUtil.isBlipId(doc.getDocumentId())) {
        out.add(doc);
      }
    }
    return out.build();
  }

  private List<GoogleDocument> selectDataDocumentsExceptManifest(List<GoogleDocument> docs) {
    ImmutableList.Builder<GoogleDocument> out = ImmutableList.builder();
    for (GoogleDocument doc : docs) {
      if (!IdUtil.isBlipId(doc.getDocumentId())
          && !IdUtil.isManifestDocument(doc.getDocumentId())) {
        out.add(doc);
      }
    }
    return out.build();
  }

  @Nullable private GoogleDocument selectManifest(List<GoogleDocument> docs) {
    for (GoogleDocument doc : docs) {
      if (IdUtil.isManifestDocument(doc.getDocumentId())) {
        return doc;
      }
    }
    return null;
  }

  // Each string is nullable, the array itself is not.
  private String pickValidParticipantId(@Nullable String... options) {
    for (String s : options) {
      if (s != null) {
        try {
          ParticipantId.of(s);
          return s;
        } catch (InvalidParticipantAddress e) {
          log.info("Encountered invalid participant address: " + s);
        }
      }
    }
    throw new RuntimeException("No valid participant id: " + Arrays.toString(options));
  }

  private String getFirstContributor(GoogleDocument doc, GoogleWavelet w) {
    Assert.check(doc.getContributorCount() > 0 || !IdUtil.isBlipId(doc.getDocumentId()),
        "No contributors on blip %s: %s",
        doc.getDocumentId(), doc.getContent());
    return pickValidParticipantId(doc.getAuthor(),
        doc.getContributorCount() == 0 ? null : doc.getContributor(0),
        w.getCreator());
  }

  private List<GoogleDocument> sortedByLastModifiedTime(List<GoogleDocument> docs) {
    List<GoogleDocument> out = Lists.newArrayList(docs);
    Collections.sort(out, new Comparator<GoogleDocument>() {
      @Override public int compare(GoogleDocument a, GoogleDocument b) {
        return Ints.saturatedCast(a.getLastModifiedTimeMillis() - b.getLastModifiedTimeMillis());
      }});
    return out;
  }

  private void checkParticipantNormalized(String participantId) {
    Assert.check(participantId.toLowerCase().equals(participantId),
        "Participant id not normalized: %s", participantId);
  }

  private void checkParticipantsNormalized(GoogleWavelet w, List<GoogleDocument> docs) {
    checkParticipantNormalized(w.getCreator());
    for (String p : w.getParticipantList()) {
      checkParticipantNormalized(p);
    }
    for (GoogleDocument doc : docs) {
      checkParticipantNormalized(doc.getAuthor());
      for (String p : doc.getContributorList()) {
        checkParticipantNormalized(p);
      }
    }
  }

  private void checkLastModifiedTime(GoogleWavelet w, List<GoogleDocument> docs) {
    long lastDocumentModificationTimeMillis = Long.MIN_VALUE;
    for (GoogleDocument doc : docs) {
      lastDocumentModificationTimeMillis = Math.max(lastDocumentModificationTimeMillis,
          doc.getLastModifiedTimeMillis());
    }
    Assert.check(w.getLastModifiedTimeMillis() >= lastDocumentModificationTimeMillis,
        "Wavelet last modified %s, last document %s",
        w.getLastModifiedTimeMillis(), lastDocumentModificationTimeMillis);
  }

  private void checkDisjoint(List<?> a, List<?> b) {
    // TODO(ohler): implement
  }

  private void checkNoDuplicates(List<?> list) {
    // TODO(ohler): implement
  }

  // This will produce a history that looks like this:
  // 1. (at creation time of the wavelet) the creator adds himself
  // 2. (at creation time of the wavelet) the creator adds all other
  //    participants, in the order in which they are listed in the snapshot
  // 3. (at creation time of the wavelet) the creator puts the content into all
  //    data documents except manifest, in unspecified order.
  // 4. for each non-empty blip in arbitrary order:
  //      4a. (at creation time of the wavelet) The author/first contributor of that
  //          blip sets the content.
  //      4b. for each contributor on that blip:
  //            (at creation time of the wavelet) the contributor touches that blip
  //            with a worthy contribution (2 ops: one op that adds an unused
  //            annotation, a second op that removes it)
  // 5. if the wavelet has a manifest:
  //      (at creation time of the wavelet) the creator adds the manifest
  // 6. for each blip or data document, in last modified version order:
  //      (at last modification time of that document) the first contributor (or
  //      creator for data documents) touches the document with a worthy
  //      contribution.
  //      These changes become the new versions for UDW purposes -- we should
  //      record them somewhere.
  // 7. (at last modified time of the wavelet) the creator adds a no-op
  // 8. (at last modified time of the wavelet) the creator removes himself if
  //    he's not a participant
  public List<WaveletOperation> synthesizeHistory(GoogleWavelet w, List<GoogleDocument> docs) {
    checkNoDuplicates(docs);
    docs = selectNonEmptyDocuments(docs);
    checkParticipantsNormalized(w, docs);
    checkNoDuplicates(w.getParticipantList());
    checkLastModifiedTime(w, docs);

    List<GoogleDocument> blipsInArbitraryOrder = selectBlips(docs);
    List<GoogleDocument> dataDocsExceptManifestInArbitraryOrder =
        selectDataDocumentsExceptManifest(docs);
    @Nullable GoogleDocument manifest = selectManifest(docs);
    checkDisjoint(blipsInArbitraryOrder, dataDocsExceptManifestInArbitraryOrder);
    checkDisjoint(ImmutableList.of(manifest), blipsInArbitraryOrder);
    checkDisjoint(ImmutableList.of(manifest), dataDocsExceptManifestInArbitraryOrder);

    List<GoogleDocument> docsInLastModifiedTimeOrder = sortedByLastModifiedTime(docs);

    List<WaveletOperation> history = Lists.newArrayList();
    // 1
    history.add(newAddParticipant(w.getCreator(), w.getCreationTimeMillis(), w.getCreator()));
    // 2
    for (String p : w.getParticipantList()) {
      if (!p.equals(w.getCreator())) {
        history.add(newAddParticipant(w.getCreator(), w.getCreationTimeMillis(), p));
      }
    }
    // 3
    for (GoogleDocument doc : dataDocsExceptManifestInArbitraryOrder) {
      history.add(newMutateDocument(w.getCreator(), w.getCreationTimeMillis(),
          doc.getDocumentId(), GoogleDeserializer.deserializeContent(doc.getContent())));
    }
    // 4
    for (GoogleDocument doc : blipsInArbitraryOrder) {
      Assert.check(IdUtil.isBlipId(doc.getDocumentId()));
      DocInitialization content = GoogleDeserializer.deserializeContent(doc.getContent());
      String docId = doc.getDocumentId();
      // 4a
      history.add(newMutateDocument(getFirstContributor(doc, w), w.getCreationTimeMillis(),
          docId, content));
      // 4b
      for (String contributor : doc.getContributorList()) {
        history.addAll(
            newWorthyContribution(contributor, w.getCreationTimeMillis(), docId, content));
      }
    }
    // 5
    if (manifest != null) {
      history.add(newMutateDocument(w.getCreator(), w.getCreationTimeMillis(),
          manifest.getDocumentId(), GoogleDeserializer.deserializeContent(manifest.getContent())));
    }
    // 6
    for (GoogleDocument doc : docsInLastModifiedTimeOrder) {
      history.addAll(
          newWorthyContribution(getFirstContributor(doc, w), doc.getLastModifiedTimeMillis(),
              doc.getDocumentId(), GoogleDeserializer.deserializeContent(doc.getContent())));
    }
    // 7
    history.add(newNoOp(w.getCreator(), w.getLastModifiedTimeMillis()));
    // 8
    if (!w.getParticipantList().contains(w.getCreator())) {
      history.add(
          newRemoveParticipant(w.getCreator(), w.getLastModifiedTimeMillis(), w.getCreator()));
    }
    return history;
  }

}
