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

/**
 * Called in {@link SlobStore#mutateObject} after the object has changed.
 *
 * @author ohler@google.com (Christian Ohler)
 */
// TODO(ohler): Eliminate this; indexing should be done by adding a task queue
// task via PreCommitHook.
public interface PostMutateHook {

  final PostMutateHook NO_OP = new PostMutateHook() {
    @Override public void run(SlobId objectId, MutateResult result) {}
  };

  void run(SlobId objectId, MutateResult mutateResult);

}
