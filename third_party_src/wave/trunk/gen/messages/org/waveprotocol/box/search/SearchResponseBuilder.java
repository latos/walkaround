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

import org.waveprotocol.box.search.SearchResponse.Digest;
import org.waveprotocol.box.search.SearchResponseBuilder.DigestBuilder;
import org.waveprotocol.box.search.SearchResponseUtil;
import org.waveprotocol.box.search.SearchResponseUtil.DigestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for SearchResponses.
 *
 * Generated from org/waveprotocol/box/search/search.proto. Do not edit.
 */
public final class SearchResponseBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    SearchResponse create();
  }

  public static final class DigestBuilder {

    /** Factory to pass to {@link #build()}. */
    public interface Factory {
      Digest create();
    }

    private String title;
    private String snippet;
    private String waveId;
    private Long lastModified;
    private Integer unreadCount;
    private Integer blipCount;
    private final List<String> participants = new ArrayList<String>();
    private String author;
    public DigestBuilder() {
    }

    public DigestBuilder setTitle(String value) {
      this.title = value;
      return this;
    }

    public DigestBuilder setSnippet(String value) {
      this.snippet = value;
      return this;
    }

    public DigestBuilder setWaveId(String value) {
      this.waveId = value;
      return this;
    }

    public DigestBuilder setLastModified(long value) {
      this.lastModified = value;
      return this;
    }

    public DigestBuilder setUnreadCount(int value) {
      this.unreadCount = value;
      return this;
    }

    public DigestBuilder setBlipCount(int value) {
      this.blipCount = value;
      return this;
    }

    public DigestBuilder addAllParticipants(List<String> values) {
      this.participants.addAll(values);
      return this;
    }

    public DigestBuilder setParticipants(int n, String value) {
      this.participants.set(n, value);
      return this;
    }

    public DigestBuilder addParticipants(String value) {
      this.participants.add(value);
      return this;
    }

    public DigestBuilder clearParticipants() {
      participants.clear();
      return this;
    }

    public DigestBuilder setAuthor(String value) {
      this.author = value;
      return this;
    }

    /** Builds a {@link Digest} using this builder and a factory. */
    public Digest build(Factory factory) {
      Digest message = factory.create();
      message.setTitle(title);
      message.setSnippet(snippet);
      message.setWaveId(waveId);
      message.setLastModified(lastModified);
      message.setUnreadCount(unreadCount);
      message.setBlipCount(blipCount);
      message.clearParticipants();
      message.addAllParticipants(participants);
      message.setAuthor(author);
      return message;
    }

  }

  private String query;
  private Integer totalResults;
  private final List<Digest> digests = new ArrayList<Digest>();
  public SearchResponseBuilder() {
  }

  public SearchResponseBuilder setQuery(String value) {
    this.query = value;
    return this;
  }

  public SearchResponseBuilder setTotalResults(int value) {
    this.totalResults = value;
    return this;
  }

  public SearchResponseBuilder addAllDigests(List<? extends Digest> messages) {
    for (Digest message : messages) {
      addDigests(message);
    }
    return this;
  }

  public SearchResponseBuilder setDigests(int n, Digest message) {
    this.digests.set(n, message);
    return this;
  }

  public SearchResponseBuilder addDigests(Digest message) {
    this.digests.add(message);
    return this;
  }

  public SearchResponseBuilder clearDigests() {
    digests.clear();
    return this;
  }

  /** Builds a {@link SearchResponse} using this builder and a factory. */
  public SearchResponse build(Factory factory) {
    SearchResponse message = factory.create();
    message.setQuery(query);
    message.setTotalResults(totalResults);
    message.clearDigests();
    message.addAllDigests(digests);
    return message;
  }

}