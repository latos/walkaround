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

package com.google.walkaround.slob.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.walkaround.proto.ServerMutateRequest;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.SlobId;

import org.waveprotocol.wave.model.util.Pair;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * Interactions with the shared live object (slob) store.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public interface SlobStore {

  /**
   * Provides a connection for dealing with an object, along with a snapshot of
   * the object state.
   */
  Pair<ConnectResult, String> connect(SlobId slobId, ClientId clientId)
      throws SlobNotFoundException, IOException, AccessDeniedException;

  /**
   * Connects without returning a snapshot.
   */
  ConnectResult reconnect(SlobId slobId, ClientId clientId)
      throws SlobNotFoundException, IOException, AccessDeniedException;

  // Returns a snapshot.  TODO(ohler): make snapshot type a generic parameter or something
  String loadAtVersion(SlobId slobId, @Nullable Long version)
      throws SlobNotFoundException, IOException, AccessDeniedException;

  /**
   * Loads the history of an object. If the history is large, not all requested
   * data may be returned, but the result object will contain this information;
   * the caller should then ask again for the next chunk. The returned data will
   * always begin at startVersion and be contiguous.
   *
   * @param startVersion The version of the object before the first change to
   *        return.
   * @param endVersion The version of the object after the last change to
   *        return, or null for current version.
   * @return the historical mutations, as many as could be retrieved in one go.
   * @throws SlobNotFoundException if the object is not found
   */
  HistoryResult loadHistory(SlobId slobId, long startVersion, @Nullable Long endVersion)
      throws SlobNotFoundException, IOException, AccessDeniedException;

  /**
   * Processes the given mutate request.
   */
  MutateResult mutateObject(ServerMutateRequest req)
      throws SlobNotFoundException, IOException, AccessDeniedException;

  /**
   * Creates a new object.
   */
  void newObject(SlobId slobId, String metadata, List<ChangeData<String>> initialHistory)
      throws SlobAlreadyExistsException, IOException, AccessDeniedException;

  /** Result of a history fetch. */
  final class HistoryResult {
    private final ImmutableList<ChangeData<String>> data;
    private final boolean hasMore;

    public HistoryResult(ImmutableList<ChangeData<String>> data,
        boolean hasMore) {
      Preconditions.checkNotNull(data, "Null data");
      this.data = data;
      this.hasMore = hasMore;
    }

    public ImmutableList<ChangeData<String>> getData() {
      return data;
    }

    public boolean hasMore() {
      return hasMore;
    }

    @Override public String toString() {
      return "HistoryResult(" + data + ", " + hasMore + ")";
    }
  }

  /**
   * Information used to establish a live connection to the specified object.
   */
  final class ConnectResult {
    // Null means no live connection is possible.
    @Nullable private final String channelToken;
    private final long version;

    public ConnectResult(@Nullable String channelToken, long version) {
      this.channelToken = channelToken;
      this.version = version;
    }

    @Nullable public String getChannelToken() {
      return channelToken;
    }

    public long getVersion() {
      return version;
    }

    @Override public String toString() {
      return "ConnectResult(" + channelToken + ", " + version + ")";
    }
  }

}
