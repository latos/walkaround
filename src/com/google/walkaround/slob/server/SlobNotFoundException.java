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
 * Thrown when the requested object is not found.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(danilatos): Require object id in all constructors.
public class SlobNotFoundException extends Exception {
  public SlobNotFoundException(SlobId id) {
    super("Not found: " + id);
  }
  public SlobNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  public SlobNotFoundException(String message) {
    super(message);
  }
  public SlobNotFoundException(Throwable cause) {
    super(cause);
  }
}
