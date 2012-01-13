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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.walkaround.proto.GoogleImport.GoogleDocument;
import com.google.walkaround.proto.GoogleImport.GoogleWavelet;
import com.google.walkaround.proto.ImportWaveletTask;
import com.google.walkaround.proto.ImportWaveletTask.ImportSharingMode;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.wave.server.WaveletCreator;
import com.google.walkaround.wave.server.auth.StableUserId;
import com.google.walkaround.wave.server.googleimport.conversion.PrivateReplyAnchorLegacyIdConverter;
import com.google.walkaround.wave.server.googleimport.conversion.HistorySynthesizer;
import com.google.walkaround.wave.server.googleimport.conversion.StripWColonFilter;
import com.google.walkaround.wave.server.googleimport.conversion.WaveletHistoryConverter;
import com.google.walkaround.wave.server.gxp.SourceInstance;

import org.waveprotocol.wave.migration.helpers.FixLinkAnnotationsFilter;
import org.waveprotocol.wave.model.document.operation.Nindo;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.util.Pair;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.IOException;
import java.util.List;
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

  /** Nindo converter for conversational wavelets. */
  private static final Function<Pair<String, Nindo>, Nindo> CONV_NINDO_CONVERTER =
      new Function<Pair<String, Nindo>, Nindo>() {
    @Override public Nindo apply(Pair<String, Nindo> in) {
      String documentId = in.getFirst();
      Nindo.Builder out = new Nindo.Builder();
      in.getSecond().apply(new StripWColonFilter(new FixLinkAnnotationsFilter(
          new PrivateReplyAnchorLegacyIdConverter(documentId, out))));
      return out.build();
    }
  };

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

  private List<WaveletOperation> convertConvHistory(List<WaveletOperation> in) {
    List<WaveletOperation> out = Lists.newArrayList();
    WaveletHistoryConverter m = new WaveletHistoryConverter(CONV_NINDO_CONVERTER);
    for (WaveletOperation op : in) {
      out.add(m.convertAndApply(op));
    }
    return out;
  }

  public void importWavelet(ImportWaveletTask task) throws IOException, PermanentFailure {
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
    List<WaveletOperation> history = new HistorySynthesizer().synthesizeHistory(wavelet, documents);
    if (EXCESSIVE_LOGGING) {
      log.info("Synthesized history: " + history);
    }
    history = convertConvHistory(history);
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

    // TODO(ohler): import attachments
    // TODO(ohler): make imported waves recognizable as imported
  }

}
