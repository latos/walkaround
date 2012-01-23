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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ChangeRejected;
import com.google.walkaround.slob.shared.InvalidSnapshot;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobModel;
import com.google.walkaround.wave.shared.IdHack;
import com.google.walkaround.wave.shared.MessageSerializer;
import com.google.walkaround.wave.shared.WaveSerializer;
import com.google.walkaround.wave.shared.WaveletUtil;

import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.operation.OperationPair;
import org.waveprotocol.wave.model.operation.TransformException;
import org.waveprotocol.wave.model.operation.wave.Transform;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;

import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class WaveObjectStoreModel implements SlobModel {

  // TODO(ohler): Replace these methods with getIndexEntry().
  public interface ReadableWaveletObject extends SlobModel.ReadableSlob {
    ParticipantId getCreator();
    // TODO(ohler): Define separate models for conv wavelets and UDWs; this
    // stuff makes no sense on UDWs.
    String getTitle();
    String getSnippet();
    long getLastModifiedMillis();
    List<ParticipantId> getParticipants();
  }

  private class WaveletObject implements ReadableWaveletObject, SlobModel.Slob {

    /**
     * Wavelet state. Created from first delta, then mutated in {@link #apply}.
     * Never cleared.
     */
    private WaveletDataImpl wavelet;

    private String cachedSnapshot = null;

    public WaveletObject(WaveletDataImpl initialState) {
      this.wavelet = initialState;
    }

    @Override @Nullable
    public String snapshot() {
      if (wavelet == null) {
        return null;
      }
      if (cachedSnapshot == null) {
        cachedSnapshot = serializer.serializeWavelet(wavelet);
      }
      return cachedSnapshot;
    }

    @Override
    public void apply(ChangeData<String> change) throws ChangeRejected {
      cachedSnapshot = null;
      WaveletOperation op;
      try {
        op = serializer.deserializeDelta(change.getPayload());
      } catch (MessageException e) {
        throw new ChangeRejected("Malformed op: " + change, e);
      }

      if (wavelet == null) {
        try {
          wavelet = WaveletUtil.buildWaveletFromInitialOps(
              IdHack.FAKE_WAVELET_NAME, Collections.singletonList(op));
        } catch (OperationException e) {
          throw new ChangeRejected("Invalid initial op: " + op, e);
        }
      } else {
        try {
          op.apply(wavelet);
        } catch (OperationException e) {
          // Operation failed. The wavelet is still intact, however, so just
          // report the failure and continue on.
          throw new ChangeRejected("Invalid op: " + op, e);
        }
      }
    }

    // TODO(ohler): Guarantee that the only method that will ever be called at
    // version 0 is apply().
    @Override public ParticipantId getCreator() {
      return wavelet.getCreator();
    }

    @Override public String getTitle() {
      // TODO(ohler): extract title
      return "";
    }

    // Getting a snippet as a function of just the current state of the wave is
    // not what we really want; we should be extracting a snippet from the wave
    // based on the search query, or based on which blips the user hasn't read
    // yet.  TODO(ohler): redo this when we integrate with full text search.
    @Override public String getSnippet() {
      return TextRenderer.renderToText(wavelet);
    }

    @Override public long getLastModifiedMillis() {
      return wavelet.getLastModifiedTime();
    }

    @Override public List<ParticipantId> getParticipants() {
      return ImmutableList.copyOf(wavelet.getParticipants());
    }
  }

  private final WaveSerializer serializer;

  @Inject
  public WaveObjectStoreModel(MessageSerializer messageSerializer) {
    this.serializer = new WaveSerializer(messageSerializer);
  }

  @Override
  public Slob create(String snapshot) throws InvalidSnapshot {
    try {
      return new WaveletObject(snapshot == null ? null
          : serializer.deserializeWavelet(IdHack.FAKE_WAVELET_NAME, snapshot));
    } catch (MessageException e) {
      throw new InvalidSnapshot(e);
    }
  }

  @Override
  public List<String> transform(
      List<ChangeData<String>> clientChanges, List<ChangeData<String>> serverChanges)
      throws ChangeRejected {
    try {
      WaveletOperation[] clientOps = deserializeOps(clientChanges);
      WaveletOperation[] serverOps = serverOps(serverChanges);

      for (WaveletOperation serverOp : serverOps) {
        for (int i = 0; i < clientOps.length; i++) {
          OperationPair<WaveletOperation> pair = Transform.transform(clientOps[i], serverOp);
          serverOp = pair.serverOp();
          clientOps[i] = pair.clientOp();
        }
      }

      List<String> ret = Lists.newArrayListWithCapacity(clientOps.length);
      for (int i = 0; i < clientOps.length; i++) {
        ret.add(serializer.serializeDelta(clientOps[i]));
      }

      return ret;
    } catch (TransformException e) {
      throw new ChangeRejected(e);
    } catch (MessageException e) {
      throw new ChangeRejected(e);
    }
  }

  // Server ops aren't expected to fail deserialization.
  private WaveletOperation[] serverOps(List<ChangeData<String>> serverChanges) {
    try {
      return deserializeOps(serverChanges);
    } catch (MessageException e) {
      throw new IllegalArgumentException("Malformed server op(s) " + serverChanges, e);
    }
  }

  private WaveletOperation[] deserializeOps(List<ChangeData<String>> changes)
      throws MessageException {
    WaveletOperation[] ops = new WaveletOperation[changes.size()];
    for (int i = 0; i < changes.size(); i++) {
      ChangeData<String> delta = changes.get(i);
      ops[i] = serializer.deserializeDelta(delta.getPayload());
    }
    return ops;
  }
}
