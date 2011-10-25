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

package com.google.walkaround.wave.client;

import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;

/**
 * Dumps read state commands to the debug log.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class FakeReadStateService implements ReadStateService {

  private static final Log LOG = Logs.create("readstate");

  FakeReadStateService() {
  }

  public static FakeReadStateService create() {
    return new FakeReadStateService();
  }

  @Override
  public void setReadState(boolean read) {
    LOG.log(Level.INFO, "Marking wave as ", read ? "read" : "unread");
  }
}
