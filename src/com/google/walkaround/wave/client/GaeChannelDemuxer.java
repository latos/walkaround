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
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;

import org.waveprotocol.wave.client.common.util.JsoView;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.StringMap;

/**
 * De-multiplexes object channels a client is listening to.
 *
 * Packets arrive with two keys, 'id' to identify the object, and 'm'
 * containing the message payload.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */

public class GaeChannelDemuxer {
  // There should only ever be one global instance, or strange things will happen.
  private GaeChannelDemuxer() {
  }

  private static final GaeChannelDemuxer INSTANCE = new GaeChannelDemuxer();

  public static GaeChannelDemuxer get() {
    return INSTANCE;
  }

  /**
   * Channel that listens for messages to a specific object
   */
  public interface GaeChannel {
    void onMessage(JsoView data);
  }

  private final Log log = Logs.create("demuxer");
  private final StringMap<GaeChannel> channels = CollectionUtils.createStringMap();
  private String currentToken = null;

  @SuppressWarnings("unused") // used by native code
  private JavaScriptObject socket;

  public void registerChannel(String objectId, GaeChannel channel) {
    Preconditions.checkState(!channels.containsKey(objectId),
        "Channel handler already registered for " + objectId);

    channels.put(objectId, channel);
  }

  public void deregisterChannel(String objectId) {
    Preconditions.checkState(channels.containsKey(objectId),
        "Channel handler not registered for %s", objectId);
    channels.remove(objectId);
  }

  public void connect(String token) {
    if (!Preconditions.checkNotNull(token, "Null token").equals(currentToken)) {
      log.log(Level.INFO, "Connecting with token ", token);
      currentToken = token;
      connectNative(token);
    } else {
      log.log(Level.DEBUG, "Already using same token, ignoring ", token);
    }
  }

  private native void connectNative(String token) /*-{
    var me = this;

    var socket = this.@com.google.walkaround.wave.client.GaeChannelDemuxer::socket;
    if (socket != null) {
      socket.close();
    }

    channel = new $wnd.goog.appengine.Channel(token);

    socket = channel.open();
    this.@com.google.walkaround.wave.client.GaeChannelDemuxer::socket = socket;

    socket.onopen = $entry(function() {
      me.
        @com.google.walkaround.wave.client.GaeChannelDemuxer::onOpened()
        ();
    });
    socket.onmessage = $entry(function(msg) {
      me.
        @com.google.walkaround.wave.client.GaeChannelDemuxer::onMessage(Ljava/lang/String;)
        (msg.data);
    });
    socket.onerror = $entry(function(err) {
      me.
        @com.google.walkaround.wave.client.GaeChannelDemuxer::onError(ILjava/lang/String;)
        (err.code, err.description);
    });
    socket.onclose = $entry(function() {
      me.
        @com.google.walkaround.wave.client.GaeChannelDemuxer::onClose()
        ();
    });
  }-*/;


  @SuppressWarnings("unused") // called by native code
  private void onOpened() {
    log.log(Level.DEBUG, "onOpened ");
  }

  @SuppressWarnings("unused") // called by native code
  private void onMessage(String data) {
    log.log(Level.DEBUG, "onMessage data=", data);
    if (data == null) {
      log.log(Level.WARNING, "Null data on channel");
      return;
    }
    try {
      JsoView jso = JsUtil.eval(data);
      if (!jso.containsKey("id") || !jso.containsKey("m")) {
        throw new MessageException("Missing fields");
      }
      String id = jso.getString("id");
      JsoView m = jso.getJsoView("m");
      GaeChannel channel = channels.get(id);
      if (channel == null) {
        log.log(Level.WARNING, "No channel registered for object with id ", id);
        return;
      }
      channel.onMessage(m);
    } catch (MessageException e) {
      log.log(Level.WARNING, "Bad data on channel ", data, " ", e);
    }
  }

  @SuppressWarnings("unused") // called by native code
  private void onError(int httpCode, String description) {
    log.log(Level.WARNING, "onError code=", httpCode, " description=", description);
  }

  @SuppressWarnings("unused") // called by native code
  private void onClose() {
    log.log(Level.DEBUG, "onClose ");
  }
}
