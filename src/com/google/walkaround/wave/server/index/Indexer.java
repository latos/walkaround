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

package com.google.walkaround.wave.server.index;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.search.AddDocumentsException;
import com.google.appengine.api.search.AddDocumentsResponse;
import com.google.appengine.api.search.Consistency;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexManager;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.OperationResult;
import com.google.appengine.api.search.SearchRequest;
import com.google.appengine.api.search.SearchResponse;
import com.google.appengine.api.search.SearchResult;
import com.google.appengine.api.search.StatusCode;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.StateAndVersion;
import com.google.walkaround.wave.server.model.ServerMessageSerializer;
import com.google.walkaround.wave.server.model.TextRenderer;
import com.google.walkaround.wave.shared.IdHack;
import com.google.walkaround.wave.shared.WaveSerializer;

import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.automaton.DocumentSchema;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.DocumentFactory;
import org.waveprotocol.wave.model.wave.data.impl.ObservablePluggableMutableDocument;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 *
 */
// XXX XXX Rename and/or rename WaveIndex and/or merge the two.
public class Indexer {
  public static class UserIndexEntry {
    private final SlobId objectId;
    private final ParticipantId creator;
    private final String title;
    private final String snippet;
    private final long lastModifiedMillis;

    public UserIndexEntry(SlobId objectId,
        ParticipantId creator,
        String title,
        String snippet,
        long lastModifiedMillis) {
      this.objectId = checkNotNull(objectId, "Null objectId");
      this.creator = checkNotNull(creator, "Null creator");
      this.title = checkNotNull(title, "Null title");
      this.snippet = checkNotNull(snippet, "Null snippet");
      this.lastModifiedMillis = lastModifiedMillis;
    }

    public SlobId getObjectId() {
      return objectId;
    }

    public ParticipantId getCreator() {
      return creator;
    }

    public String getTitle() {
      return title;
    }

    public String getSnippet() {
      return snippet;
    }

    public long getLastModifiedMillis() {
      return lastModifiedMillis;
    }

    @Override public String toString() {
      return "IndexEntry("
          + objectId + ", "
          + creator + ", "
          + title + ", "
          + snippet + ", "
          + lastModifiedMillis
          + ")";
    }

