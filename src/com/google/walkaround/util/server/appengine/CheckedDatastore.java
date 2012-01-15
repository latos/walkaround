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

package com.google.walkaround.util.server.appengine;

import com.google.appengine.api.datastore.CommittedButStillApplyingException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreNeedIndexException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.InternalFailureException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A wrapper around {@link DatastoreService} that throws checked exceptions
 * for failures.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
// TODO(ohler): Eliminate PermanentFailure and RetryableFailure from this
// interface.  The exception types that we throw should express what happened;
// how to deal with it (whether to retry or not) is the up to the caller to
// decide.  PermanentFailure and RetryableFailure were only intended to be
// RetryHelper's API, not to be used everywhere.  (Maybe it is not a good API.
// An alternative API would be to signal the need for a retry in a similar way
// to Guava's AbstractIterator.endOfData(), and to use RuntimeExceptions to
// abort the body with a permanent failure.)
public class CheckedDatastore {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(CheckedDatastore.class.getName());

  private interface Evaluater<T> {
    T run() throws RetryableFailure, PermanentFailure;
  }

  public interface CheckedIterator {
    boolean hasNext() throws PermanentFailure, RetryableFailure;
    Entity next() throws PermanentFailure, RetryableFailure;
    Cursor getCursor() throws PermanentFailure, RetryableFailure;

    public static final CheckedIterator EMPTY = new CheckedIterator() {
      @Override public boolean hasNext() {
        return false;
      }

      @Override public Entity next() {
        throw new NoSuchElementException("empty iterator");
      }

      @Override public Cursor getCursor() {
        throw new UnsupportedOperationException("Not implemented");
      }
    };
  }

  /**
   * A wrapper around {@link QueryResultIterator}<{@link Entity}> that throws
   * checked exceptions for failures.
   */
  private static class CheckedIteratorImpl implements CheckedIterator {
    private final QueryResultIterator<Entity> iterator;

    CheckedIteratorImpl(QueryResultIterator<Entity> iterator) {
      Preconditions.checkNotNull(iterator, "Null iterator");
      this.iterator = iterator;
    }

