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

import com.google.common.base.Preconditions;
import com.google.walkaround.slob.shared.SlobModel.Slob;

/**
 * A {@link Slob} and its current version.  Mutable.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class StateAndVersion {

  private final Slob state;
  private long version;

  public StateAndVersion(Slob state, long version) {
    Preconditions.checkNotNull(state, "Null state");
    this.state = state;
    this.version = version;
  }

  public Slob getState() {
    return state;
  }

  public long getVersion() {
    return version;
  }

  public void apply(ChangeData<String> delta) throws ChangeRejected {
    state.apply(delta);
    version++;
  }

  @Override public String toString() {
    return "StateAndVersion(" + state + ", " + version + ")";
  }

}
