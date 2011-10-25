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

import com.google.inject.Inject;
import com.google.walkaround.util.shared.RandomBase64Generator.RandomProvider;

import java.util.Random;

/**
 * Implement {@link RandomProvider} using a {@link Random}.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class RandomProviderAdapter implements RandomProvider {

  private final Random random;

  @Inject
  public RandomProviderAdapter(Random random) {
    this.random = random;
  }

  @Override public int nextInt(int n) {
    return random.nextInt(n);
  }

}
