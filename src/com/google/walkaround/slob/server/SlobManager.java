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

import com.google.walkaround.slob.shared.SlobId;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(ohler): eliminate this class.
public interface SlobManager {

  public static final class SlobIndexUpdate {
    @Nullable private final String indexedHtml;
    @Nullable private final Boolean viewed;

    /**
     * @param indexedHtml optionally update the indexed html
     * @param viewed optionally update the viewed state
     */
    public SlobIndexUpdate(@Nullable String indexedHtml, @Nullable Boolean viewed) {
      this.indexedHtml = indexedHtml;
      this.viewed = viewed;
    }

    @Override public String toString() {
      return "SlobIndexUpdate(" + (indexedHtml != null ? indexedHtml.length() : "null") + ","
          + viewed + ")";
    }

    @Nullable public String getIndexedHtml() {
      return indexedHtml;
    }

    @Nullable public Boolean getViewed() {
      return viewed;
    }
  }

  void update(SlobId objectId, SlobIndexUpdate update) throws IOException;

}
