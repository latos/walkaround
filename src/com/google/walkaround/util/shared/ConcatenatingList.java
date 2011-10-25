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

import com.google.common.base.Preconditions;

import java.util.AbstractList;
import java.util.List;

/**
 * An unmodifiable live view of the concatenation of two lists.
 *
 * The implementation is only efficient if the two lists support efficient
 * random access.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ConcatenatingList<T> extends AbstractList<T> {
  public static final <T> ConcatenatingList<T> of(List<T> a, List<T> b) {
    return new ConcatenatingList<T>(a, b);
  }

  private final List<T> a;
  private final List<T> b;

  public ConcatenatingList(List<T> a, List<T> b) {
    this.a = Preconditions.checkNotNull(a, "Null a");
    this.b = Preconditions.checkNotNull(b, "Null b");
  }

  @Override
  public T get(int index) {
    return index < a.size() ? a.get(index) : b.get(index - a.size());
  }

  @Override
  public int size() {
    return a.size() + b.size();
  }

}
