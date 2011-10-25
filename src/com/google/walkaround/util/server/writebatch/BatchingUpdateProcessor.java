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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.writebatch.Messages.Message;
import com.google.walkaround.util.server.writebatch.Messages.PermanentFailureMessage;
import com.google.walkaround.util.server.writebatch.Messages.ResultMessage;
import com.google.walkaround.util.server.writebatch.Messages.YourTurnMessage;
import com.google.walkaround.util.server.writebatch.UpdateTransaction.BatchTooLargeException;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Processes updates in a way that batches transactions whenever there are
 * multiple concurrent requests from different threads.
 *
 * This class is thread-safe (and only useful if used from multiple threads).
 *
 * Usage: Keep one BatchingUpdateProcessor per shared object.  The
 * TransactionFactory argument of the processor defines, through the
 * UpdateTransaction implementation that it returns, how the shared objects
 * behave and how they are stored and updated.  Incoming request handlers should
 * get the BatchingUpdateProcessor p that is responsible for the object to be
 * modified, create an object of type U that specifies the change to be made,
 * and invoke p.processUpdate(U).  The BatchingUpdateProcessor will serialize
 * all concurrent calls to p.processUpdate() and turn them into sequential calls
 * to a transaction object.
 *
 * The BatchingUpdateProcessor first calls TransactionFactory.beginTransaction()
 * to create a new transaction tx, then tx.processUpdate() once for each update,
 * and finally tx.commit().
 *
 * Once tx.commit() returns, each call to p.processUpdate() in the request
 * threads returns the UpdateResult returned by the corresponding call to
 * tx.processUpdate().  Results for rejected updates may be returned to the
 * request thread immediately rather than after commit().
 *
 * If TransactionFactory.beginTransaction(), UpdateTransaction.processUpdate()
 * or UpdateTransaction.commit() throw a RetryableFailure, the
 * BatchingUpdateProcessor will retry, according to the strategy implemented by
 * the RetryHelper argument, by starting another transaction through
 * TransactionFactory.beginTransaction() and repeating the above procedure.
 * Rejected updates may not be retried.
 *
 *
 * If any method on an object of this type throws a RuntimeException, the object
 * must be discarded as its internal state may be corrupted.  If any method
 * throws an Error, the entire JVM should be terminated, as threads may be
 * deadlocked or otherwise wedged.  If a method throws a checked exception, on
 * the other hand, the object's state is still valid.
 *
 * @author ohler@google.com (Christian Ohler)
 *
 * @param <U> type of update
 * @param <R> type of result of an update
 * @param <T> type of transaction
 */
