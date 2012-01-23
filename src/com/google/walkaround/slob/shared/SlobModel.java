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

package com.google.walkaround.slob.shared;

import javax.annotation.Nullable;

import java.util.List;

/**
 * Represents a domain of shared live objects (slobs).
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(danilatos): Have deserialize/serialize operation methods,
// and pass reified ops into the other methods like apply() & transform.
public interface SlobModel {

  /**
   * Read access to an object in this domain.
   */
  interface ReadableSlob {
    /**
     * Returns a serialized form of the object. Can be null if the object has no
     * history.
     */
    @Nullable String snapshot();
  }

  /**
   * Read/write access to an object in this domain.
   */
  interface Slob extends ReadableSlob {
    /**
     * Applies a delta to the object.
     *
     * @throws ChangeRejected if the change is invalid. If
     *         {@link ChangeRejected} is thrown, the object MUST remain in the
     *         previous state, ready for application of a valid operation.
     */
    void apply(ChangeData<String> payload) throws ChangeRejected;
  }

  /**
   * Creates an object belonging to this domain.
   *
   * @param snapshot initial snapshot from which to deserialize. null if this is a
   *        completely new object.
   */
  Slob create(@Nullable String snapshot) throws InvalidSnapshot;

  /**
   * Transforms operations on objects in this domain.
   *
   * @return the transformed client payloads (they will need to be re-wrapped).
   *
   * @throws ChangeRejected if the client ops were invalid or not compatible
   *           with eachother. The server ops are guaranteed to have passed
   *           through the apply() method successfully and therefore are known
   *           to be valid.
   */
  List<String> transform(List<ChangeData<String>> clientOps, List<ChangeData<String>> serverOps)
      throws ChangeRejected;
}
