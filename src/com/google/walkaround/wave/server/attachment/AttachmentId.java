/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.google.walkaround.wave.server.attachment;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Attachment ID.  This is distinct from an App Engine BlobKey since code
 * outside the server shouldn't reason about BlobKeys.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class AttachmentId implements Serializable {

  private static final long serialVersionUID = 112261689375404053L;

  private final String id;

  public AttachmentId(String id) {
    this.id = checkNotNull(id, "Null id");
  }

  public String getId() {
    return id;
  }

  @Override public String toString() {
    return getClass().getSimpleName() + "(" + id + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof AttachmentId)) { return false; }
    AttachmentId other = (AttachmentId) o;
    return Objects.equal(id, other.id);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(id);
  }

}
