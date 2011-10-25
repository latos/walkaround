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

package com.google.walkaround.wave.server.gxp;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Information {@link FlagsFragment} needs about a flag.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public final class FlagDisplayRecord {
  private final String name;
  private final String type;
  private final String value;

  public FlagDisplayRecord(String name, String type, String value) {
    Preconditions.checkNotNull(name, "Null name");
    Preconditions.checkNotNull(type, "Null type");
    Preconditions.checkNotNull(value, "Null value");
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  @Override public String toString() {
    return "FlagDisplayRecord(" + name + ", " + type + ", " + value + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof FlagDisplayRecord)) { return false; }
    FlagDisplayRecord other = (FlagDisplayRecord) o;
    return Objects.equal(name, other.name)
        && Objects.equal(type, other.type)
        && Objects.equal(value, other.value);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(name, type, value);
  }

}
