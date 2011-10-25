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

package com.google.walkaround.wave.client.attachment;

import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;
import com.google.walkaround.wave.client.rpc.AttachmentInfoService;

import org.waveprotocol.wave.client.common.util.JsoView;
import org.waveprotocol.wave.client.doodad.attachment.SimpleAttachmentManager;
import org.waveprotocol.wave.client.scheduler.Scheduler;
import org.waveprotocol.wave.client.scheduler.SchedulerInstance;
import org.waveprotocol.wave.client.scheduler.TimerService;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.FuzzingBackOffGenerator;
import org.waveprotocol.wave.model.util.IdentitySet;
import org.waveprotocol.wave.model.util.StringMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Really basic RPC-based attachment manager implementation. Has some primitive
 * retry logic.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class WalkaroundAttachmentManager implements SimpleAttachmentManager {

  /**
   * Max number of attachments to request information for at once. The server
   * might also do fewer than this number at a time.
   */
  // TODO(danilatos): Determine a good value for this constant and make it a flag.
  private static final int MAX_INFO_REQUESTS = 200;

  private final IdentitySet<Listener> listeners = CollectionUtils.createIdentitySet();

  /**
   * Cached attachment data. TODO(danilatos): Clear old ones if it becomes too large?
   */
  private final StringMap<WalkaroundAttachment> attachments = CollectionUtils.createStringMap();

  /**
   * Queue of attachment ids to get information for.
   */
  // TODO(danilatos): Use a set to check uniqueness with faster .contains()
  private final Queue<String> infoQueue = CollectionUtils.createQueue();

  private final List<String> pendingInfoIds = new ArrayList<String>();

  // TODO(danilatos): Ctor arg, and flag.
  private final FuzzingBackOffGenerator backoffGenerator =
      new FuzzingBackOffGenerator(1500, 1800 * 1000, 0.5);

  private final AttachmentInfoService service;

  private final Scheduler.Task infoFetchTask = new Scheduler.Task() {
    @Override public void execute() {
      fetchSomeInfo();
    }
  };

  private final AttachmentInfoService.Callback callback = new AttachmentInfoService.Callback() {
    int attempts = 0;

    @Override
    public void onSuccess(JsoView data) {
      attempts = 0;
      backoffGenerator.reset();
      handleInfo(data);
    }

    @Override
    public void onFatalError(Throwable e) {
      logger.log(Level.WARNING, "Attachment fatal error", e);
    }

    @Override
    public void onConnectionError(Throwable e) {
      // TODO(danilatos): Some kind of better error handling?
      attempts++;
      Level level = attempts > 5 ? Level.WARNING : Level.INFO;
      logger.log(level, "Attachment connection error", e);
      retryPendingIds(backoffGenerator.next().targetDelay);
    }
  };

  private final Log logger;
  private final TimerService scheduler;

  public WalkaroundAttachmentManager(AttachmentInfoService service, Log logger) {
    this(service, SchedulerInstance.getMediumPriorityTimer(), logger);
  }

  public WalkaroundAttachmentManager(AttachmentInfoService service, TimerService scheduler,
      Log logger) {
    this.service = service;
    this.scheduler = scheduler;
    this.logger = logger;
  }

  @Override
  public void addListener(Listener l) {
    listeners.add(l);
  }

  @Override
  public void removeListener(Listener l) {
    listeners.remove(l);
  }

  @Override
  public Attachment getAttachment(String id) {
    if (!attachments.containsKey(id)) {
      attachments.put(id, new WalkaroundAttachment(id));
      scheduleGetInfo(id);
    }
    return attachments.get(id);
  }

  private void scheduleGetInfo(String id) {
    if (infoQueue.contains(id)) {
      return;
    }
    infoQueue.add(id);
    scheduler.schedule(infoFetchTask);
  }

  private void fetchSomeInfo() {
    if (!pendingInfoIds.isEmpty()) {
      return;
    }

    List<String> ids = new ArrayList<String>();
    for (int i = 0; i < MAX_INFO_REQUESTS && !infoQueue.isEmpty(); i++) {
      String id = infoQueue.remove();
      pendingInfoIds.add(id);
    }

    service.fetchInfo(Collections.unmodifiableList(pendingInfoIds), callback);
  }

  private void handleInfo(JsoView result) {
    List<String> pendingIdsCopy = new ArrayList<String>(pendingInfoIds);
    final List<String> updatedIds = new ArrayList<String>();
    pendingInfoIds.clear();
    // TODO(danilatos) Iterate over the result instead, once random access
    // to pendingInfoIds is added.
    for (String id : pendingIdsCopy) {
      JsoView data = (JsoView) result.getJso(id);
      if (data != null) {
        WalkaroundAttachment attachment = attachments.get(id);
        assert attachment != null;
        attachment.setData(data);
        updatedIds.add(id);
      } else {
        pendingInfoIds.add(id);
      }
    }

    if (!pendingInfoIds.isEmpty()) {
      retryPendingIds(0);
    }

    // Notify listeners after doing all the important work.
    listeners.each(new IdentitySet.Proc<Listener>() {
      @Override public void apply(Listener l) {
        for (String id : updatedIds) {
          l.onThumbnailUpdated(attachments.get(id));
          l.onContentUpdated(attachments.get(id));
        }
      }
    });
  }

  private void retryPendingIds(int delay) {
    // Put the pending ids back on the end of the queue.
    for (String id : pendingInfoIds) {
      infoQueue.add(id);
    }
    pendingInfoIds.clear();
    scheduler.scheduleDelayed(infoFetchTask, delay);
  }
}
