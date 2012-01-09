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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.SlobId;

/**
 * POJO form of {@code ObjectSessionProto}.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ObjectSession {

  private final SlobId objectId;
  private final ClientId clientId;
  private final String storeType;

  public ObjectSession(SlobId objectId, ClientId clientId, String storeType) {
    Preconditions.checkNotNull(objectId, "Null objectId");
    Preconditions.checkNotNull(clientId, "Null clientId");
    Preconditions.checkNotNull(storeType, "Null storeType");
    this.objectId = objectId;
    this.clientId = clientId;
    this.storeType = storeType;
  }

  public SlobId getObjectId() {
    return objectId;
  }

  public ClientId getClientId() {
    return clientId;
  }

  public String getStoreType() {
    return storeType;
  }

  @Override public String toString() {
    return "ObjectSession(" + objectId + ", " + clientId + ", " + storeType + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof ObjectSession)) { return false; }
    ObjectSession other = (ObjectSession) o;
    return Objects.equal(objectId, other.objectId)
        && Objects.equal(clientId, other.clientId)
        && Objects.equal(storeType, other.storeType);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(objectId, clientId, storeType);
  }

}