public class BatchingUpdateProcessor<U, R extends UpdateResult, T extends UpdateTransaction<U, R>> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(BatchingUpdateProcessor.class.getName());

  private class QueueItem {
    private final Thread thread;
    private final RestrictedChannel<R> channel = new RestrictedChannel<R>();
    private final U update;

    public QueueItem(Thread thread, U update) {
      Preconditions.checkNotNull(thread, "Null thread");
      Preconditions.checkNotNull(update, "Null update");
      this.thread = thread;
      this.update = update;
    }

    public Thread getThread() {
      return thread;
    }

    public RestrictedChannel<R> getChannel() {
      return channel;
    }

    public U getUpdate() {
      return update;
    }

    @Override public String toString() {
      return "QueueItem(" + thread + ", " + channel + ", " + update + ")";
    }
  }

  // A nontrivial invariant that we maintain is
  //
  //   worker == null implies waitingItems.isEmpty()
  //
  // or, reading it the other way, "if there are waiting items, then worker is
  // non-null".  This invariant guarantees that we don't end up with waiting
  // items with no worker to take care of them.

  private final TransactionFactory<R, T> txFactory;
  private final RetryHelper retryHelper;
  private final Object lock = new Object();
  // Concurrent so that we can remove rejected requests with no synchronization.
  // Additions have to happen under the lock.  (The invariant also implies that
  // synchronization is needed for additions but not removals.)
  private final ConcurrentLinkedQueue<QueueItem> waitingItems =
      new ConcurrentLinkedQueue<QueueItem>();
  private Thread worker = null;
  // If this is non-null, the updater is unusable because its state may be corrupted.
  private volatile Throwable corrupted = null;

  public BatchingUpdateProcessor(TransactionFactory<R, T> txFactory,
      RetryHelper retryHelper) {
    Preconditions.checkNotNull(txFactory, "Null txFactory");
    Preconditions.checkNotNull(retryHelper, "Null retryHelper");
    this.txFactory = txFactory;
    this.retryHelper = retryHelper;
  }

  @Override public String toString() {
    return "BatchingUpdateProcessor(" + (corrupted == null ? "" : corrupted + ", ")
        + worker + ", " + waitingItems.size() + " waiting, " + waitingItems.peek() + ")";
  }

  private void setCorrupted(Throwable t) {
    log.log(Level.SEVERE, this + ": Corrupted", t);
    corrupted = t;
  }

  private void assertWorker() {
    Preconditions.checkState(worker == Thread.currentThread(),
        "%s: Thread %s assumed it was the worker", this, Thread.currentThread());
  }

  public R processUpdate(U update) throws PermanentFailure {
    try {
      return doProcessUpdate(update);
    } catch (RuntimeException e) {
      // It's possible that this RuntimeException left the processor in a state
      // where no thread feels responsible for doing any work any more, thus
      // leaving the threads that are currently waiting deadlocked.  (Future
      // requests won't have this problem because our contract says that the
      // caller has to discard the updater after a RuntimeException.)  We could
      // try to resolve the deadlock by sending a corruption notification
      // message to all waitingItems.  A simpler alternative would be to rely on
      // App Engine's 30-second timeout to kill the hanging threads, but I don't
      // think that works for servers.  For now, we throw an Error, in the hope
      // that this will terminate the JVM.
      setCorrupted(e);
      throw new Error("RuntimeException from doProcessUpdate()", e);
    } catch (Error e) {
      setCorrupted(e);
      throw e;
    }
  }

  private R doProcessUpdate(U update) throws PermanentFailure {
    // TODO(ohler): reject if queue is too long
    log.info(this + ": processUpdate(" + update + ")");
    QueueItem item = new QueueItem(Thread.currentThread(), update);
    synchronized (lock) {
      if (corrupted != null) {
        // This should be unnecessary since the contract says that we shouldn't be called any more
        // after a RuntimeException, but that's easy to get wrong, so let's be defensive.
        throw new Error("Updater is corrupted: " + this, corrupted);
      }
      log.info(this + ": doProcessUpdate(): waitingItems=" + waitingItems);
      if (worker == null) {
        log.info(this + ": doProcessUpdate(): no worker active, will become worker");
        worker = Thread.currentThread();
        item.getChannel().send(YourTurnMessage.<R>of());
      } else {
        log.info(this + ": doProcessUpdate(): will wait for current worker: " + worker);
      }
      waitingItems.add(item);
    }
    log.info(this + ": doProcessUpdate(): waiting for message");
    Message<R> message = item.getChannel().receive();
    log.info(this + ": doProcessUpdate(): message=" + message);
    if (message.isYourTurnMessage()) {
      log.info(this + ": doProcessUpdate(): became worker");
      assertWorker();
      return doWorkAndReturnResult(item);
    } else {
      log.info(this + ": doProcessUpdate(): piggybacked, got result " + message);
      return handleResultMessage(message);
    }
  }

  private R handleResultMessage(Message<R> message) throws PermanentFailure {
    log.info(this + ": handleResultMessage(" + message + ")");
    if (message.isYourTurnMessage()) {
      throw new AssertionError(this + ": Expected result message, not " + message);
    } else if (message.isResultMessage()) {
      return message.asResultMessage().getResult();
    } else if (message.isPermanentFailureMessage()) {
      throw new PermanentFailure("Worker thread reported PermanentFailure",
          message.asPermanentFailureMessage().getFailure());
    } else {
      throw new AssertionError(this + ": Unexpected message type: " + message);
    }
  }

  private R doWorkAndReturnResult(QueueItem item) throws PermanentFailure {
    doWork();
    Preconditions.checkState(!item.getChannel().isEmpty(), "%s", item);
    log.info(this + ": doWorkAndReturnResult(): getting message");
    Message<R> message = item.getChannel().receive();
    log.info(this + ": doWorkAndReturnResult(): message=" + message);
    return handleResultMessage(message);
  }

  private List<QueueItem> removeWaitingItems(int count) {
    ImmutableList.Builder<QueueItem> b = ImmutableList.builder();
    log.info(this + ": removeWaitingItems(): removing " + count + " waiting items from "
        + waitingItems);
    for (int i = 0; i < count; i++) {
      assertWorker();
      b.add(waitingItems.remove());
    }
    List<QueueItem> removedItems = b.build();
    log.info(this + ": removeWaitingItems(): removed: " + removedItems);
    log.info(this + ": removeWaitingItems(): remaining: " + waitingItems);
    return removedItems;
  }

  private class WorkerBody implements RetryHelper.VoidBody {
    private final List<Message<R>> messagesToSend;

    private WorkerBody(List<Message<R>> messagesToSend) {
      this.messagesToSend = messagesToSend;
    }

    @Override public String toString() {
      return "WorkerBody(" + BatchingUpdateProcessor.this + ")";
    }

    private void dropRequest(Iterator<QueueItem> iterator, QueueItem item, Message<R> m) {
      log.info("doWork(): dropping request " + item + " with message " + m);
      iterator.remove();
      item.getChannel().send(m);
    }

    private void processItems(T tx) throws RetryableFailure, PermanentFailure {
      Iterator<QueueItem> iterator = waitingItems.iterator();
      Preconditions.checkState(iterator.hasNext(), "%s", waitingItems);
      int rejected = 0;
      while (iterator.hasNext()) {
        QueueItem next = iterator.next();
        log.info("doWork(): next=" + next);
        R result;
        try {
          result = tx.processUpdate(next.getUpdate());
        } catch (BatchTooLargeException e) {
          if (messagesToSend.isEmpty() && rejected == 0) {
            throw new RuntimeException(tx + " rejected the first update: " + next);
          }
          log.log(Level.INFO, "doWork(): batch too large", e);
          break;
        } catch (Error e) {
          dropRequest(iterator, next, PermanentFailureMessage.<R>of(
              new PermanentFailure("processUpdate() threw Error", e)));
          throw e;
        } catch (RuntimeException e) {
          dropRequest(iterator, next, PermanentFailureMessage.<R>of(
              new PermanentFailure("processUpdate() threw RuntimeException", e)));
          throw e;
        }
        log.info("doWork(): result=" + result);
        assertWorker();
        if (result.isRejected()) {
          log.info("doWork(): rejected");
          dropRequest(iterator, next, ResultMessage.<R>of(result));
          rejected++;
        } else {
          log.info("doWork(): accepted");
          messagesToSend.add(ResultMessage.<R>of(result));
        }
      }
      log.info("doWork(): " + rejected + " rejected, "
          + messagesToSend.size() + " messages to send");
    }

    @Override public void run() throws RetryableFailure, PermanentFailure {
      messagesToSend.clear();
      if (waitingItems.isEmpty()) {
        return;
      }
      boolean commitCalled = false;
      log.info("doWork(): calling beginTransaction()");
      T tx = txFactory.beginTransaction();
      log.info("doWork(): tx=" + tx);
      try {
        log.info("doWork(): waitingItems at start: " + waitingItems);
        processItems(tx);
        // We log waitingItems before and after to be able to see if anyone
        // modified it concurrently.
        log.info("doWork(): waitingItems at end but before removing: "
            + waitingItems);
        log.info("doWork(): messagesToSend=" + messagesToSend);
        log.info("doWork(): calling commit()");
        commitCalled = true;
        tx.commit();
        log.info("doWork(): commit() done");
      } catch (Error e) {
        // Log this in case the finally clause below masks it with another exception.
        log.log(Level.SEVERE, "Error in doWork()'s retry body", e);
        throw e;
      } catch (RuntimeException e) {
        // Log this in case the finally clause below masks it with another exception.
        log.log(Level.SEVERE, "RuntimeException in doWork()'s retry body", e);
        throw e;
      } finally {
        if (!commitCalled) {
          log.info("doWork(): calling rollback()");
          tx.rollback();
          log.info("doWork(): rollback() done");
        }
      }
    }
  }

  private void addPermanentFailureMessages(List<Message<R>> messagesToSend,
      PermanentFailure failure) {
    Message<R> failureMessage = PermanentFailureMessage.<R>of(failure);
    if (messagesToSend.isEmpty()) {
      log.warning(this + ": messagesToSend is empty");
      // This is a weird situation.  Perhaps beginTransaction() threw a
      // PermanentFailure.  We drop at least one request (if there are any) to
      // avoid infinite loops.
      if (!waitingItems.isEmpty()) {
        messagesToSend.add(failureMessage);
      }
    } else {
      for (int i = 0; i < messagesToSend.size(); i++) {
        messagesToSend.set(i, failureMessage);
      }
    }
  }

  private void doWork() throws PermanentFailure {
    log.info(this + ": doWork(): begin");
    Preconditions.checkState(!waitingItems.isEmpty(), "%s: waitingItems is empty", this);
    // This list is parallel with a prefix of waitingItems: On success, the Nth message in this list
    // will be sent to the Nth waitingItem.  On PermanentFailure, the same number of waitingItems
    // will be notified, but all with a failure message.
    List<Message<R>> messagesToSend = Lists.newArrayList();
    try {
      retryHelper.run(new WorkerBody(messagesToSend));
    } catch (PermanentFailure failure) {
      log.log(Level.WARNING, "Permanent failure", failure);
      addPermanentFailureMessages(messagesToSend, failure);
      throw failure;
    } catch (Error e) {
      // Log this in case the finally clause below masks it with another exception.
      log.log(Level.SEVERE, "Error in doWork()", e);
      addPermanentFailureMessages(messagesToSend,
          new PermanentFailure("Error in doWork()", e));
      throw e;
    } catch (RuntimeException e) {
      // Log this in case the finally clause below masks it with another exception.
      log.log(Level.SEVERE, "RuntimeException in doWork()", e);
      addPermanentFailureMessages(messagesToSend,
          new PermanentFailure("RuntimeException in doWork()", e));
      throw e;
    } finally {
      // Note that it is possible that messagesToSend is empty: If the batch succeeded and
      // all results were rejections.
      List<QueueItem> removed = removeWaitingItems(messagesToSend.size());
      synchronized (lock) {
        assertWorker();
        // NOTE(ohler): It is essential that this executes even if we throw PermanentFailure;
        // otherwise, no thread will feel responsible for doing any work any more.
        if (!waitingItems.isEmpty()) {
          QueueItem item = waitingItems.element();
          worker = item.getThread();
          item.getChannel().send(YourTurnMessage.<R>of());
        } else {
          worker = null;
        }
      }
      Preconditions.checkState(removed.size() == messagesToSend.size(), "%s %s",
          removed.size(), messagesToSend.size());
      for (int i = 0; i < removed.size(); i++) {
        log.info(this + ": doWork(): sending result " + i);
        removed.get(i).getChannel().send(messagesToSend.get(i));
      }
    }
  }

}
