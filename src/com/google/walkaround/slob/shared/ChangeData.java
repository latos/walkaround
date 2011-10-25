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

/**
 * Represents a change in the mutation history of an object.
 *
 * @param <T> payload type. Parametrised so the server can use String and the
 *          client can treat it as a reified JSON object. Ideally both client
 *          and server would use the same type.
 *
 *          TODO(danilatos): Two things need to happen first. 1) A JsoView-like
 *          interface needs to be created, with single-jso-impl client
 *          implementation, and org.json-backed server implementation. 2) PST
 *          needs to be fixed to generate less stupid proto implementations, in
 *          fact, just one simple implementation backed by a shared json
 *          interface such as mentioned in 1. Then "T" would always be that
 *          interface (the server could abolish its use of String, because the
 *          json interface would be directly adaptable to a protobuf).
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(danilatos): Rename to Delta
public final class ChangeData<T> {

  private final ClientId clientId;
  private final T payload;

  public ChangeData(ClientId clientId, T payload) {
    Preconditions.checkNotNull(clientId, "Null clientId");
    Preconditions.checkNotNull(payload, "Null payload");
    this.clientId = clientId;
    this.payload = payload;
  }

  public ClientId getClientId() {
    return clientId;
  }

  public T getPayload() {
    return payload;
  }

  @Override public String toString() {
    return "ChangeData(" + clientId + ", " + payload + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof ChangeData)) { return false; }
    ChangeData other = (ChangeData) o;
    return Objects.equal(clientId, other.clientId)
        && Objects.equal(payload, other.payload);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(clientId, payload);
  }

}
