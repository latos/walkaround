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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Type-safe wrapper around a raw object id.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public final class SlobId implements Serializable {

  private final String id;

  public SlobId(String id) {
    this.id = checkNotNull(id, "Null id");
  }

  public String getId() {
    return id;
  }

  @Override public String toString() {
    return "SlobId(" + id + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof SlobId)) { return false; }
    SlobId other = (SlobId) o;
    return Objects.equal(id, other.id);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(id);
  }

}
