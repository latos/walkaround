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

package com.google.walkaround.wave.client.rpc;

import com.google.common.base.Preconditions;
import com.google.walkaround.proto.ObjectSessionProto;
import com.google.walkaround.slob.shared.SlobId;

import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.StringMap;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;

import javax.annotation.Nullable;

/**
 * Registry of wavelet data for current wave.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveletMap {

  public static class WaveletEntry {
    private final SlobId objectId;
    // This is the serialized JSON form of the SignedObjectSession protobuf.  We
    // store it in serialized form to avoid the cost of re-serializing it for
    // every request that we send.
    private final String signedSessionString;
    private final ObjectSessionProto session;
    // Null means we can't get a live stream of updates for this object.
    @Nullable private final String channelToken;
    private final WaveletDataImpl waveletState;

    public WaveletEntry(SlobId objectId,
        String signedSessionString,
        ObjectSessionProto session,
        @Nullable String channelToken,
        WaveletDataImpl waveletState) {
      Preconditions.checkNotNull(objectId, "Null objectId");
      Preconditions.checkNotNull(signedSessionString, "Null signedSessionString");
      Preconditions.checkNotNull(session, "Null session");
      Preconditions.checkNotNull(waveletState, "Null waveletState");
      this.objectId = objectId;
      this.signedSessionString = signedSessionString;
      this.session = session;
      this.channelToken = channelToken;
      this.waveletState = waveletState;
    }

    public SlobId getObjectId() {
      return objectId;
    }

    public String getSignedSessionString() {
      return signedSessionString;
    }

    public ObjectSessionProto getSession() {
      return session;
    }

    @Nullable public String getChannelToken() {
      return channelToken;
    }

    public WaveletDataImpl getWaveletState() {
      return waveletState;
    }

    @Override public String toString() {
      return "WaveletEntry(" + objectId + ", " + signedSessionString + ", "
          + session + ", " + channelToken + ", " + waveletState + ")";
    }
  }

  public interface Proc {
    void wavelet(WaveletEntry data);
  }

  private final StringMap<WaveletEntry> wavelets = CollectionUtils.createStringMap();

  /**
   * Updates the data for the given wavelet (the id is part of the data)
   */
  public void updateData(WaveletEntry newEntry) {
    wavelets.put(newEntry.getObjectId().getId(), newEntry);
  }

  public WaveletEntry get(SlobId id) {
    return wavelets.get(id.getId());
  }

  public boolean contains(SlobId id) {
    return wavelets.containsKey(id.getId());
  }

  public void each(final Proc proc) {
    wavelets.each(new StringMap.ProcV<WaveletEntry>() {
      @Override public void apply(String key, WaveletEntry data) {
        proc.wavelet(data);
      }
    });
  }
}
