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

package com.google.walkaround.wave.server;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.walkaround.proto.WalkaroundWaveletSnapshot;
import com.google.walkaround.proto.WaveletDiffSnapshot;
import com.google.walkaround.slob.server.AccessDeniedException;
import com.google.walkaround.slob.server.SlobNotFoundException;
import com.google.walkaround.slob.server.SlobStore;
import com.google.walkaround.slob.server.SlobStore.ConnectResult;
import com.google.walkaround.slob.server.SlobStore.HistoryResult;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.conv.ConvStore;
import com.google.walkaround.wave.server.model.ServerMessageSerializer;
import com.google.walkaround.wave.server.udw.UdwStore;
import com.google.walkaround.wave.shared.IdHack;
import com.google.walkaround.wave.shared.WaveSerializer;

import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.automaton.DocumentSchema;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.supplement.PrimitiveSupplement;
import org.waveprotocol.wave.model.supplement.WaveletBasedSupplement;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.Pair;
import org.waveprotocol.wave.model.util.StringMap;
import org.waveprotocol.wave.model.wave.data.DocumentFactory;
import org.waveprotocol.wave.model.wave.data.impl.ObservablePluggableMutableDocument;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;
import org.waveprotocol.wave.model.wave.opbased.OpBasedWavelet;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles loading waves.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveLoader {

  public static class LoadedUdw {
    private final SlobId objectId;
    private final ConnectResult connectResult;
    private final WalkaroundWaveletSnapshot snapshot;

    public LoadedUdw(SlobId objectId,
        ConnectResult connectResult,
        WalkaroundWaveletSnapshot snapshot) {
      Preconditions.checkNotNull(objectId, "Null objectId");
      Preconditions.checkNotNull(connectResult, "Null connectResult");
      Preconditions.checkNotNull(snapshot, "Null snapshot");
      this.objectId = objectId;
      this.connectResult = connectResult;
      this.snapshot = snapshot;
    }

    public SlobId getObjectId() {
      return objectId;
    }

    public ConnectResult getConnectResult() {
      return connectResult;
    }

    public WalkaroundWaveletSnapshot getSnapshot() {
      return snapshot;
    }

    @Override public String toString() {
      return "LoadedUdw(" + objectId + ", " + connectResult + ")";
    }
  }

  public static class LoadedWave {
    private final SlobId convObjectId;
    @Nullable private final ConnectResult convConnectResult;
    private final WaveletDiffSnapshot convSnapshotWithDiffs;
    @Nullable private final LoadedUdw udw;

    public LoadedWave(SlobId convObjectId,
        @Nullable ConnectResult convConnectResult,
        WaveletDiffSnapshot convSnapshotWithDiffs,
        @Nullable LoadedUdw udw) {
      this.convObjectId = checkNotNull(convObjectId, "Null convObjectId");
      this.convConnectResult = convConnectResult;
      this.convSnapshotWithDiffs =
          checkNotNull(convSnapshotWithDiffs, "Null convSnapshotWithDiffs");
      this.udw = udw;
    }

    public SlobId getConvObjectId() {
      return convObjectId;
    }

    @Nullable public ConnectResult getConvConnectResult() {
      return convConnectResult;
    }

    public WaveletDiffSnapshot getConvSnapshotWithDiffs() {
      return convSnapshotWithDiffs;
    }

    @Nullable public LoadedUdw getUdw() {
      return udw;
    }

    @Override public String toString() {
      return getClass().getSimpleName() + "("
          + convObjectId + ", "
          + convConnectResult + ", "
          + convSnapshotWithDiffs + ", "
          + udw
          + ")";
    }
  }

  private static final Logger log = Logger.getLogger(WaveLoader.class.getName());

  private final WaveletCreator waveletCreator;
  private final SlobStore convStore;
  private final SlobStore udwStore;
  private final boolean enableUdw;
  private final boolean enableDiffOnOpen;
  private final WaveSerializer serializer;

  @Inject
  public WaveLoader(WaveletCreator waveletCreator,
      @Flag(FlagName.ENABLE_UDW) boolean enableUdw,
      @Flag(FlagName.ENABLE_DIFF_ON_OPEN) boolean enableDiffOnOpen,
      @ConvStore SlobStore convStore,
      @UdwStore SlobStore udwStore) {
    this.waveletCreator = waveletCreator;
    this.convStore = convStore;
    this.udwStore = udwStore;
    this.enableUdw = enableUdw;
    this.enableDiffOnOpen = enableDiffOnOpen;
    serializer = new WaveSerializer(new ServerMessageSerializer(),
        new DocumentFactory<ObservablePluggableMutableDocument>() {
      @Override public ObservablePluggableMutableDocument create(
          WaveletId waveletId, String docId, DocInitialization content) {
        return new ObservablePluggableMutableDocument(
            DocumentSchema.NO_SCHEMA_CONSTRAINTS, content);
      }
    });
  }

  public LoadedWave loadStaticAtVersion(SlobId convObjectId, @Nullable Long version)
      throws IOException, AccessDeniedException, SlobNotFoundException {
    Preconditions.checkNotNull(convObjectId, "Null convObjectId");
    String rawConvSnapshot = convStore.loadAtVersion(convObjectId, version);
    WaveletName convWaveletName = IdHack.convWaveletNameFromConvObjectId(convObjectId);
    WaveletDataImpl convWavelet = deserializeWavelet(convWaveletName, rawConvSnapshot);
    // TODO(ohler): Determine if it's better UX if we load the UDW here as well.
    return waveWithoutUdw(convObjectId, null, convWavelet);
  }

  public LoadedWave load(SlobId convObjectId, ClientId clientId)
      throws IOException, AccessDeniedException, SlobNotFoundException {
    Preconditions.checkNotNull(convObjectId, "Null convObjectId");
    Preconditions.checkNotNull(clientId, "Null clientId");

    Pair<ConnectResult, String> convPair = convStore.connect(convObjectId, clientId);
    ConnectResult convResult = convPair.getFirst();
    String rawConvSnapshot = convPair.getSecond();
    log(convObjectId, convResult);

    WaveletName convWaveletName = IdHack.convWaveletNameFromConvObjectId(convObjectId);

    // The most recent version of wavelet to get list of documents from.
    WaveletDataImpl convWavelet = deserializeWavelet(convWaveletName, rawConvSnapshot);

    long convVersion = convResult.getVersion();
    Assert.check(convVersion == convWavelet.getVersion(),
        "ConnectResult revision %s does not match wavelet version %s",
        convVersion, convWavelet.getVersion());

    if (!enableUdw) {
      return waveWithoutUdw(convObjectId, convResult, convWavelet);
    } else {
      // Now we go and load some of the history in order to render diffs.
      // For fully read or unread blips, we don't need to load any history.
      // So the approach here is to find all the blips that are partially
      // read, and get the smallest version.

      // TODO(ohler): This should be getOrCreateAndConnect(), so that we can avoid
      // reconstructing the state of the wavelet that we just created.  But
      // snapshot caching would help with this as well, so we should probably do
      // that first.
      SlobId udwId = waveletCreator.getOrCreateUdw(convObjectId);
      Pair<ConnectResult, String> udwPair;
      try {
        udwPair = udwStore.connect(udwId, clientId);
      } catch (SlobNotFoundException e) {
        throw new RuntimeException("UDW disappeared right after getOrCreateUdw(): " + udwId);
      }
      ConnectResult udwResult = udwPair.getFirst();
      WaveletName udwWaveletName = IdHack.udwWaveletNameFromConvObjectIdAndUdwObjectId(
          convObjectId, udwId);
      WaveletDataImpl udw = deserializeWavelet(udwWaveletName, udwPair.getSecond());
      WalkaroundWaveletSnapshot udwSnapshot = serializer.createWaveletMessage(udw);
      LoadedUdw loadedUdw = new LoadedUdw(udwId, udwResult, udwSnapshot);
      if (!enableDiffOnOpen) {
        return waveWithoutDiffs(convObjectId, convResult, convWavelet, loadedUdw);
      }

      StringMap<Long> lastReadVersions = getLastReadVersions(udw, convWavelet);

      // The intermediate revision we'll load our wave in, as a simple optimization
      // that avoids loading most of the history in many use cases. We can try to
      // be smarter about this eventually.
      long intermediateVersion = getMinReadVersion(convWavelet, lastReadVersions);
      if (intermediateVersion <= 0 || intermediateVersion > convVersion) {
        throw new AssertionError("Invalid intermediateVersion " + intermediateVersion
          + ", conv version = " + convVersion);
      }

      String intermediateSnapshot;
      if (intermediateVersion == convVersion) {
        intermediateSnapshot = rawConvSnapshot;
      } else {
        try {
          intermediateSnapshot = convStore.loadAtVersion(convObjectId, intermediateVersion);
        } catch (SlobNotFoundException e) {
          throw new RuntimeException(
              "Conv object disappeared when trying to load intermediate version: " + convObjectId);
        }
      }
      WaveletDataImpl intermediateWavelet = deserializeWavelet(convWaveletName,
          intermediateSnapshot);

      Assert.check(intermediateWavelet.getVersion() == intermediateVersion);

      // We have to stop getting the history at conv version, because we're not
      // computing the metadata again for any concurrent ops that got added
      // since we loaded the convResult - so we don't want them getting added
      // into our diff snapshot. We can let the client catch up instead.
      HistoryResult history;
      try {
        history = convStore.loadHistory(convObjectId, intermediateVersion, convVersion);
      } catch (SlobNotFoundException e) {
        throw new RuntimeException(
            "Conv object disappeared when trying to load history: " + convObjectId);
      }

      // Graceful degrade to no diff-on-open if there was too much data.
      // TODO(danilatos): Potentially try to load more history if there's time,
      // or, memcache the current progress so a refresh would have a better
      // chance... or implement composition trees... or some other improvement.
      if (history.hasMore()) {
        return waveWithoutDiffs(convObjectId, convResult, convWavelet, loadedUdw);
      }

      List<String> mutations = mutations(history.getData());

      WaveletDiffSnapshot convSnapshot = serializer.createWaveletDiffMessage(
          intermediateWavelet, convWavelet, lastReadVersions, mutations);
      return new LoadedWave(convObjectId, convResult, convSnapshot, loadedUdw);
    }
  }

  private LoadedWave waveWithoutUdw(SlobId convObjectId,
      ConnectResult convResult, WaveletDataImpl convWavelet) {
    return waveWithoutDiffs(convObjectId, convResult, convWavelet, null);
  }

  private LoadedWave waveWithoutDiffs(SlobId convObjectId,
      ConnectResult convResult, WaveletDataImpl convWavelet, @Nullable LoadedUdw udw) {
    WaveletDiffSnapshot convSnapshot = serializer.createWaveletDiffMessage(convWavelet, convWavelet,
        CollectionUtils.<Long>createStringMap(), Collections.<String>emptyList());
    return new LoadedWave(convObjectId, convResult, convSnapshot, udw);
  }

  /**
   * Creates a mapping of documents to their last read versions in wavelet
   */
  private StringMap<Long> getLastReadVersions(WaveletDataImpl udw, WaveletDataImpl conv) {
    StringMap<Long> lastReadVersions = CollectionUtils.createStringMap();
    WaveletBasedSupplement supplement = WaveletBasedSupplement.create(
        OpBasedWavelet.createReadOnly(udw));

    for (String documentId : conv.getDocumentIds()) {
      long lastReadBlipVersion = supplement.getLastReadBlipVersion(conv.getWaveletId(), documentId);
      if (lastReadBlipVersion == PrimitiveSupplement.NO_VERSION) {
        continue;
      }
      Assert.check(lastReadBlipVersion >= 0);
      lastReadVersions.put(documentId, lastReadBlipVersion);
    }

    return lastReadVersions;
  }

  List<String> mutations(List<ChangeData<String>> data) {
    List<String> mutations = Lists.newArrayList();
    for (ChangeData<String> c : data) {
      mutations.add(c.getPayload());
    }
    return mutations;
  }

  private void log(SlobId objectId, ConnectResult result) {
    log.info("enableUdw: " + enableUdw + ", load(" + objectId + "): " + result);
  }

  /**
   * Calculates the minimum read version of all documents that have been
   * partially read, with a base case of the current wavelet version
   *
   * I.e. if there are no partially read documents, i.e. each document is either
   * entirely read or entirely unread, then the wavelet version is returned.
   *
   * The domain of document ids from the current state of the wavelet is used,
   * rather than the domain of lastReadVersions, in order to filter out read
   * documents that no longer exist.
   */
  private long getMinReadVersion(
      WaveletDataImpl wavelet, StringMap<Long> lastReadVersions) {
    long minVersion = wavelet.getVersion();
    for (String documentId : wavelet.getDocumentIds()) {
      long documentReadVersion = lastReadVersions.get(documentId, 0L);
      long documentModifiedVersion = wavelet.getDocument(documentId).getLastModifiedVersion();

      if (documentReadVersion >= documentModifiedVersion) {
        continue;
      }

      if (documentReadVersion == 0) {
        continue;
      }

      if (documentReadVersion < minVersion) {
        minVersion = documentReadVersion;
      }
    }
    return minVersion;
  }

  private WaveletDataImpl deserializeWavelet(WaveletName waveletName, String snapshot) {
    try {
      return serializer.deserializeWavelet(waveletName, snapshot);
    } catch (MessageException e) {
      throw new RuntimeException("Invalid snapshot for " + waveletName, e);
    }
  }

}
