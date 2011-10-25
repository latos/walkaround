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

package com.google.walkaround.wave.shared;

import com.google.common.base.Preconditions;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.shared.RandomBase64Generator;

import org.waveprotocol.wave.model.id.IdConstants;
import org.waveprotocol.wave.model.id.IdGenerator;
import org.waveprotocol.wave.model.id.SimplePrefixEscaper;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;

/**
 * Hacks pertaining to wave/wavelet ids.
 *
 * On the client, wave IDs are derived from a fixed domain and the object id of
 * the conversational wavelet.  Wavelet IDs are derived from the same fixed
 * domain and the object id of the wavelet (conversational or udw).
 *
 * On the server, we use FAKE_WAVELET_NAME.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class IdHack {

  public static class StubIdGenerator implements IdGenerator {
    @Override public WaveId newWaveId() {
      throw new AssertionError("Not implemented");
    }

    @Override public WaveletId newConversationWaveletId() {
      throw new AssertionError("Not implemented");
    }

    @Override public WaveletId newConversationRootWaveletId() {
      throw new AssertionError("Not implemented");
    }

    @Override public WaveletId newUserDataWaveletId(String address) {
      throw new AssertionError("Not implemented");
    }

    @Override public String newBlipId() {
      throw new AssertionError("Not implemented");
    }

    @Deprecated @Override public String peekBlipId() {
      throw new AssertionError("Not implemented");
    }

    @Override public String newDataDocumentId() {
      throw new AssertionError("Not implemented");
    }

    @Override public String newUniqueToken() {
      throw new AssertionError("Not implemented");
    }

    @Override public String newId(String namespace) {
      throw new AssertionError("Not implemented");
    }

    @Override public String getDefaultDomain() {
      throw new AssertionError("Not implemented");
    }
  }

  public static final int BLIP_ID_LENGTH_CHARS = 8;

  public static class MinimalIdGenerator extends StubIdGenerator {
    private final WaveletId convRootId;
    private final WaveletId udwId;
    private final RandomBase64Generator random64;

    private String nextBlipId = null;

    public MinimalIdGenerator(WaveletId convRootId, WaveletId udwId,
        RandomBase64Generator random64) {
      Preconditions.checkNotNull(convRootId, "Null convRootId");
      Preconditions.checkNotNull(udwId, "Null udwId");
      Preconditions.checkNotNull(random64, "Null random64");
      this.convRootId = convRootId;
      this.udwId = udwId;
      this.random64 = random64;
    }

    @Override public String toString() {
      return "MinimalIdGenerator(" + convRootId + ", " + udwId + ")";
    }

    @Override public String newBlipId() {
      String ret = peekBlipId();
      nextBlipId = null;
      return ret;
    }

    @Deprecated // TODO(danilatos): Check to see if we can fix the code that calls this.
    @Override
    public String peekBlipId() {
      if (nextBlipId == null) {
        nextBlipId = createBlipId();
      }
      return nextBlipId;
    }

    private String createBlipId() {
      return joinTokens(IdConstants.BLIP_PREFIX, random64.next(BLIP_ID_LENGTH_CHARS));
    }

    // TODO(ohler): Rename this method to getConversationRootWaveletId().
    @Override public WaveletId newConversationRootWaveletId() {
      return convRootId;
    }

    // TODO(ohler): Rename this method to getUserDataWaveletId().
    @Override public WaveletId newUserDataWaveletId(String address) {
      return udwId;
    }
  }

  // TODO(ohler): inject
  private static final String DOMAIN = "walkaround";

  private static String joinTokens(String... tokens) {
    return SimplePrefixEscaper.DEFAULT_ESCAPER.join(IdConstants.TOKEN_SEPARATOR, tokens);
  }

  private static String[] splitTokens(String s) {
    return SimplePrefixEscaper.DEFAULT_ESCAPER.split(IdConstants.TOKEN_SEPARATOR, s);
  }

  /**
   * This is the UDW id that the client uses internally if UDWs are disabled.
   */
  public static final WaveletId DISABLED_UDW_ID =
      WaveletId.of("disabledudwdomain.invalid", "disabledudwid");

  /**
   * The wavelet name that the object store model uses.
   */
  public static final WaveletName FAKE_WAVELET_NAME =
      WaveletName.of(
          WaveId.of("fakewavedomain.invalid", "fakewaveid"),
          WaveletId.of("fakewaveletdomain.invalid", "fakewaveletid"));

  public static WaveId waveIdFromConvObjectId(SlobId convObjectId) {
    return WaveId.of(DOMAIN,
        joinTokens(IdConstants.WAVE_PREFIX, convObjectId.getId()));
  }

  public static WaveletId convWaveletIdFromObjectId(SlobId objectId) {
    return WaveletId.of(DOMAIN,
        joinTokens(IdConstants.CONVERSATION_WAVELET_PREFIX, objectId.getId()));
  }

  public static WaveletId udwWaveletIdFromObjectId(SlobId objectId) {
    return WaveletId.of(DOMAIN,
        joinTokens(IdConstants.USER_DATA_WAVELET_PREFIX, objectId.getId()));
  }

  public static WaveletName convWaveletNameFromConvObjectId(SlobId convObjectId) {
    return WaveletName.of(
        waveIdFromConvObjectId(convObjectId),
        convWaveletIdFromObjectId(convObjectId));
  }

  public static WaveletName udwWaveletNameFromConvObjectIdAndUdwObjectId(
      SlobId convObjectId, SlobId udwObjectId) {
    return WaveletName.of(
        waveIdFromConvObjectId(convObjectId), udwWaveletIdFromObjectId(udwObjectId));
  }

  public static SlobId objectIdFromWaveletId(WaveletId waveletId) {
    String[] tokens = splitTokens(waveletId.getId());
    Preconditions.checkArgument(tokens.length == 2,
        "Wavelet id does not consist of two tokens: %s", waveletId);
    if (IdConstants.CONVERSATION_WAVELET_PREFIX.equals(tokens[0])
        || IdConstants.USER_DATA_WAVELET_PREFIX.equals(tokens[0])) {
      return new SlobId(tokens[1]);
    } else {
      throw new RuntimeException("Unknown prefix in wavelet id: " + waveletId);
    }
  }

}
