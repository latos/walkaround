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

package com.google.walkaround.wave.server.wavemanager;

import com.google.common.collect.ImmutableList;
import com.google.common.net.UriEscapers;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.wave.server.gxp.InboxDisplayRecord;
import com.google.walkaround.wave.server.index.WaveIndexer;
import com.google.walkaround.wave.server.index.WaveIndexer.UserIndexEntry;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.IOException;
import java.util.List;

/**
 * Utility for making searches and getting the results in a format suitable for
 * display.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class Searcher {
  @Inject WaveIndexer userIndex;
  @Inject ParticipantId participantId;

  private class Search {
    private final String query;
    private final int offset;
    private final int limit;

    Search(String query, int offset, int limit) {
      this.query = query;
      this.offset = offset;
      this.limit = limit;
    }

    private List<InboxDisplayRecord> getWavesInner() throws IOException {
      ImmutableList.Builder<InboxDisplayRecord> out = ImmutableList.builder();
      List<UserIndexEntry> waves = userIndex.findWaves(participantId, query, offset, limit);
      for (UserIndexEntry wave : waves) {
        out.add(new InboxDisplayRecord(
            // TODO(danilatos): Retrieve contact details if possible and use name not address.
            wave.getCreator().getAddress(),
            wave.getTitle().trim(),
            // TODO(danilatos): Detect if the snippet redundantly starts with the title,
            // and strip it out, if we are just displaying a folder (but don't if we
            // are displaying the context of a search query).
            wave.getSnippetHtml().trim(),
            "" + new LocalDate(new Instant(wave.getLastModifiedMillis())),
            makeWaveLink(wave.getObjectId())));
      }
      return out.build();
    }

    List<InboxDisplayRecord> getWaves() throws IOException {
      try {
        return new RetryHelper().run(
            new RetryHelper.Body<List<InboxDisplayRecord>>() {
              @Override public List<InboxDisplayRecord> run()
                  throws RetryableFailure {
                try {
                  return getWavesInner();
                } catch (IOException e) {
                  throw new RetryableFailure(e);
                }
              }
            });
      } catch (PermanentFailure e) {
        throw new IOException("PermanentFailure reading index", e);
      }
    }
  }

  public List<InboxDisplayRecord> searchWaves(String query, int offset, int limit)
      throws IOException {
    return new Search(query, offset, limit).getWaves();
  }

  public static String makeWaveLink(SlobId objectId) {
    return "/wave?id=" + queryEscape(objectId.getId());
  }

  private static String queryEscape(String s) {
    return UriEscapers.uriQueryStringEscaper(false).escape(s);
  }
}
