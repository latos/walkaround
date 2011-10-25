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

package com.google.walkaround.util.shared;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Produces pseudo-random web-safe base-64 strings.
 *
 * Thread-safe assuming that the {@link RandomProvider} is.
 */
// TODO(ohler): eliminate duplication with
// org.waveprotocol.wave.examples.fedone.util.RandomBase64Generator
public class RandomBase64Generator {

  public interface RandomProvider {
    /** @returns a pseudorandom non-negative integer smaller than upperBound */
    int nextInt(int upperBound);
  }

  /** The 64 valid web-safe characters. */
  @VisibleForTesting static final char[] WEB64_ALPHABET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
      .toCharArray();

  private final RandomProvider random;

  @Inject
  public RandomBase64Generator(RandomProvider random) {
    this.random = random;
  }

  /**
   * Returns a string with {@code length} random base-64 characters.
   */
  public String next(int length) {
    StringBuilder result = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      result.append(WEB64_ALPHABET[random.nextInt(64)]);
    }
    return result.toString();
  }
}
