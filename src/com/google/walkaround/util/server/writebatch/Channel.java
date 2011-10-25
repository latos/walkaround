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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A blocking queue of T with some logging instrumentation and no
 * InterruptedExceptions.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class Channel<T> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(Channel.class.getName());

  private final ArrayBlockingQueue<T> queue;

  public Channel(int capacity) {
    queue = new ArrayBlockingQueue<T>(capacity);
  }

  // Never returns, but has return type Error so that it can be called in a
  // throws clause.
  private Error interrupted(InterruptedException e) {
    log.log(Level.SEVERE, "Interrupted", e);
    Thread.currentThread().interrupt();
    throw new Error("Interrupted", e);
  }

  public void send(T m) {
    log.info(Thread.currentThread() + ": " + this + ": send(" + m + ")");
    try {
      queue.put(m);
    } catch (InterruptedException e) {
      throw interrupted(e);
    }
    log.info(Thread.currentThread() + ": " + this + ": send(): returning");
  }

  public T receive() {
    log.info(Thread.currentThread() + ": " + this + ": receive()");
    T result;
    try {
      result = queue.take();
    } catch (InterruptedException e) {
      throw interrupted(e);
    }
    log.info(Thread.currentThread() + ": " + this + ": receive(): returning " + result);
    return result;
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  @Override public String toString() {
    int size = queue.size();
    if (size <= 5) {
      // TODO(ohler): should perhaps have a different toString() if capacity is 0
      return "Channel(" + queue + ")";
    } else {
      return "Channel(" + size + ", " + queue.peek() + ")";
    }
  }

}
