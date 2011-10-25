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
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.walkaround.wave.client.WalkaroundOperationChannel.SavedStateListener;
import com.google.walkaround.wave.client.rpc.Rpc.ConnectionState;
import com.google.walkaround.wave.client.rpc.Rpc.ConnectionStateListener;

import org.waveprotocol.wave.client.common.safehtml.EscapeUtils;
import org.waveprotocol.wave.client.scheduler.Scheduler;
import org.waveprotocol.wave.client.scheduler.SchedulerInstance;
import org.waveprotocol.wave.client.scheduler.TimerService;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.StringSet;

/**
 * Simple saved state indicator.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// TODO(danilatos): Make the UI pretty
public class SavedStateIndicator implements SavedStateListener, ConnectionStateListener {

  private enum SavedState {
    SAVED("Saved"),
    UNSAVED("Unsaved..."),
    TURBULENCE("Unable to save");

    final String message;
    private SavedState(String message) {
      this.message = message;
    }
  }

  private static final int UPDATE_DELAY_MS = 300;

  private final Scheduler.Task updateTask = new Scheduler.Task() {
    @Override public void execute() {
      updateDisplay();
    }
  };

  private final Element element;
  private final TimerService scheduler;

  private final StringSet channels = CollectionUtils.createStringSet();
  private boolean turbulenced = false;
  private ConnectionState connectionState = ConnectionState.CONNECTED;

  private SavedState visibleSavedState = SavedState.SAVED;

  public SavedStateIndicator(Element element) {
    this.element = element;
    this.scheduler = SchedulerInstance.getLowPriorityTimer();
  }

  @Override
  public void saved(String channelId) {
    Preconditions.checkState(channels.contains(channelId), "Already saved channel %s", channelId);
    channels.remove(channelId);
    maybeUpdateDisplay();
  }

  @Override
  public void unsaved(String channelId) {
    channels.add(channelId);
    maybeUpdateDisplay();
  }

  @Override
  public void turbulenced() {
    turbulenced = true;
    maybeUpdateDisplay();
  }

  @Override
  public void connectionStateChanged(ConnectionState newConnectionState) {
    this.connectionState = newConnectionState;
    updateDisplay();
  }

  private void maybeUpdateDisplay() {
    if (needsUpdating()) {
      switch (currentSavedState()) {
        case SAVED:
          scheduler.scheduleDelayed(updateTask, UPDATE_DELAY_MS);
          break;
        case TURBULENCE:
        case UNSAVED:
          updateDisplay();
          break;
        default:
          throw new AssertionError("unknown " + currentSavedState());
      }
    } else {
      scheduler.cancel(updateTask);
    }
  }

  private boolean needsUpdating() {
    return visibleSavedState != currentSavedState();
  }

  private SavedState currentSavedState() {
    return turbulenced ? SavedState.TURBULENCE
        : channels.isEmpty() ? SavedState.SAVED : SavedState.UNSAVED;
  }

  private void updateDisplay() {
    visibleSavedState = currentSavedState();
    element.setInnerHTML(visibleSavedState.message + htmlForConnectionState());
  }

  private String htmlForConnectionState() {
    switch (connectionState) {
      case CONNECTED: return "";
      case LOGGED_OUT: {
        String self = Window.Location.getHref();
        return "<div>You are logged out. <a href=\"" + EscapeUtils.htmlEscape(self)
            + "\">Reload</a> " + " the page to log in again</div>";
      }
      case OFFLINE: return "<div>You are not connected</div>";
      case SOFT_RELOAD:
      case HARD_RELOAD: {
        // TODO(danilatos): Refresh automatically (perhaps not here) if there are no
        // unsaved changes.
        String self = Window.Location.getHref();
        return "<div>Please <a href=\"" + EscapeUtils.htmlEscape(self) + "\">reload</a> "
            + " the page to recover</div>";
      }
      default:
        throw new AssertionError("unknown " + connectionState);
    }
  }
}
