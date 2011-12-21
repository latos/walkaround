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

import org.waveprotocol.box.search.SearchRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for SearchRequests.
 *
 * Generated from org/waveprotocol/box/search/search.proto. Do not edit.
 */
public final class SearchRequestBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    SearchRequest create();
  }

  private String query;
  private Integer index;
  private Integer numResults;
  public SearchRequestBuilder() {
  }

  public SearchRequestBuilder setQuery(String value) {
    this.query = value;
    return this;
  }

  public SearchRequestBuilder setIndex(int value) {
    this.index = value;
    return this;
  }

  public SearchRequestBuilder setNumResults(int value) {
    this.numResults = value;
    return this;
  }

  /** Builds a {@link SearchRequest} using this builder and a factory. */
  public SearchRequest build(Factory factory) {
    SearchRequest message = factory.create();
    message.setQuery(query);
    message.setIndex(index);
    message.setNumResults(numResults);
    return message;
  }

}