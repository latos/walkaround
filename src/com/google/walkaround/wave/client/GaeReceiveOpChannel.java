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

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.walkaround.slob.client.ChangeDataParser;
import com.google.walkaround.slob.client.GenericOperationChannel.ReceiveOpChannel;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;
import com.google.walkaround.wave.client.GaeChannelDemuxer.GaeChannel;
import com.google.walkaround.wave.client.rpc.ChannelConnectService;

import org.waveprotocol.wave.client.common.util.JsoView;
import org.waveprotocol.wave.client.scheduler.Scheduler;
import org.waveprotocol.wave.client.scheduler.SchedulerInstance;
import org.waveprotocol.wave.client.scheduler.TimerService;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.IntMap;

import javax.annotation.Nullable;

/**
 * Implementation of a {@link ReceiveOpChannel} based on Google App Engine's
 * channel API.
 *
 * Converts a stream of possibly-missing, possibly-unordered, possibly-duplicated
 * messages into a stream of in-order, consecutive, no-dup messages.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(danilatos): Move the flaky layer into a separate class - possibly
// ChannelConnectService, as its callback interface would be sufficient as
// it stands now.
public abstract class GaeReceiveOpChannel<M>
    implements ReceiveOpChannel<M>, GaeChannel {

  private final Scheduler.IncrementalTask heartbeatTask = new Scheduler.IncrementalTask() {
    @Override
    public boolean execute() {
      log.log(Level.DEBUG, "Heartbeat");
      service.connect(signedSessionString, currentRevision, callback);
      return true;
    }
  };

  private final Scheduler.Task catchupTask = new Scheduler.Task() {
    @Override
    public void execute() {
      maybeCatchup();
    }
  };

  private final ChannelConnectService.Callback callback = new ChannelConnectService.Callback() {
    @Override
    public void onConnect(String token) {
      log.log(Level.DEBUG, "connect onSuccess ", token);

      Preconditions.checkState(initialToken == null, "Didn't even connect normally first!");

      demuxer.connect(token);
    }

    @Override
    public void onKnownHeadRevision(int headRevision) {
      log.log(Level.DEBUG, "onKnownHeadRevision(", headRevision, "), ",
          "old known=", knownHeadRevision, ", current=", currentRevision);

      knownHeadRevision = Math.max(knownHeadRevision, headRevision);
      if (knownHeadRevision > currentRevision) {
        scheduleCatchup();
      }

      assert knownHeadRevision == currentRevision || scheduler.isScheduled(catchupTask);
    }

    @Override
    public void onHistoryItem(int resultingRevision, ChangeData<JavaScriptObject> message) {
      receiveUnorderedData(resultingRevision, message);
    }

    @Override
    public void onConnectionError(Throwable e) {
      // TODO(danilatos): Increase heartbeat interval
      log.log(Level.WARNING, "onConnectionError ", e);
    }

    @Override
    public void onFatalError(Throwable e) {
      log.log(Level.WARNING, "onFatalError ", e);
    }
  };

  /**
   * Delay catchup in case we receive operations in the meantime.
   */
  // TODO(danilatos): Flags for these values, and fuzz.
  private static final int CATCHUP_DELAY_MILLIS = 3000;
  private static final int HEARTBEAT_INTERVAL_MILLIS = 15 * 1000;

  private final GaeChannelDemuxer demuxer = GaeChannelDemuxer.get();
  private final TimerService scheduler = SchedulerInstance.getMediumPriorityTimer();
  private final IntMap<ChangeData<JavaScriptObject>> pending = CollectionUtils.createIntMap();
  private final SlobId objectId;
  private final String signedSessionString;
  private final ChannelConnectService service;
  private final Log log;
  private ReceiveOpChannel.Listener<M> listener;
  private int currentRevision = 0;
  private int knownHeadRevision = 0;
  private int catchupRevision = 0;

  /**
   * Initial token provided in constructor, used once and then set to null.
   * Subsequent tokens provided by the connect service callback.
   */
  @Nullable private String initialToken;

  @SuppressWarnings("unused") // used by native code
  private JavaScriptObject socket;

  public GaeReceiveOpChannel(SlobId objectId, String signedSessionString, String channelToken,
      ChannelConnectService service, Log log) {
    this.objectId = objectId;
    this.signedSessionString = signedSessionString;
    this.service = service;
    this.log = log;
    initialToken = channelToken;
  }

  @Override
  public void connect(int revision, ReceiveOpChannel.Listener<M> listener) {
    Preconditions.checkState(this.listener == null && initialToken != null);
    this.listener = listener;
    this.currentRevision = this.knownHeadRevision = revision;

    log.log(Level.DEBUG, "connect, rev=", revision, ", token=", initialToken);

    // Set up browser channel
    demuxer.registerChannel(objectId.getId(), this);
    demuxer.connect(initialToken);
    initialToken = null;

    // Send the first heartbeat immediately, to quickly catch up any initial missing
    // ops, which might happen if the object is currently active.
    scheduler.scheduleRepeating(heartbeatTask, 0, HEARTBEAT_INTERVAL_MILLIS);
  }

  @Override
  public void disconnect() {
    scheduler.cancel(heartbeatTask);
    demuxer.deregisterChannel(objectId.getId());
  }

  private boolean receiving = false;
  private boolean corruptedByException = false;
  private void receiveUnorderedData(int resultingRevision, ChangeData<JavaScriptObject> message) {
    Preconditions.checkState(!corruptedByException, "receiveUnorderedData called while corrupted");
    Preconditions.checkState(!receiving, "receiveUnorderedData called re-entrantly");
    receiving = true;

    try {
      unguardedReceiveUnorderedData(resultingRevision, message);
    } catch (RuntimeException e) {
      corruptedByException = true;
      log.log(Level.WARNING, "Op channel is now corrupted", e);
      throw e;
    }

    receiving = false;
  }

  private void unguardedReceiveUnorderedData(int resultingRevision,
      ChangeData<JavaScriptObject> message) {
    knownHeadRevision = Math.max(knownHeadRevision, resultingRevision);

    if (resultingRevision <= currentRevision) {
      log.log(Level.DEBUG, "Old dup at revision ", resultingRevision,
          ", current is now ", currentRevision);
      return;
    }

    ChangeData<JavaScriptObject> existing = pending.get(resultingRevision);
    if (existing != null) {
      // Should not have pending data at a revision we could have pushed out.
      assert resultingRevision > currentRevision + 1 : "should not have pending data";

      // Sanity check
      if (!existing.getClientId().equals(message.getClientId())) {
        listener.onError(new Exception(
            "Duplicates did not match at resultingRevision " + resultingRevision + ": "
            + existing + " vs " + message));
      }
      log.log(Level.DEBUG, "Dup message: ", message);
      return;
    }


    if (resultingRevision > currentRevision + 1) {
      pending.put(resultingRevision, message);
      log.log(Level.DEBUG, "Missed message, currentRevision=", currentRevision,
          " message revision=", resultingRevision);
      scheduleCatchup();
      return;
    }

    assert resultingRevision == currentRevision + 1 : "other cases should have been caught";

    while (true) {
      M data;
      try {
        data = parse(message);
      } catch (MessageException e) {
        listener.onError(e);
        return;
      }

      log.log(Level.DEBUG, "Ordered op @", resultingRevision, " sid=", message.getClientId(),
          ", payload=", message.getPayload());
      listener.onMessage(currentRevision + 1, message.getClientId().getId(), data);
      currentRevision++;

      int next = currentRevision + 1;
      message = pending.get(next);
      if (message != null) {
        pending.remove(next);
      } else {
        break;
      }
    }

    assert !pending.containsKey(currentRevision + 1);
  }

  private void scheduleCatchup() {
    log.log(Level.DEBUG, "scheduleCatchup()");
    // Check, to avoid resetting the delay.
    if (!scheduler.isScheduled(catchupTask)) {
      scheduler.scheduleDelayed(catchupTask, CATCHUP_DELAY_MILLIS);
    }
  }

  private void maybeCatchup() {
    // Check we're still out of date, and not already catching up.
    if (knownHeadRevision > currentRevision && knownHeadRevision > catchupRevision) {
      log.log(Level.DEBUG, "Catching up to " + knownHeadRevision);
      catchupRevision = knownHeadRevision;
      service.fetchHistory(signedSessionString, currentRevision, callback);
    } else {
      log.log(Level.DEBUG, "No need to catchup");
    }
  }

  @Override
  public void onMessage(JsoView changes) {
    int len = (int) changes.getNumber("length");
    for (int i = 0; i < len; i++) {
      JsoView jso = changes.getJsoView(i);
      int resultingRevision = (int) jso.getNumber("revision");
      ChangeData<JavaScriptObject> message = ChangeDataParser.fromJson(jso);

      log.log(Level.INFO, "Store message: ", message);
      receiveUnorderedData(resultingRevision, message);
    }
  }

  protected abstract M parse(ChangeData<JavaScriptObject> message)
      throws MessageException;
}
