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

import com.google.common.base.Preconditions;
import com.google.walkaround.slob.shared.SlobId;

import java.io.Serializable;

/**
 * An entry in WaveletDirectory.  Currently, this is just an SlobId, but we
 * could add something else later (e.g., legacy WaveletName, perhaps wavelet
 * type).
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class WaveletMapping implements Serializable {
  private static final long serialVersionUID = 328650053195007979L;

  private final SlobId objectId;

  public WaveletMapping(SlobId objectId) {
    Preconditions.checkNotNull(objectId, "Null objectId");
    this.objectId = objectId;
  }

  public SlobId getObjectId() {
    return objectId;
  }

  @Override
  public String toString() {
    return "WaveletMapping(" + objectId + ")";
  }
}
