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

import com.google.common.collect.Lists;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.writebatch.BatchingUpdateProcessorTest.Result;
import com.google.walkaround.util.server.writebatch.BatchingUpdateProcessorTest.Update;

import junit.framework.TestCase;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class RandomTest extends TestCase {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(RandomTest.class.getName());

  private static boolean randomChance(Random r, double p) {
    return r.nextDouble() < p;
  }

  private static void maybeFail(Random random, double permanentFailureLikelihood,
      double retryableFailureLikelihood)
      throws RetryableFailure, PermanentFailure {
    if (randomChance(random, permanentFailureLikelihood)) {
      throw new PermanentFailure();
    }
    if (randomChance(random, retryableFailureLikelihood)) {
      throw new RetryableFailure();
    }
  }

  private static int randomInt(Random random, int min, int max) {
    return random.nextInt(max - min + 1) + min;
  }

  private static int dummy = 0;

  private static void randomDelay(Random random, int minIterations, int maxIterations) {
    // NOTE(ohler): I originally had a randomized sleep() statements here, but
    // some superficial experiments showed that removing them (and having no
    // delays at all) was _more_ effective at detecting missing locks.
    // Presumably the thread scheduler aligns everything at millisecond (or even
    // 10ms) boundaries, and our critical sections are all much shorter than
    // that.  We now use a busy loop for delays.
    int iterations = randomInt(random, minIterations, maxIterations);
    for (int i = 0; i < iterations; i++) {
      // busy loop
      dummy++;
    }
  }

  private static class Transaction implements UpdateTransaction<Update, Result> {
    static class Parameters {
      double batchTooLargeLikelihood = 0.02;
      double processUpdatePermanentFailureLikelihood = 0.001;
      double processUpdateRetryableFailureLikelihood = 0.01;
      double commitPermanentFailureLikelihood = 0.005;
      double commitRetryableFailureLikelihood = 0.05;
      double updateRejectionLikelihood = 0.1;
      int processUpdateMinDelayIterations = 0;
      int processUpdateMaxDelayIterations = 100;
      int commitMinDelayIterations = 0;
      int commitMaxDelayIterations = 100;
    }

    private final Parameters params;
    private final Random random;
    private int updatesProcessed = 0;

    Transaction(Parameters params, Random random) {
      this.params = params;
      this.random = random;
    }

    @Override
    public Result processUpdate(Update update)
        throws BatchTooLargeException, RetryableFailure, PermanentFailure {
      randomDelay(random,
          params.processUpdateMinDelayIterations, params.processUpdateMaxDelayIterations);
      if (updatesProcessed > 0 && randomChance(random, params.batchTooLargeLikelihood)) {
        throw new BatchTooLargeException("Have already processed " + updatesProcessed);
      }
      maybeFail(random, params.processUpdatePermanentFailureLikelihood,
          params.processUpdateRetryableFailureLikelihood);
      updatesProcessed++;
      return new Result(randomChance(random, params.updateRejectionLikelihood));
    }

    @Override
    public void commit() throws RetryableFailure, PermanentFailure {
      randomDelay(random, params.commitMinDelayIterations, params.commitMaxDelayIterations);
      maybeFail(random, params.commitPermanentFailureLikelihood,
          params.commitRetryableFailureLikelihood);
    }

    @Override
    public void rollback() {
    }
  }

  private static class Factory implements TransactionFactory<Result, Transaction>{
    static class Parameters {
      double beginTransactionPermanentFailureLikelihood = 0.001;
      double beginTransactionRetryableFailureLikelihood = 0.01;
      int beginTransactionMinDelayIterations = 0;
      int beginTransactionMaxDelayIterations = 100;
      Transaction.Parameters transactionParameters = new Transaction.Parameters();
    }

    private final Parameters params;
    private final Random random;
    private int transactionsCreated = 0;

    Factory(Parameters params, Random random) {
      this.params = params;
      this.random = random;
    }

    @Override public Transaction beginTransaction() throws RetryableFailure, PermanentFailure {
      randomDelay(random,
          params.beginTransactionMinDelayIterations, params.beginTransactionMaxDelayIterations);
      maybeFail(random, params.beginTransactionPermanentFailureLikelihood,
          params.beginTransactionRetryableFailureLikelihood);
      transactionsCreated++;
      if (transactionsCreated % 100 == 0) {
        log.info("Transactions created: " + transactionsCreated);
      }
      return new Transaction(params.transactionParameters, random);
    }
  }

  private static class Processor extends BatchingUpdateProcessor<Update, Result, Transaction> {
    public Processor(Factory f) {
      super(f, new RetryHelper());
    }
  }

  private static class RequestRunnable implements Runnable {
    static class Parameters {
      int minRequestsPerThread = 1;
      int maxRequestsPerThread = 100;
    }

    private final Parameters params;
    private final Random random;
    private final Processor processor;

    RequestRunnable(Parameters params, Random random, Processor processor) {
      this.params = params;
      this.random = random;
      this.processor = processor;
    }

    private void sendRequest() {
      try {
        processor.processUpdate(new Update());
      } catch (PermanentFailure f) {
        log.log(Level.INFO, "Permanent failure", f);
      }
    }

    @Override public void run() {
      try {
        int numRequests = randomInt(random,
            params.minRequestsPerThread, params.maxRequestsPerThread);
        for (int i = 0; i < numRequests; i++) {
          sendRequest();
        }
      } catch (Throwable t) {
        log.log(Level.SEVERE, "Uncaught throwable", t);
        t.printStackTrace(System.err);
        System.exit(1);
      }
    }
  }

  private void runTestNoDeadlocksOrAssertionFailures(int numThreads) throws Exception {
    Processor p = new Processor(new Factory(new Factory.Parameters(), new SecureRandom()));
    List<Thread> threads = Lists.newArrayList();
    for (int i = 0; i < numThreads; i++) {
      Thread t = new Thread(
          new RequestRunnable(new RequestRunnable.Parameters(),
              // Every thread has its own Random to avoid the need for
              // synchronization.  We use SecureRandom to make sure the
              // sequences of random numbers are different.
              new SecureRandom(),
              p),
          "Request thread " + i);
      t.start();
      threads.add(t);
    }
    for (Thread t : threads) {
      log.info("Waiting for " + t);
      t.join();
    }
    log.info("Success");
  }

  public void testNoDeadlocksOrAssertionFailures() throws Exception {
    for (int i = 0; i < 100; i++) {
      runTestNoDeadlocksOrAssertionFailures(i % 10 + 1);
    }
  }

  // TODO(ohler): add a test that verifies the actual functionality rather than
  // just "no deadlocks or assertion failures".

}
