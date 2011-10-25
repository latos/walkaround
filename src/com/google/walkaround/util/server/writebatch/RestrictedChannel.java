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

package com.google.walkaround.util.server.writebatch;

import static com.google.walkaround.util.server.writebatch.Messages.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import org.waveprotocol.wave.model.util.Pair;

/**
 * A Channel<Message<R>> of capacity 1 that only permits a specific sequence of
 * message types and disallows send() until the previous message has been
 * received.
 *
 * @author ohler@google.com (Christian Ohler)
 */
// Package-private because only BatchingUpdateProcessor needs access.
class RestrictedChannel<R> {

  private enum State {
    INITIAL,
    YOUR_TURN_RECEIVED,
    CLOSED;
  }

  private static final ImmutableMap<Pair<State, MessageType>, State> TRANSITIONS =
      ImmutableMap.<Pair<State, MessageType>, State>builder()
      .put(Pair.of(State.INITIAL, MessageType.RESULT), State.CLOSED)
      .put(Pair.of(State.INITIAL, MessageType.YOUR_TURN), State.YOUR_TURN_RECEIVED)
      .put(Pair.of(State.INITIAL, MessageType.PERMANENT_FAILURE), State.CLOSED)
      .put(Pair.of(State.YOUR_TURN_RECEIVED, MessageType.RESULT), State.CLOSED)
      //   Pair.of(State.YOUR_TURN_RECEIVED, MessageType.YOUR_TURN)
      .put(Pair.of(State.YOUR_TURN_RECEIVED, MessageType.PERMANENT_FAILURE), State.CLOSED)
      //   Pair.of(State.CLOSED, MessageType.RESULT)
      //   Pair.of(State.CLOSED, MessageType.YOUR_TURN)
      //   Pair.of(State.CLOSED, MessageType.PERMANENT_FAILURE)
      .build();

  private final Channel<Message<R>> channel = new Channel<Message<R>>(1);
  private final Object lock = new Object();
  // Guarded by lock.
  private State state = State.INITIAL;

  public RestrictedChannel() {
  }


  State nextState(State current, Message<R> m) {
    State next = TRANSITIONS.get(Pair.of(current, m.getType()));
    if (next == null) {
      throw new AssertionError("Invalid message in state " + current + ": " + m);
    }
    return next;
  }

  public void send(Message<R> m) {
    synchronized (lock) {
      Preconditions.checkState(channel.isEmpty(), "Unexpected second message %s in channel %s",
          m, this);
      state = nextState(state, m);
      channel.send(m);
    }
  }

  public Message<R> receive() {
    return channel.receive();
  }

  public boolean isEmpty() {
    return channel.isEmpty();
  }

  @Override public String toString() {
    return "RestrictedChannel(" + state + ", " + channel + ")";
  }

}
