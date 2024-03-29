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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.io.Serializable;

/**
 * A client ID.  See walkaround.proto for more information.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public final class ClientId implements Serializable {

  private final String id;

  public ClientId(String id) {
    Preconditions.checkNotNull(id, "Null id");
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override public String toString() {
    return "ClientId(" + id + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof ClientId)) { return false; }
    ClientId other = (ClientId) o;
    return Objects.equal(id, other.id);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(id);
  }

}
