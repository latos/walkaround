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
import com.google.walkaround.slob.shared.SlobModel.ReadableSlob;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public interface PostCommitAction {

  /** Run immediately after each commit, with high likelihood, only once. */
  void unreliableImmediatePostCommit(SlobId slobId, long resultingVersion, ReadableSlob resultingState);

  /**
   * Run some time after the commit, guaranteed, possibly multiple times, and
   * possibly multiple successive commits batched into one run. Should be
   * idempotent, in fact make no assumptions whatever about the number of
   * commits since last called.
   */
  void reliableDelayedPostCommit(SlobId slobId);
}
