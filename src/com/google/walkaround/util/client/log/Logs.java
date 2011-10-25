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

package com.google.walkaround.util.client.log;

import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;

import org.waveprotocol.wave.client.scheduler.Scheduler;
import org.waveprotocol.wave.client.scheduler.SchedulerInstance;
import org.waveprotocol.wave.client.scheduler.TimerService;
import org.waveprotocol.wave.model.util.CollectionUtils;

import java.util.Collection;

/**
 * Client logging utility that supports lazy handlers, retention of a certain
 * amount of historical log entries, and two dimensions of filtering (streams
 * and levels).
 *
 * The implementation is designed to be efficient when expensive log rendering
 * is turned off, by avoiding unnecessary object creation. Handlers are
 * asynchronously notified that log events are available, and may choose to
 * ignore them (if they are inactive) or have all events pushed to them.
 *
 * The {@link Logs.Log} interface omits the stream name in its interface,
 * because it is intended to be a simple wrapper with the stream name curried in
 * for convenience, as generally each application component will use a single
 * stream name.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class Logs {
  public enum Level {
    /** An unrecoverable error, the application state may be corrupted */
    // NOTE(ohler): server/servlet/ClientExceptionHandler.java contains a string
    // constant that needs to match this.
    SEVERE,
    /** An error but the application has recovered / worked around the issue */
    WARNING,
    /** For reasonably sparse, high level information */
    INFO,
    /** Verbose information */
    DEBUG
  }

  /**
   * A simple wrapper around the logs interface that curries up a stream name.
   *
   * Please read details in {@link Logs#log(String, Level, Object...)}
   */
  public interface Log {
    public void log(Level level, Object... objects);

    public static final Log DEV_NULL = new Log() {
      @Override public void log(Level level, Object... objects) { }
    };
  }

  /**
   * Handler for log entries, to implement functionality such as rendering log
   * entries, reporting errors to a server, etc.
   */
  public interface Handler {
    /**
     * Tests if this handler is ready to receive entries. This is called by the
     * logger, usually asynchronously after items have been logged.
     *
     * @return true if the handler wishes to have the new entries pushed to it.
     */
    boolean canReceive();

    /**
     * Called for each log entry to be received by the handler. This will only
     * happen when {@link #canReceive()} returns true, or if someone manually
     * calls {@link Logs#pushEntries(int,Handler)}.
     *
     *  Do not mutate the objects array as it is shared with other handlers.
     *
     *  NOTE(danilatos): The first UmbrellaException is unwrapped and replaced
     * with its first cause in both the objects array and the first throwable.
     *
     * @param objects the complete set of objects logged
     * @param firstThrowable the first instance of a throwable inside objects.
     */
    void receiveEntry(int num, double timestamp, String stream, Level level, Object[] objects,
        Throwable firstThrowable);
  }

  /**
   * See {@link Entry#firstThrowable}
   */
  private static final Throwable NO_THROWABLE_INSTANCE = new Throwable();

  private static class Entry {
    private double timestamp;
    private String stream;
    private Level level;
    private Object[] objects;
    /**
     * Null if not initialized, {@link Logs#NO_THROWABLE_INSTANCE} if it is
     * initialized and should be regarded as null (no throwable exists in the
     * log entry).
     */
    private Throwable firstThrowable;
  }

  private static final int MAX_ENTRIES_BUFFERED = 200;

  private static final Logs INSTANCE = new Logs(SchedulerInstance.getMediumPriorityTimer());

  public static Logs get() {
    return INSTANCE;
  }

  public static Log create(final String streamName) {
    return new Log() {
      @Override
      public void log(Level level, Object... objects) {
        INSTANCE.log(streamName, level, objects);
      }
    };
  }

  private final Scheduler.Task notifier = new Scheduler.Task() {
    @Override public void execute() {
      isScheduled = false;
      flush();
    }
  };

  private final TimerService timer;
  private final Collection<Handler> handlers = CollectionUtils.createQueue();
  /**
   * Buffer for entries, so we can batch notifications to handlers.
   */
  private final Entry[] entries = new Entry[MAX_ENTRIES_BUFFERED];
  /**
   * Optimization for notifier
   */
  private boolean isScheduled = false;
  /**
   * Number of unflushed (notification not sent) log entries. Must not exceed
   * {@link #MAX_ENTRIES_BUFFERED}.
   */
  private int unflushed = 0;
  /**
   * Index into the {@link #entries} array where the next entry will go.
   */
  private int next = 0;
  /**
   * Total entries ever logged
   */
  private int total = 0;

  Logs(TimerService timer) {
    this.timer = timer;
    for (int i = 0; i < entries.length; i++) {
      entries[i] = new Entry();
    }
  }

  public void addHandler(Handler h) {
    flush();

    if (!handlers.contains(h)) {
      handlers.add(h);
    }
  }

  /**
   * As handling of the log entry may be deferred, objects passed should be
   * immutable with respect to their {@link #toString()} method. (This is
   * trivially the case for Strings themselves and any other immutable objects).
   *
   *  It is best to comma-separate message components rather than concatenating
   * eagerly, to avoid expensive string concatenation and calculation of
   * {@link #toString()} methods for large objects, which can be slow, and
   * unnecessary if the log entry is to be filtered out according to the current
   * settings.
   *
   * If the result of an object's {@link #toString()} method is expected to
   * change after a time, it may be necessary to eagerly convert it to a string
   * at the call site to preserve log message correctness (but keeping in mind
   * the performance implications).
   *
   * @param stream application component stream (for horizontal filtering)
   * @param level log level (for vertical filtering)
   * @param objects
   */
  public void log(String stream, Level level, Object... objects) {
    // We do not want to lose log entries, so flush synchronously if the
    // buffer is full.
    if (unflushed == entries.length) {
      flush();
    }

    Entry entry = entries[next];
    entry.timestamp = timer.currentTimeMillis();
    entry.stream = stream;
    entry.level = level;
    entry.objects = objects;

    next = (next + 1) % entries.length;
    unflushed++;
    total++;
    assert next == total % entries.length;

    // isScheduled check is technically redundant as the scheduler will do it...
    // but this way is SUPER OPTIMIZED.
    if (!isScheduled) {
      timer.schedule(notifier);
      isScheduled = true;
    }
  }

  public void pushEntries(int from, Handler h) {
    for (int i = Math.max(from, total - entries.length); i < total; i++) {
      Entry e = entries[i % entries.length];
      h.receiveEntry(i, e.timestamp, e.stream, e.level, e.objects,
          getThrowable(e));
    }
  }

  private void flush() {
    if (unflushed == 0) {
      return;
    }

    int from = total - unflushed;
    unflushed = 0;

    for (Handler h : handlers) {
      try {
        if (h.canReceive()) {
          pushEntries(from, h);
        }
      } catch (Throwable t) {
        // We don't want to re-log it, or let it reach the uncaught exception
        // handler, because that will likely cause an infinite loop!
        // TODO(danilatos): Don't Window.alert() in real production, parametrise
        // this with some debug mode if statement thing.
        Window.alert("Log handler threw " + t);
      }
    }
  }

  /**
   * Extracts the Throwable from the entry (and caches it).
   * Has the side effect of unwrapping the first UmbrellaException.
   */
  private Throwable getThrowable(Entry e) {
    if (e.firstThrowable == null) {
      e.firstThrowable = NO_THROWABLE_INSTANCE;
      Object[] objects = e.objects;
      for (int i = 0; i < objects.length; i++) {
        if (objects[i] instanceof Throwable) {
          Throwable t = (Throwable) objects[i];

          // Replace annoying umbrella exceptions with the real one
          if (t instanceof UmbrellaException && t.getCause() != null) {
            t = t.getCause();
            objects[i] = t;
          }

          e.firstThrowable = t;
          break;
        }
      }
    }

    return e.firstThrowable != NO_THROWABLE_INSTANCE ? e.firstThrowable : null;
  }
}