    @Override public final boolean equals(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof UserIndexEntry)) { return false; }
      UserIndexEntry other = (UserIndexEntry) o;
      return lastModifiedMillis == other.lastModifiedMillis
          && Objects.equal(objectId, other.objectId)
          && Objects.equal(creator, other.creator)
          && Objects.equal(title, other.title)
          && Objects.equal(snippet, other.snippet);
    }

    @Override public final int hashCode() {
      return Objects.hashCode(objectId, creator, title, snippet, lastModifiedMillis);
    }
  }

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(Indexer.class.getName());

  private static final String USER_INBOX_INDEX_PREFIX = "INBOX-";

  private final WaveSerializer serializer;
  private final SimpleLoader loader;
  private final IndexManager indexManager;

  @Inject
  public Indexer(SimpleLoader loader, IndexManager indexManager) {
    this.loader = loader;
    this.indexManager = indexManager;
    this.serializer = new WaveSerializer(
        new ServerMessageSerializer(), new DocumentFactory<ObservablePluggableMutableDocument>() {

          @Override
          public ObservablePluggableMutableDocument create(
              WaveletId waveletId, String docId, DocInitialization content) {
            return new ObservablePluggableMutableDocument(
                DocumentSchema.NO_SCHEMA_CONSTRAINTS, content);
          }
        });
  }

  public void index(SlobId slobId) throws IOException {
    // TODO(danilatos): Remove waves from the inboxes of participants that have
    // been removed from the wave.

    StateAndVersion rawConv = loader.load(slobId);
    WaveletName convWaveletName = IdHack.convWaveletNameFromConvObjectId(slobId);

    WaveletDataImpl convWavelet = deserializeWavelet(convWaveletName,
        rawConv.getState().snapshot());

    if (rawConv.getVersion() != convWavelet.getVersion()) {
      throw new AssertionError("Raw version " + rawConv.getVersion() +
          " does not match wavelet version " + convWavelet.getVersion());
    }

    String content = TextRenderer.renderToText(convWavelet);
    String title = content.length() > 20 ? content.substring(0, 20) : content; // hack
    String creator = convWavelet.getCreator().getAddress();
    String lastModified = convWavelet.getLastModifiedTime() + "";

    for (ParticipantId participant : convWavelet.getParticipants()) {
      log.info("Indexing " + slobId.getId() + " for " + participant.getAddress());

      Document.Builder builder = Document.newBuilder();
      builder.setId(slobId.getId());
      builder.addField(Field.newBuilder().setName("content").setText(content));
      builder.addField(Field.newBuilder().setName("title").setText(title));
      builder.addField(Field.newBuilder().setName("creator").setText(creator));
      builder.addField(Field.newBuilder().setName("modified").setText(lastModified));
      Document doc = builder.build();

      Index idx = getIndex(participant);

      log.info("Indexing " + describe(doc));

      // TODO(danilatos): Factor out all the error handling?
      AddDocumentsResponse resp;
      try {
        resp = idx.add(doc);
      } catch (AddDocumentsException e) {
        throw new IOException("Error indexing " + slobId, e);
      }
      for (OperationResult result : resp.getResults()) {
        if (!result.getCode().equals(StatusCode.OK)) {
          throw new IOException("Error indexing " + slobId + ", " + result.getMessage());
        }
      }
    }
  }

  private Index getIndex(ParticipantId participant) {
    return indexManager.getIndex(
        IndexSpec.newBuilder().setName(getIndexName(participant))
        // We could consider making this global, though the documentation
        // says not more than 1 update per second, which is a bit borderline.
        .setConsistency(Consistency.PER_DOCUMENT).build());
  }

  private String getIndexName(ParticipantId participant) {
    // NOTE(danilatos): This is not robust against email addresses being recycled.
    return USER_INBOX_INDEX_PREFIX + participant.getAddress();
  }

  private WaveletDataImpl deserializeWavelet(WaveletName waveletName, String snapshot) {
    try {
      return serializer.deserializeWavelet(waveletName, snapshot);
    } catch (MessageException e) {
      throw new RuntimeException("Invalid snapshot for " + waveletName, e);
    }
  }

  public List<UserIndexEntry> findWaves(ParticipantId user, String query, int offset, int limit)
      throws IOException {

    SearchRequest request = SearchRequest.newBuilder()
        .setQuery(query)
        .setFieldsToSnippet("content")
        .setOffset(offset)
        .setLimit(limit)
        .build();
    // TODO(danilatos): Figure out if this also sometimes throws exceptions.
    // In the example documentation they guard it with a try...catch Exception block.
    SearchResponse response = getIndex(user).search(request);
    StatusCode responseCode = response.getOperationResult().getCode();
    if (!StatusCode.OK.equals(responseCode)) {
      throw new IOException("Search fail for query '" + query + "'"
          + ", response: " + responseCode + " " + response.getOperationResult().getMessage());
    }
    List<UserIndexEntry> entries = Lists.newArrayList();
    for (SearchResult result : response) {
      Document doc = result.getDocument();

      List<Field> expressions = result.getExpressions();
      assert expressions.size() == 1;
      String snippet = expressions.get(0).getText();

      entries.add(new UserIndexEntry(
          new SlobId(doc.getId()),
          ParticipantId.ofUnsafe(getField(doc, "creator")),
          getField(doc, "title"),
          snippet,
          Long.parseLong(getField(doc, "modified"))
          ));
    }
    return entries;
  }

  private String getField(Document doc, String field) {
    Iterable<Field> fields = doc.getField(field);
    if (fields == null) {
      throw new RuntimeException("No field " + field + " in doc " + describe(doc));
    }
    for (Field f : fields) {
      return f.getText();
    }
    throw new RuntimeException("Empty field list for " + field + " in doc " + describe(doc));
  }

  private String describe(Document doc) {
    return "doc[" + doc.getId() + " with " + doc.getFieldNames() + "]";
  }
}