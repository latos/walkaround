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

package com.google.walkaround.slob.client;

import org.waveprotocol.wave.client.scheduler.Scheduler.IncrementalTask;
import org.waveprotocol.wave.client.scheduler.Scheduler.Schedulable;
import org.waveprotocol.wave.client.scheduler.Scheduler.Task;
import org.waveprotocol.wave.client.scheduler.TimerService;

/**
 * Convenience class that stubs out all methods of {@link TimerService}.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(danilatos): Move this class into the scheduler.testing package
// once the dust has settled with the open source jars?
public class StubTimerService implements TimerService {

  @Override
  public void cancel(Schedulable job) {
    throw new AssertionError("Not implemented");
  }

  @Override
  public double currentTimeMillis() {
    throw new AssertionError("Not implemented");
  }

  @Override
  public int elapsedMillis() {
    throw new AssertionError("Not implemented");
  }

  @Override
  public boolean isScheduled(Schedulable job) {
    throw new AssertionError("Not implemented");
  }

  @Override
  public void schedule(Task task) {
    throw new AssertionError("Not implemented");
  }

  @Override
  public void schedule(IncrementalTask process) {
    throw new AssertionError("Not implemented");
  }

  @Override
  public void scheduleDelayed(Task task, int minimumTime) {
    throw new AssertionError("Not implemented");
  }

  @Override
  public void scheduleDelayed(IncrementalTask process, int minimumTime) {
    throw new AssertionError("Not implemented");
  }

  @Override
  public void scheduleRepeating(IncrementalTask process, int minimumTime, int interval) {
    throw new AssertionError("Not implemented");
  }
}
