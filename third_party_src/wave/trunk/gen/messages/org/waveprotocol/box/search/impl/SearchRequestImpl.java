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

import org.waveprotocol.box.search.SearchRequest;
import org.waveprotocol.box.search.SearchRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of SearchRequest.
 *
 * Generated from org/waveprotocol/box/search/search.proto. Do not edit.
 */
public class SearchRequestImpl implements SearchRequest {
  private String query;
  private Integer index;
  private Integer numResults;
  public SearchRequestImpl() {
  }

  public SearchRequestImpl(SearchRequest message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(SearchRequest message) {
    setQuery(message.getQuery());
    setIndex(message.getIndex());
    setNumResults(message.getNumResults());
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
  public int getIndex() {
    return index;
  }

  @Override
  public void setIndex(int value) {
    this.index = value;
  }

  @Override
  public int getNumResults() {
    return numResults;
  }

  @Override
  public void setNumResults(int value) {
    this.numResults = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.query = null;
    this.index = null;
    this.numResults = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof SearchRequestImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof SearchRequest) {
      return SearchRequestUtil.isEqual(this, (SearchRequest) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return SearchRequestUtil.getHashCode(this);
  }

}