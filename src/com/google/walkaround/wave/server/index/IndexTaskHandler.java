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

package com.google.walkaround.wave.server.index;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.wave.server.WalkaroundServletModule;
import com.google.walkaround.wave.server.util.AbstractHandler;
import com.google.walkaround.wave.server.wavemanager.InboxHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Endpoint for indexing task queue tasks.
 *
 * TODO(danilatos): Consider using pull-queues in the future.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class IndexTaskHandler extends AbstractHandler {
  public static void scheduleIndex(SlobId id) {
    log.info("Enqueuing index task for " + id);
    Queue queue = QueueFactory.getQueue("index");
    queue.add(withUrl(WalkaroundServletModule.INDEX_TASK_PATH).param("id", id.getId()));
  }

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(InboxHandler.class.getName());

  private final Indexer indexer;

  @Inject
  public IndexTaskHandler(Indexer indexer) {
    this.indexer = indexer;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    indexer.index(new SlobId(req.getParameter("id")));
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doGet(req, resp);
  }
}