    @Override public boolean hasNext() throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<Boolean>() {
        @Override public Boolean run() {
          return iterator.hasNext();
        }
      });
    }

    @Override public Entity next() throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<Entity>() {
        @Override public Entity run() {
          return iterator.next();
        }
      });
    }

    @Override public Cursor getCursor() throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<Cursor>() {
        @Override public Cursor run() {
          return iterator.getCursor();
        }
      });
    }

    @Override public String toString() {
      return "CheckedIteratorImpl(" + iterator + ")";
    }
  }

  /**
   * A wrapper around {@link PreparedQuery} that throws checked exceptions for
   * failures.
   */
  public abstract static class CheckedPreparedQuery {
    public abstract CheckedIterator asIterator(final FetchOptions options)
        throws PermanentFailure, RetryableFailure;
    public CheckedIterator asIterator() throws PermanentFailure, RetryableFailure {
      return asIterator(FetchOptions.Builder.withDefaults());
    }

    public abstract List<Entity> asList(final FetchOptions options)
        throws PermanentFailure, RetryableFailure;
    public List<Entity> asList() throws PermanentFailure, RetryableFailure {
      return asList(FetchOptions.Builder.withDefaults());
    }

    public abstract int countEntities(final FetchOptions options)
        throws PermanentFailure, RetryableFailure;

    public Entity asSingleEntity() throws PermanentFailure, RetryableFailure {
      // We could say withLimit(2) here, but that's only more efficient in the
      // failure case, and in that case, the improved diagnostics are probably
      // worth the cost.
      List<Entity> results = asList(FetchOptions.Builder.withLimit(10));
      switch (results.size()) {
        case 0:
          return null;
        case 1:
          return results.get(0);
        default:
          throw new RuntimeException("Found more than one result for " + this
              + "; some of them are: " + results + " (not exhaustive)");
      }
    }

    public Entity getFirstResult() throws PermanentFailure, RetryableFailure {
      List<Entity> results = asList(FetchOptions.Builder.withLimit(1));
      return results.isEmpty() ? null : results.get(0);
    }
  }

  /**
   * A wrapper around {@link PreparedQuery} that throws checked exceptions for
   * failures.
   */
  private static class CheckedPreparedQueryImpl extends CheckedPreparedQuery {
    private final PreparedQuery q;

    private CheckedPreparedQueryImpl(PreparedQuery q) {
      Preconditions.checkNotNull(q, "Null q");
      this.q = q;
    }

    @Override public String toString() {
      return "CheckedPreparedQueryImpl(" + q + ")";
    }

    @Override public CheckedIterator asIterator(final FetchOptions options)
        throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<CheckedIterator>() {
        @Override public CheckedIterator run() {
          return new CheckedIteratorImpl(q.asQueryResultIterator(options));
        }
      });
    }

    @Override public List<Entity> asList(final FetchOptions options)
        throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<List<Entity>>() {
        @Override public List<Entity> run() {
          return q.asList(options);
        }
      });
    }

    @Override public int countEntities(final FetchOptions options)
        throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<Integer>() {
        @Override public Integer run() {
          return q.countEntities(options);
        }
      });
    }
  }

  public interface CheckedTransaction {
    // Read access.
    Entity get(Key key) throws PermanentFailure, RetryableFailure;
    Map<Key, Entity> get(Iterable<Key> keys) throws PermanentFailure, RetryableFailure;
    CheckedPreparedQuery prepare(Query q);

    // Write access.
    Key put(Entity e) throws PermanentFailure, RetryableFailure;
    List<Key> put(Iterable<Entity> e) throws PermanentFailure, RetryableFailure;
    void delete(Key... keys) throws PermanentFailure, RetryableFailure;
    TaskHandle enqueueTask(Queue queue, TaskOptions task) throws PermanentFailure, RetryableFailure;

    // Transaction lifecycle management.
    void rollback();
    void commit() throws PermanentFailure, RetryableFailure;
    boolean isActive();
    /** Shorthand for {@code if (isActive()) rollback();}. */
    void close();
  }

  private class CheckedTransactionImpl implements CheckedTransaction {
    private final Transaction transaction;

    CheckedTransactionImpl(Transaction transaction) {
      this.transaction = transaction;
    }

    @Override
    public Entity get(final Key key) throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<Entity>() {
        @Override public Entity run() {
          try {
            return datastore.get(transaction, key);
          } catch (EntityNotFoundException e) {
            return null;
          }
        }
      });
    }

    @Override
    public Map<Key, Entity> get(final Iterable<Key> keys)
        throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<Map<Key, Entity>>() {
        @Override public Map<Key, Entity> run() {
          return datastore.get(transaction, keys);
        }
      });
    }

    @Override
    public CheckedPreparedQuery prepare(Query q) {
      // TODO(ohler): confirm that this doesn't need safeRun, and document why not
      return new CheckedPreparedQueryImpl(datastore.prepare(transaction, q));
    }

    @Override
    public Key put(final Entity e) throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<Key>() {
        @Override public Key run() {
          return datastore.put(transaction, e);
        }
      });
    }

    @Override
    public List<Key> put(final Iterable<Entity> e) throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<List<Key>>() {
        @Override public List<Key> run() {
          return datastore.put(transaction, e);
        }
      });
    }

    @Override
    public void delete(final Key... keys) throws PermanentFailure, RetryableFailure {
      safeRun(new Evaluater<Void>() {
        @Override public Void run() {
          datastore.delete(transaction, keys);
          return null;
        }
      });
    }

    @Override
    public TaskHandle enqueueTask(final Queue queue, final TaskOptions task)
        throws PermanentFailure, RetryableFailure {
      return safeRun(new Evaluater<TaskHandle>() {
        @Override public TaskHandle run() throws RetryableFailure, PermanentFailure {
          try {
            return queue.add(transaction, task);
          } catch (InternalFailureException e) {
            // Perhaps the fact that this is different from
            // TransientFailureException suggests that we shouldn't retry, but
            // neither the exception type nor the documentation are explicit
            // about it, so let's retry.
            throw new RetryableFailure("Internal task queue failure enqueueing " + task, e);
          } catch (TransientFailureException e) {
            throw new RetryableFailure("Transient task queue failure enqueueing " + task, e);
          }
        }
      });
    }

    @Override
    public void rollback() {
      try {
        safeRun(new Evaluater<Void>() {
          @Override public Void run() {
            transaction.rollback();
            return null;
          }
        });
      } catch (PermanentFailure e) {
        log.log(Level.SEVERE, "Exception during rollback, ignoring", e);
      } catch (RetryableFailure e) {
        log.log(Level.WARNING, "Exception during rollback, ignoring", e);
      } catch (IllegalArgumentException e) {
        // Datastore gives us IllegalArgumentException when the transaction has timed out.
        log.log(Level.WARNING, "IllegalArgumentException during rollback, ignoring", e);
      }
    }

    @Override
    public void commit() throws PermanentFailure, RetryableFailure {
      safeRun(new Evaluater<Void>() {
        @Override public Void run() {
          try {
            transaction.commit();
          } catch (CommittedButStillApplyingException e) {
            // just log warning for now out of curiosity.
            // we could just totally ignore it.
            log.log(Level.WARNING, "still applying", e);
          }
          return null;
        }
      });
    }

    @Override
    public boolean isActive() {
      return transaction.isActive();
    }

    @Override
    public void close() {
      if (isActive()) {
        rollback();
      }
    }

    @Override
    public String toString() {
      return "CheckedTransactionImpl(" + transaction
          + String.format(" (%s@%x)", transaction.getClass(), System.identityHashCode(transaction))
          + ")";
    }
  }

  private final DatastoreService datastore;

  @Inject
  public CheckedDatastore(DatastoreService datastore) {
    this.datastore = datastore;
  }

  public CheckedTransaction beginTransaction() throws PermanentFailure, RetryableFailure {
    return safeRun(new Evaluater<CheckedTransaction>() {
      @Override public CheckedTransaction run() {
        Transaction rawTransaction = datastore.beginTransaction();
        // NOTE(ohler): Calling rawTransaction.getId() forces TransactionImpl to
        // wait for the result of the beginTransaction RPC.  We do this here to
        // get the DatastoreTimeoutException (in the case of a timeout) right
        // away rather than at some surprising time later in
        // TransactionImpl.toString() or similar.
        //
        // Hopefully, TransactionImpl will be fixed to eliminate the need for
        // this.
        try {
          rawTransaction.getId();
        } catch (DatastoreTimeoutException e) {
          // We don't log transaction itself because I'm worried its toString()
          // might fail.  TODO(ohler): confirm this.
          log.log(Level.WARNING, "Failed to begin transaction", e);
          // Now we need to roll back the transaction (even though it doesn't
          // actually exist), otherwise TransactionCleanupFilter will try to
          // roll it back, which is bad because it's not prepared for the crash
          // that we catch below.
          try {
            rawTransaction.rollback();
            throw new Error("Rollback of nonexistent transaction did not fail");
          } catch (DatastoreTimeoutException e2) {
            log.log(Level.INFO, "Rollback of nonexistent transaction failed as expected", e2);
          }
          throw e;
        }
        CheckedTransaction checkedTransaction = new CheckedTransactionImpl(rawTransaction);
        log.info("Begun transaction " + checkedTransaction);
        return checkedTransaction;
      }
    });
  }

  public CheckedPreparedQuery prepareNontransactionalQuery(Query q) {
    // TODO(ohler): confirm that this doesn't need safeRun, and document why not
    return new CheckedPreparedQueryImpl(datastore.prepare(q));
  }

  public DatastoreService unsafe() {
    return datastore;
  }

  private static <T> T safeRun(Evaluater<T> runnable) throws PermanentFailure, RetryableFailure {
    try {
      return runnable.run();
    } catch (DatastoreTimeoutException e) {
      throw new RetryableFailure(e);
    } catch (ConcurrentModificationException e) {
      throw new RetryableFailure(e);
    } catch (DatastoreFailureException e) {
      throw new PermanentFailure(e);
    } catch (DatastoreNeedIndexException e) {
      throw new PermanentFailure(e);
    } catch (PreparedQuery.TooManyResultsException e) {
      throw new PermanentFailure(e);
    }
  }

}
