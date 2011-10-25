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

import com.google.common.base.Objects;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryStrategy;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;

import junit.framework.TestCase;

import javax.annotation.Nullable;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class BatchingUpdateProcessorTest extends TestCase {

  // For use by other tests as well.
  static class Update {
    public Update() {
    }

    @Override public String toString() {
      return "Update()";
    }

    @Override public boolean equals(Object o) {
      return o != null && o.getClass() == Update.class;
    }

    @Override public int hashCode() {
      return Objects.hashCode(Update.class);
    }
  }

  // For use by other tests as well.
  static class Result implements UpdateResult {
    private final boolean rejected;

    public Result(boolean rejected) {
      this.rejected = rejected;
    }

    @Override public boolean isRejected() {
      return rejected;
    }

    @Override public String toString() {
      return "Result(" + rejected + ")";
    }

    @Override public boolean equals(Object o) {
      if (o == null) { return false; }
      if (o == this) { return true; }
      if (!(o.getClass() == Result.class)) { return false; }
      Result other = (Result) o;
      return this.rejected == other.rejected;
    }

    @Override public int hashCode() {
      return Objects.hashCode(Result.class, rejected);
    }
  }

  // For use by other tests as well.
  class SingleImmediateRetry implements RetryStrategy {
    @Override public long delayMillisBeforeRetry(int numRetries, long millisSoFar,
        RetryableFailure exception) throws PermanentFailure {
      if (numRetries == 0) {
        return 0;
      } else {
        throw new PermanentFailure(exception);
      }
    }
  }

  public void testSingleRejectedUpdateAndCommitFailure() throws Exception {
    class TestTransaction implements UpdateTransaction<Update, Result> {
      @Override public Result processUpdate(Update update)
          throws BatchTooLargeException, RetryableFailure, PermanentFailure {
        // Always reject.
        return new Result(true);
      }

      @Override public void commit() throws RetryableFailure, PermanentFailure {
        // Always throw.
        throw new RetryableFailure();
      }

      @Override public void rollback() {
      }
    }

    class TestTransactionFactory implements TransactionFactory<Result, TestTransaction>{
      @Override public TestTransaction beginTransaction()
          throws RetryableFailure, PermanentFailure {
        return new TestTransaction();
      }
    }

    class TestProcessor extends BatchingUpdateProcessor<Update, Result, TestTransaction> {
      public TestProcessor() {
        super(new TestTransactionFactory(), new RetryHelper(new SingleImmediateRetry()));
      }
    }

    TestProcessor p = new TestProcessor();
    // The test is that this doesn't throw.
    p.processUpdate(new Update());
  }


  public void testRuntimeExceptionsGetPropagated() throws Exception {
    class TestTransaction implements UpdateTransaction<Update, Result> {
      private final @Nullable RuntimeException processUpdateException;
      private final @Nullable RuntimeException commitException;
      private final @Nullable RuntimeException rollbackException;

      TestTransaction(@Nullable RuntimeException processUpdateException,
          @Nullable RuntimeException commitException,
          @Nullable RuntimeException rollbackException) {
        this.processUpdateException = processUpdateException;
        this.commitException = commitException;
        this.rollbackException = rollbackException;
      }

      @Override public Result processUpdate(Update update)
          throws BatchTooLargeException, RetryableFailure, PermanentFailure {
        if (processUpdateException != null) {
          throw processUpdateException;
        } else {
          return new Result(false);
        }
      }

      @Override public void commit() throws RetryableFailure, PermanentFailure {
        if (commitException != null) {
          throw commitException;
        }
      }

      @Override public void rollback() {
        if (rollbackException != null) {
          throw rollbackException;
        }
      }
    }

    class TestTransactionFactory implements TransactionFactory<Result, TestTransaction>{
      private final @Nullable RuntimeException beginTransactionException;
      private final @Nullable RuntimeException processUpdateException;
      private final @Nullable RuntimeException commitException;
      private final @Nullable RuntimeException rollbackException;

      TestTransactionFactory(@Nullable RuntimeException beginTransactionException,
          @Nullable RuntimeException processUpdateException,
          @Nullable RuntimeException commitException,
          @Nullable RuntimeException rollbackException) {
        this.beginTransactionException = beginTransactionException;
        this.processUpdateException = processUpdateException;
        this.commitException = commitException;
        this.rollbackException = rollbackException;
      }

      @Override public TestTransaction beginTransaction()
          throws RetryableFailure, PermanentFailure {
        if (beginTransactionException != null) {
          throw beginTransactionException;
        } else {
          return new TestTransaction(processUpdateException, commitException, rollbackException);
        }
      }
    }

    final RuntimeException testException = new RuntimeException(
        "HACK: Exception whose stack trace does not match the location where it was thrown");
    final RuntimeException testException2 = new RuntimeException(
        "HACK: Exception 2 whose stack trace does not match the location where it was thrown");

    class TestProcessor extends BatchingUpdateProcessor<Update, Result, TestTransaction> {
      public TestProcessor(@Nullable RuntimeException beginTransactionException,
          @Nullable RuntimeException processUpdateException,
          @Nullable RuntimeException commitException,
          @Nullable RuntimeException rollbackException) {
        super(new TestTransactionFactory(beginTransactionException,
                processUpdateException, commitException, rollbackException),
            RetryHelper.NO_RETRY);
      }

      void runTest() throws Exception {
        try {
          processUpdate(new Update());
          fail();
        } catch (Error e) {
          assertEquals("RuntimeException from doProcessUpdate()", e.getMessage());
          assertEquals(testException, e.getCause());
        }
      }
    }

    new TestProcessor(testException, null, null, null).runTest();
    new TestProcessor(null, testException, null, null).runTest();
    new TestProcessor(null, null, testException, null).runTest();
    // Transaction.rollback() is called if something goes wrong before the
    // transaction is committed.  "Something goes wrong" in this case is
    // processUpdate() throwing testException2.  If rollback throws
    // testException, that masks the exception from processUpdate().  (It would
    // be fine not to mask the exception as well, so this assertion is an
    // overspecification; it's still useful since it verifies that the code
    // behaves as we expect.)
    new TestProcessor(null, testException2, null, testException).runTest();
  }

}
