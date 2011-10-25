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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.walkaround.proto.WaveletMetadata;
import com.google.walkaround.proto.gson.UdwMetadataGsonImpl;
import com.google.walkaround.proto.gson.WaveletMetadataGsonImpl;
import com.google.walkaround.slob.server.AccessDeniedException;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.server.SlobAlreadyExistsException;
import com.google.walkaround.slob.server.SlobStore;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.shared.RandomBase64Generator;
import com.google.walkaround.wave.server.WaveletDirectory.ObjectIdAlreadyKnown;
import com.google.walkaround.wave.server.auth.StableUserId;
import com.google.walkaround.wave.server.conv.ConvStore;
import com.google.walkaround.wave.server.model.InitialOps;
import com.google.walkaround.wave.server.model.ServerMessageSerializer;
import com.google.walkaround.wave.server.udw.UdwStore;
import com.google.walkaround.wave.server.udw.UserDataWaveletDirectory;
import com.google.walkaround.wave.shared.WaveSerializer;

import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Creates wavelets.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveletCreator {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(WaveletCreator.class.getName());

  private static final WaveSerializer SERIALIZER =
      new WaveSerializer(new ServerMessageSerializer());

  private final WaveletDirectory waveletDirectory;
  private final UserDataWaveletDirectory udwDirectory;
  private final ParticipantId participantId;
  private final StableUserId userId;
  private final SlobStore convStore;
  private final SlobStore udwStore;
  private final RandomBase64Generator random64;

  @Inject
  public WaveletCreator(
      WaveletDirectory waveletDirectory,
      UserDataWaveletDirectory udwDirectory,
      ParticipantId participantId,
      StableUserId userId,
      @ConvStore SlobStore convStore,
      @UdwStore SlobStore udwStore,
      RandomBase64Generator random64) {
    this.waveletDirectory = waveletDirectory;
    this.udwDirectory = udwDirectory;
    this.participantId = participantId;
    this.userId = userId;
    this.convStore = convStore;
    this.udwStore = udwStore;
    this.random64 = random64;
  }

  private WaveletMapping registerWavelet(SlobId objectId) throws IOException {
    WaveletMapping mapping = new WaveletMapping(objectId);
    log.info("Registering " + mapping);
    try {
      waveletDirectory.register(mapping);
    } catch (ObjectIdAlreadyKnown e) {
      throw new RuntimeException("Freshly-created object already known", e);
    }
    log.info("Registered " + mapping);
    return mapping;
  }

  private String makeUdwMetadata(SlobId convId) {
    UdwMetadataGsonImpl udwMeta = new UdwMetadataGsonImpl();
    udwMeta.setAssociatedConvId(convId.getId());
    udwMeta.setOwner(userId.getId());
    WaveletMetadataGsonImpl meta = new WaveletMetadataGsonImpl();
    meta.setType(WaveletMetadata.Type.UDW);
    meta.setUdwMetadata(udwMeta);
    return GsonProto.toJson(meta);
  }

  private String makeConvMetadata() {
    WaveletMetadataGsonImpl meta = new WaveletMetadataGsonImpl();
    meta.setType(WaveletMetadata.Type.CONV);
    return GsonProto.toJson(meta);
  }

  private List<ChangeData<String>> serializeChanges(List<WaveletOperation> ops) {
    ImmutableList.Builder<ChangeData<String>> out = ImmutableList.builder();
    for (String delta : SERIALIZER.serializeDeltas(ops)) {
      out.add(new ChangeData<String>(getFakeClientId(), delta));
    }
    return out.build();
  }

  private WaveletMapping createUdw(SlobId convId) throws IOException {
    long creationTime = System.currentTimeMillis();
    List<WaveletOperation> history = InitialOps.userDataWaveletOps(participantId, creationTime);
    SlobId objectId = newUdwWithGeneratedId(makeUdwMetadata(convId), serializeChanges(history));
    return registerWavelet(objectId);
  }

  public SlobId getOrCreateUdw(SlobId convId) throws IOException {
    SlobId existing = udwDirectory.getUdwId(convId, userId);
    if (existing != null) {
      return existing;
    } else {
      SlobId newUdwId = createUdw(convId)
          .getObjectId();
      SlobId existingUdwId = udwDirectory.getOrAdd(convId, userId, newUdwId);
      if (existingUdwId == null) {
        return newUdwId;
      } else {
        log.log(Level.WARNING, "Multiple concurrent UDW creations for "
            + userId + " on " + convId + ": " + existingUdwId + ", " + newUdwId);
        return existingUdwId;
      }
    }
  }

  private ClientId getFakeClientId() {
    return new ClientId("");
  }

  public WaveletMapping createConv(SlobId objectId, @Nullable List<WaveletOperation> history)
      throws IOException, SlobAlreadyExistsException, AccessDeniedException {
    if (history == null) {
      history =
          InitialOps.conversationWaveletOps(participantId, System.currentTimeMillis(), random64);
    }
    convStore.newObject(objectId, makeConvMetadata(), serializeChanges(history));
    return registerWavelet(objectId);
  }

  private SlobId getRandomObjectId() {
    // 96 random bits.  (6 bits per base64 character.)  TODO(ohler): Justify the
    // number 96.
    return new SlobId(random64.next(96 / 6));
  }

  private SlobId newUdwWithGeneratedId(String metadata,
      List<ChangeData<String>> initialHistory) throws IOException {
    SlobId newId = getRandomObjectId();
    try {
      udwStore.newObject(newId, metadata, initialHistory);
      return newId;
    } catch (SlobAlreadyExistsException e) {
      throw new IOException("TODO(ohler): Retry with a different id: " + newId, e);
    } catch (AccessDeniedException e) {
      throw new RuntimeException("Unexpected AccessDeniedException creating udw " + newId, e);
    }
  }

  public SlobId newConvWithGeneratedId(@Nullable List<WaveletOperation> history)
      throws IOException {
    SlobId newId = getRandomObjectId();
    try {
      createConv(newId, history);
      return newId;
    } catch (SlobAlreadyExistsException e) {
      throw new IOException("TODO(ohler): Retry with a different id: " + newId, e);
    } catch (AccessDeniedException e) {
      throw new RuntimeException("Unexpected AccessDeniedException creating conv " + newId, e);
    }
  }

}
