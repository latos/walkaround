/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.waveprotocol.box.search;

import org.waveprotocol.box.search.SearchResponse.*;
import org.waveprotocol.box.search.SearchResponse.Digest;
import org.waveprotocol.box.search.SearchResponseUtil.DigestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.Iterator;
import java.util.List;

/**
 * Compares {@link SearchResponse}s for equality.
 *
 * Generated from org/waveprotocol/box/search/search.proto. Do not edit.
 */
public final class SearchResponseUtil {

  public static final class DigestUtil {
    private DigestUtil() {
    }

    /** Returns true if m1 and m2 are structurally equal. */
    public static boolean isEqual(Digest m1, Digest m2) {
      if (!m1.getTitle().equals(m2.getTitle())) return false;
      if (!m1.getSnippet().equals(m2.getSnippet())) return false;
      if (!m1.getWaveId().equals(m2.getWaveId())) return false;
      if (m1.getLastModified() != m2.getLastModified()) return false;
      if (m1.getUnreadCount() != m2.getUnreadCount()) return false;
      if (m1.getBlipCount() != m2.getBlipCount()) return false;
      if (!m1.getParticipants().equals(m2.getParticipants())) return false;
      if (!m1.getAuthor().equals(m2.getAuthor())) return false;
      return true;
    }

    /** Returns true if m1 and m2 are equal according to isEqual. */
    public static boolean areAllEqual(List<? extends Digest> m1,
    List<? extends Digest> m2) {
      if (m1.size() != m2.size()) return false;
      Iterator<? extends Digest> i1 = m1.iterator();
      Iterator<? extends Digest> i2 = m2.iterator();
      while (i1.hasNext()) {
        if (!isEqual(i1.next(), i2.next())) return false;
      }
      return true;
    }

    /** Returns a structural hash code of message. */
    public static int getHashCode(Digest message) {
      int result = 1;
      result = (31 * result) + message.getTitle().hashCode();
      result = (31 * result) + message.getSnippet().hashCode();
      result = (31 * result) + message.getWaveId().hashCode();
      result = (31 * result) + Long.valueOf(message.getLastModified()).hashCode();
      result = (31 * result) + Integer.valueOf(message.getUnreadCount()).hashCode();
      result = (31 * result) + Integer.valueOf(message.getBlipCount()).hashCode();
      result = (31 * result) + message.getParticipants().hashCode();
      result = (31 * result) + message.getAuthor().hashCode();
      return result;
    }

  }

  private SearchResponseUtil() {
  }

  /** Returns true if m1 and m2 are structurally equal. */
  public static boolean isEqual(SearchResponse m1, SearchResponse m2) {
    if (!m1.getQuery().equals(m2.getQuery())) return false;
    if (m1.getTotalResults() != m2.getTotalResults()) return false;
    if (!DigestUtil.areAllEqual(m1.getDigests(), m2.getDigests())) return false;
    return true;
  }

  /** Returns true if m1 and m2 are equal according to isEqual. */
  public static boolean areAllEqual(List<? extends SearchResponse> m1,
  List<? extends SearchResponse> m2) {
    if (m1.size() != m2.size()) return false;
    Iterator<? extends SearchResponse> i1 = m1.iterator();
    Iterator<? extends SearchResponse> i2 = m2.iterator();
    while (i1.hasNext()) {
      if (!isEqual(i1.next(), i2.next())) return false;
    }
    return true;
  }

  /** Returns a structural hash code of message. */
  public static int getHashCode(SearchResponse message) {
    int result = 1;
    result = (31 * result) + message.getQuery().hashCode();
    result = (31 * result) + Integer.valueOf(message.getTotalResults()).hashCode();
    result = (31 * result) + message.getDigests().hashCode();
    return result;
  }

}