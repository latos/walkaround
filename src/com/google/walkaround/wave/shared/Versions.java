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

package com.google.walkaround.wave.shared;

/**
 * Used to explicitly mark locations where wave versions (longs) are converted
 * to object store versions (ints).
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(danilatos): This is no longer necessary, let's make everything long
// (or maybe double on the client) and get rid of this.
public final class Versions {

  public static int truncate(long version) {
    if (version > Integer.MAX_VALUE) {
      throw new AssertionError("huge version " + version);
    }
    return (int) version;
  }
}
