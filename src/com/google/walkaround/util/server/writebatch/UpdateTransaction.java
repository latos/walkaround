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

import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;

/**
 * A transaction that processes a sequence of updates.  Multiple updates are
 * processed in one transaction for efficiency.
 *
 * Instances will receive a number of calls to processUpdate() followed by
 * exactly one call to either commit() or rollback().
 *
 * @author ohler@google.com (Christian Ohler)
 */
public interface UpdateTransaction<U, R extends UpdateResult> {

  class BatchTooLargeException extends Exception {
    private static final long serialVersionUID = 312635177505224710L;

    public BatchTooLargeException() {
    }

    public BatchTooLargeException(String message) {
      super(message);
    }

    public BatchTooLargeException(Throwable cause) {
      super(cause);
    }

    public BatchTooLargeException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Take an update and processes it.  Resulting changes to the datastore should
   * only be committed in commit().
   *
   * It's valid for this function to mutate the update.  If it does, the mutated
   * update will be used for the next try (if there are any retries).
   *
   * May throw {@link BatchTooLargeException} to decline to process the update;
   * {@link BatchingUpdateProcessor} will then process the update in the
   * next transaction.  The transaction remains usable after declining an update
   * (still has to respond to processUpdate, commit, and rollback).
   *
   * The first call to this method on a given transaction must never throw
   * {@link BatchTooLargeException}.
   */
  R processUpdate(U update) throws BatchTooLargeException, RetryableFailure, PermanentFailure;

  /**
   * Commits the transaction.  Called on successful completion of a sequence of
   * updates.  (Commits the underlying datastore transaction, or may roll it
   * back if there is nothing to write, e.g., if the transaction has rejected
   * all updates that it processed.)
   */
  void commit() throws RetryableFailure, PermanentFailure;

  /**
   * Aborts the transaction.  Called in case of an error.  Should not throw
   * exceptions as that might mask the exception that caused the original error.
   * (Throwing RuntimeExceptions and Errors as a consequence of programming
   * errors is fine, of course; but other errors should be logged rather than
   * rethrown as RuntimeExceptions or Errors.)
   */
  void rollback();

}
