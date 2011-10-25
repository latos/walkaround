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

package com.google.walkaround.wave.server.googleimport;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.walkaround.proto.ImportTaskPayload;
import com.google.walkaround.wave.server.auth.StableUserId;

/**
 * Represents a task that is part of the import process for a user.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ImportTask {

  private final StableUserId userId;
  private final long taskId;
  private final long creationTimeMillis;
  private final ImportTaskPayload payload;

  public ImportTask(StableUserId userId,
      long taskId,
      long creationTimeMillis,
      ImportTaskPayload payload) {
    Preconditions.checkNotNull(userId, "Null userId");
    Preconditions.checkNotNull(payload, "Null payload");
    this.userId = userId;
    this.taskId = taskId;
    this.creationTimeMillis = creationTimeMillis;
    this.payload = payload;
  }

  public StableUserId getUserId() {
    return userId;
  }

  public long getTaskId() {
    return taskId;
  }

  public long getCreationTimeMillis() {
    return creationTimeMillis;
  }

  public ImportTaskPayload getPayload() {
    return payload;
  }

  @Override public String toString() {
    return "ImportTask("
        + userId + ", "
        + taskId + ", "
        + creationTimeMillis + ", "
        + payload
        + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof ImportTask)) { return false; }
    ImportTask other = (ImportTask) o;
    return taskId == other.taskId
        && creationTimeMillis == other.creationTimeMillis
        && Objects.equal(userId, other.userId)
        && Objects.equal(payload, other.payload);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(userId, taskId, creationTimeMillis, payload);
  }

}
