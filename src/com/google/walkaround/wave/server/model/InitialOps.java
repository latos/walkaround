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

package com.google.walkaround.wave.server.model;

import com.google.walkaround.util.shared.RandomBase64Generator;
import com.google.walkaround.wave.shared.IdHack;

import org.waveprotocol.wave.model.conversation.Conversation;
import org.waveprotocol.wave.model.conversation.ConversationThread;
import org.waveprotocol.wave.model.conversation.ConversationView;
import org.waveprotocol.wave.model.conversation.WaveBasedConversationView;
import org.waveprotocol.wave.model.id.IdGenerator;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.operation.OperationRuntimeException;
import org.waveprotocol.wave.model.operation.SilentOperationSink;
import org.waveprotocol.wave.model.operation.wave.AddParticipant;
import org.waveprotocol.wave.model.operation.wave.BasicWaveletOperationContextFactory;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperationContext;
import org.waveprotocol.wave.model.testing.BasicFactories;
import org.waveprotocol.wave.model.testing.FakeDocument;
import org.waveprotocol.wave.model.version.HashedVersion;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.ParticipationHelper;
import org.waveprotocol.wave.model.wave.data.ObservableWaveletData;
import org.waveprotocol.wave.model.wave.data.ReadableWaveletData;
import org.waveprotocol.wave.model.wave.data.impl.WaveViewDataImpl;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;
import org.waveprotocol.wave.model.wave.opbased.OpBasedWavelet;
import org.waveprotocol.wave.model.wave.opbased.WaveViewImpl;
import org.waveprotocol.wave.model.wave.opbased.WaveViewImpl.WaveletConfigurator;
import org.waveprotocol.wave.model.wave.opbased.WaveViewImpl.WaveletFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utilities for initial operations on wavelets.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class InitialOps {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(InitialOps.class.getName());

  public static List<WaveletOperation> conversationWaveletOps(final ParticipantId creator,
      final long creationTime, final RandomBase64Generator random) {
    final List<WaveletOperation> ops = new ArrayList<WaveletOperation>();
    final SilentOperationSink<WaveletOperation> sink = new SilentOperationSink<WaveletOperation>() {
      @Override public void consume(WaveletOperation op) {
        ops.add(op);
      }
    };

    IdGenerator gen = new IdHack.MinimalIdGenerator(
        // Fake wavelet ID since we always use fake wavelet IDs on the server
        // side (they are never persisted).  The UDW ID is not needed in the
        // code below but the model code asks for it anyway.  We arbitrarily use
        // DISABLED_UDW_ID.
        IdHack.FAKE_WAVELET_NAME.waveletId, IdHack.DISABLED_UDW_ID,
        random);
    final WaveViewDataImpl waveData = WaveViewDataImpl.create(IdHack.FAKE_WAVELET_NAME.waveId);
    final FakeDocument.Factory docFactory = BasicFactories.fakeDocumentFactory();
    final ObservableWaveletData.Factory<?> waveletDataFactory =
        new ObservableWaveletData.Factory<WaveletDataImpl>() {
          private final ObservableWaveletData.Factory<WaveletDataImpl> inner =
              WaveletDataImpl.Factory.create(docFactory);

          @Override
          public WaveletDataImpl create(ReadableWaveletData data) {
            WaveletDataImpl wavelet = inner.create(data);
            waveData.addWavelet(wavelet);
            return wavelet;
          }
        };
    WaveletFactory<OpBasedWavelet> waveletFactory = new WaveletFactory<OpBasedWavelet>() {
      @Override
      public OpBasedWavelet create(WaveId waveId, WaveletId waveletId, ParticipantId creator) {
        final WaveletDataImpl data = new WaveletDataImpl(
            waveletId,
            creator,
            creationTime,
            0L,
            HashedVersion.unsigned(0),
            creationTime,
            waveId,
            docFactory);
        SilentOperationSink<WaveletOperation> executor =
            new SilentOperationSink<WaveletOperation>() {
          @Override
          public void consume(WaveletOperation operation) {
            try {
              operation.apply(data);
            } catch (OperationException e) {
              throw new OperationRuntimeException("Error applying op", e);
            }
          }
        };
        return new OpBasedWavelet(waveId,
            data,
            new BasicWaveletOperationContextFactory(creator) {
              @Override public long currentTimeMillis() {
                return creationTime;
              }
            },
            ParticipationHelper.IGNORANT,
            executor,
            sink);
      }
    };
    WaveViewImpl<?> wave = WaveViewImpl.create(
        waveletFactory, waveData.getWaveId(), gen, creator, WaveletConfigurator.ADD_CREATOR);

    // Build a conversation with a root blip.
    ConversationView v = WaveBasedConversationView.create(wave, gen);
    Conversation c = v.createRoot();
    ConversationThread thread = c.getRootThread();
    thread.appendBlip();

    log.info("initial ops=" + ops);

    return ops;
  }

  public static List<WaveletOperation> userDataWaveletOps(ParticipantId user, long creationTime) {
    return Collections.<WaveletOperation>singletonList(
        new AddParticipant(new WaveletOperationContext(user, creationTime, 0L), user));
  }
}
