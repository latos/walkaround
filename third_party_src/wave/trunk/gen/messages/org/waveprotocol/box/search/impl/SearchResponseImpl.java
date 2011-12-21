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

package org.waveprotocol.box.search.impl;

import org.waveprotocol.box.search.SearchResponse.Digest;
import org.waveprotocol.box.search.impl.SearchResponseImpl.DigestImpl;
import org.waveprotocol.box.search.SearchResponse;
import org.waveprotocol.box.search.SearchResponseUtil;
import org.waveprotocol.box.search.SearchResponse.Digest;
import org.waveprotocol.box.search.SearchResponseUtil.DigestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of SearchResponse.
 *
 * Generated from org/waveprotocol/box/search/search.proto. Do not edit.
 */
public class SearchResponseImpl implements SearchResponse {

  public static class DigestImpl implements Digest {
    private String title;
    private String snippet;
    private String waveId;
    private Long lastModified;
    private Integer unreadCount;
    private Integer blipCount;
    private final List<String> participants = new ArrayList<String>();
    private String author;
    public DigestImpl() {
    }

    public DigestImpl(Digest message) {
      copyFrom(message);
    }

    @Override
    public void copyFrom(Digest message) {
      setTitle(message.getTitle());
      setSnippet(message.getSnippet());
      setWaveId(message.getWaveId());
      setLastModified(message.getLastModified());
      setUnreadCount(message.getUnreadCount());
      setBlipCount(message.getBlipCount());
      clearParticipants();
      for (String field : message.getParticipants()) {
        addParticipants(field);
      }
      setAuthor(message.getAuthor());
    }

    @Override
    public String getTitle() {
      return title;
    }

    @Override
    public void setTitle(String value) {
      this.title = value;
    }

    @Override
    public String getSnippet() {
      return snippet;
    }

    @Override
    public void setSnippet(String value) {
      this.snippet = value;
    }

    @Override
    public String getWaveId() {
      return waveId;
    }

    @Override
    public void setWaveId(String value) {
      this.waveId = value;
    }

    @Override
    public long getLastModified() {
      return lastModified;
    }

    @Override
    public void setLastModified(long value) {
      this.lastModified = value;
    }

    @Override
    public int getUnreadCount() {
      return unreadCount;
    }

    @Override
    public void setUnreadCount(int value) {
      this.unreadCount = value;
    }

    @Override
    public int getBlipCount() {
      return blipCount;
    }

    @Override
    public void setBlipCount(int value) {
      this.blipCount = value;
    }

    @Override
    public List<String> getParticipants() {
      return Collections.unmodifiableList(participants);
    }

    @Override
    public void addAllParticipants(List<String> values) {
      this.participants.addAll(values);
    }

    @Override
    public String getParticipants(int n) {
      return participants.get(n);
    }

    @Override
    public void setParticipants(int n, String value) {
      this.participants.set(n, value);
    }

    @Override
    public int getParticipantsSize() {
      return participants.size();
    }

    @Override
    public void addParticipants(String value) {
      this.participants.add(value);
    }

    @Override
    public void clearParticipants() {
      participants.clear();
    }

    @Override
    public String getAuthor() {
      return author;
    }

    @Override
    public void setAuthor(String value) {
      this.author = value;
    }

    /** Provided to subclasses to clear all fields, for example when deserializing. */
    protected void reset() {
      this.title = null;
      this.snippet = null;
      this.waveId = null;
      this.lastModified = null;
      this.unreadCount = null;
      this.blipCount = null;
      this.participants.clear();
      this.author = null;
    }

    @Override
    public boolean equals(Object o) {
      return (o instanceof DigestImpl) && isEqualTo(o);
    }

    @Override
    public boolean isEqualTo(Object o) {
      if (o == this) {
        return true;
      } else if (o instanceof Digest) {
        return DigestUtil.isEqual(this, (Digest) o);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return DigestUtil.getHashCode(this);
    }

  }

  private String query;
  private Integer totalResults;
  private final List<DigestImpl> digests = new ArrayList<DigestImpl>();
  public SearchResponseImpl() {
  }

  public SearchResponseImpl(SearchResponse message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(SearchResponse message) {
    setQuery(message.getQuery());
    setTotalResults(message.getTotalResults());
    clearDigests();
    for (Digest field : message.getDigests()) {
      addDigests(new DigestImpl(field));
    }
  }

  @Override
  public String getQuery() {
    return query;
  }

  @Override
  public void setQuery(String value) {
    this.query = value;
  }

  @Override
  public int getTotalResults() {
    return totalResults;
  }

  @Override
  public void setTotalResults(int value) {
    this.totalResults = value;
  }

  @Override
  public List<DigestImpl> getDigests() {
    return Collections.unmodifiableList(digests);
  }

  @Override
  public void addAllDigests(List<? extends Digest> messages) {
    for (Digest message : messages) {
      addDigests(message);
    }
  }

  @Override
  public DigestImpl getDigests(int n) {
    return new DigestImpl(digests.get(n));
  }

  @Override
  public void setDigests(int n, Digest message) {
    this.digests.set(n, new DigestImpl(message));
  }

  @Override
  public int getDigestsSize() {
    return digests.size();
  }

  @Override
  public void addDigests(Digest message) {
    this.digests.add(new DigestImpl(message));
  }

  @Override
  public void clearDigests() {
    digests.clear();
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.query = null;
    this.totalResults = null;
    this.digests.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof SearchResponseImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof SearchResponse) {
      return SearchResponseUtil.isEqual(this, (SearchResponse) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return SearchResponseUtil.getHashCode(this);
  }

}