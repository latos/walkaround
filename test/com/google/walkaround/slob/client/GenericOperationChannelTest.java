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

package com.google.walkaround.slob.client;

import com.google.walkaround.slob.client.ChannelTestUtil.Doc;
import com.google.walkaround.slob.client.ChannelTestUtil.FakeServer;
import com.google.walkaround.slob.client.ChannelTestUtil.SometimesCompactingTransformer;
import com.google.walkaround.slob.client.ChannelTestUtil.UnitPackage;
import com.google.walkaround.slob.client.GenericOperationChannel.Listener;
import com.google.walkaround.slob.client.GenericOperationChannel.ReceiveOpChannel;
import com.google.walkaround.slob.client.GenericOperationChannel.SendOpService;
import com.google.walkaround.slob.client.GenericOperationChannel.State;
import com.google.walkaround.util.client.log.Logs.Log;

import junit.framework.TestCase;

import org.waveprotocol.wave.client.scheduler.Scheduler;
import org.waveprotocol.wave.client.scheduler.Scheduler.Schedulable;
import org.waveprotocol.wave.client.scheduler.Scheduler.Task;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.util.DocOpScrub;
import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator.Parameters;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator.RandomProvider;
import org.waveprotocol.wave.model.testing.RandomProviderImpl;
import org.waveprotocol.wave.model.util.FuzzingBackOffGenerator;

import java.util.List;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class GenericOperationChannelTest extends TestCase {

  /**
   * Verifies that only one task is scheduled at once.
   */
  class RestrictedTimerService extends StubTimerService {
    private Scheduler.Task currentTask;

    int expectedMinTimeLowerBound = 0;

    @Override
    public void schedule(Scheduler.Task task) {
      assertTrue(currentTask == null || currentTask == task);
      currentTask = task;
    }

    @Override
    public void scheduleDelayed(Task task, int minimumTime) {
      assert expectedMinTimeLowerBound != 0;
      assertNull(currentTask);
      assertTrue("Was: " + minimumTime,
          minimumTime >= expectedMinTimeLowerBound &&
          minimumTime < expectedMinTimeLowerBound * 2);

      currentTask = task;
    }

    @Override
    public boolean isScheduled(Schedulable job) {
      return currentTask == job;
    }

    @Override
    public void cancel(Schedulable job) {
      if (job == currentTask) {
        currentTask = null;
      }
    }

    void run() {
      assert hasTask();
      Scheduler.Task task = currentTask;
      currentTask = null;
      task.execute();
    }

    boolean hasTask() {
      return currentTask != null;
    }
  }

  /**
   * Verifies only one service request is made at a time (ignoring discarded
   * requests)
   */
  class RestrictedSendService implements SendOpService<DocOp> {
    private SendOpService.Callback oldCallback;
    private SendOpService.Callback callback;
    boolean opsWillReachServer = true;

    @Override
    public void requestRevision(SendOpService.Callback callback) {
      assertFalse(waiting());
      this.callback = callback;
      this.oldCallback = null;
    }

    @Override
    public void submitOperations(
        int revision, List<DocOp> operations, SendOpService.Callback callback) {
      assertFalse(waiting());
      this.callback = callback;
      this.oldCallback = null;

      if (opsWillReachServer) {
        server.sendToServer(revision, operations);
      }
    }

    void success(int version) {
      if (callback != null) {
        SendOpService.Callback tmp = callback;
        nullBoth();
        tmp.onSuccess(version);
      } else if (oldCallback != null) {
        oldCallback.onSuccess(version);
        oldCallback = null;
      }
    }

    void retryFailure() {
      if (callback != null) {
        SendOpService.Callback tmp = callback;
        nullBoth();
        tmp.onConnectionError(new Exception("dummy"));
      } else if (oldCallback != null) {
        oldCallback.onConnectionError(new Exception("dummy"));
        oldCallback = null;
      }
    }

    void nullBoth() {
      callback = null;
      oldCallback = null;
    }

    boolean waiting() {
      return callback != null;
    }

    @Override
    public void callbackNotNeeded(SendOpService.Callback callback) {
      assert callback == this.callback;
      this.oldCallback = this.callback;
      this.callback = null;
    }
  }


  FuzzingBackOffGenerator g = new FuzzingBackOffGenerator(1, 1000, 0);
  RestrictedTimerService timer = new RestrictedTimerService();

  ReceiveOpChannel.Listener<DocOp> channelListener;

  ReceiveOpChannel<DocOp> channel = new ReceiveOpChannel<DocOp>() {
    @Override public void connect(int revision, ReceiveOpChannel.Listener<DocOp> listener) {
      assert channelListener == null;
      channelListener = listener;
    }
    @Override public void disconnect() { }
  };
  RestrictedSendService service = new RestrictedSendService();

  private boolean notifiedClean = false;
  int operationsAcked = 0;
  int operationsReceived = 0;
  Listener<DocOp> listener = new Listener<DocOp>() {
    @Override public void onError(Throwable e) { }

    @Override public void onRemoteOp(DocOp dummy) {
      operationsReceived++;
    }

    @Override public void onAck(DocOp dummy, boolean clean) {
      assertFalse("notified clean twice in a row without becoming dirty in between", notifiedClean);
      notifiedClean = clean;
      operationsAcked++;
    }
  };


  RandomProvider r = RandomProviderImpl.ofSeed(7);
  Parameters p = new Parameters(); {
    p.setMaxOpeningComponents(5);
  }
  Doc clientDoc = new Doc();
  FakeServer server = new FakeServer(r, p);

  private final String clientSid = "me";
  private final String otherSid = "foo";
  private final SometimesCompactingTransformer transformer = new SometimesCompactingTransformer();

  GenericOperationChannel<DocOp> c = new GenericOperationChannel<DocOp>(
      g, timer, transformer, channel, service, listener, Log.DEV_NULL);

  @Override
  protected void setUp() throws Exception {
    try {
      assert false;
      fail("Assertions not turned on");
    } catch (AssertionError e) { }

    DocOpScrub.setShouldScrubByDefault(false);

    // Begin
    c.connect(0, clientSid);
  }

  public void testNormalAckXhrAckFirst() {
    sendOpsToServer(2, true);
    server.serverReceive();
    service.success(server.revision());
    // optimization should cause full ack before channel message
    assertEquals(0, operationsReceived);
    assertEquals(1, operationsAcked);
    assertTrue(c.isClean());

    channelMessages(1);
    assertEquals(0, operationsReceived);
    assertEquals(1, operationsAcked);
  }

  public void testNormalAckXhrAckFirstNotifiesCleanWithLastOpOnly() {
    transformer.compact = false;
    sendOpsToServer(2, true);
    server.serverReceive();
    service.success(server.revision());
    // optimization should cause full ack before channel message
    assertEquals(0, operationsReceived);
    assertEquals(2, operationsAcked);
    assertTrue(c.isClean());

    channelMessages(1);
    assertEquals(0, operationsReceived);
    assertEquals(2, operationsAcked);
  }

  public void testNormalAckChannelAckFirst() {
    sendOpsToServer(2, true);
    server.serverReceive();
    channelMessages(1);
    assertEquals(0, operationsReceived);
    assertEquals(1, operationsAcked);
    service.success(server.revision());
    assertEquals(0, operationsReceived);
    assertEquals(1, operationsAcked);
    assertTrue(c.isClean());
  }

  public void testNormalAckXhrAckFirstWithConcurrentOp() {
    sendOpsToServer(2, true);

    randomServerOps(1);
    server.serverReceive();

    service.success(server.revision());
    // optimization doesn't happen until channel message
    assertEquals(0, operationsAcked);
    assertFalse(c.isClean());

    channelMessages(2);
    assertEquals(1, operationsReceived);
    assertEquals(1, operationsAcked);
    assertTrue(c.isClean());
  }

  public void testNormalAckWithQueuedOps() {
    // Normal ack scenario, queued ops
    sendOpsToServer(2, true);
    queueMoreOps(3);
    server.serverReceive();
    service.success(server.revision());
    channelMessages(1);
    assertFalse(service.waiting());
    timer.run();
    assertTrue(service.waiting()); // sent next batch
    server.serverReceive();
    channelMessages(1);
    service.success(server.revision());
    assertTrue(c.isClean());
  }

  public void testSendWhileDelaying() {
    sendOpsToServer(2, false);
    timer.expectedMinTimeLowerBound = 1;
    service.retryFailure();
    localOps(1);
    timer.run();

    syncAndResendSuccessfully(1);
    assertEquals(State.ALL_ACKED, c.getState());

    timer.run();
    assertTrue(service.waiting()); // sent next batch
    server.serverReceive();
    channelMessages(1);
    service.success(server.revision());
    assertTrue(c.isClean());
  }

  public void testExponentialBackoffGrowsAndResets() {
    sendOpsToServer(2, false);

    timer.expectedMinTimeLowerBound = 1;
    service.retryFailure();
    assertEquals(State.DELAY_RESYNC, c.getState());
    timer.run();
    assertEquals(State.WAITING_SYNC, c.getState());

    timer.expectedMinTimeLowerBound = 1;
    service.retryFailure();
    assertEquals(State.DELAY_RESYNC, c.getState());
    timer.run();
    assertEquals(State.WAITING_SYNC, c.getState());

    timer.expectedMinTimeLowerBound = 2;
    service.retryFailure();
    assertEquals(State.DELAY_RESYNC, c.getState());
    timer.run();
    assertEquals(State.WAITING_SYNC, c.getState());

    timer.expectedMinTimeLowerBound = 3;
    service.retryFailure();
    assertEquals(State.DELAY_RESYNC, c.getState());
    timer.run();
    assertEquals(State.WAITING_SYNC, c.getState());

    syncAndResendSuccessfully(1);
    assertTrue(c.isClean()); // yay

    sendOpsToServer(2, false);

    timer.expectedMinTimeLowerBound = 1; // back off reset
    service.retryFailure();
    assertEquals(State.DELAY_RESYNC, c.getState());
    timer.run();
    assertEquals(State.WAITING_SYNC, c.getState());
  }

  private void syncAndResendSuccessfully(int num) {
    service.opsWillReachServer = true;
    service.success(0);
    assertTrue(service.waiting()); // retry immediately
    assertEquals(State.WAITING_ACK, c.getState());
    assert server.xhrInFlight();
    server.serverReceive();
    channelMessages(num);
    service.success(server.revision());
  }

  public void testChannelAckIgnoringXhrTemporaryFailure() {
    sendOpsToServer(2, true);
    server.serverReceive();
    channelMessages(1);
    service.retryFailure();
    assertTrue(c.isClean());
  }

  public void testHandleXhrFailureThenIgnoreAfterChannelAck() {
    sendOpsToServer(2, true);
    server.serverReceive();
    timer.expectedMinTimeLowerBound = 1;
    service.retryFailure();
    channelMessages(1);
    assertEquals(0, operationsReceived);
    assertTrue(c.isClean());
  }

  public void testXhrFailureHandledWaitOps() {
    // Xhr retriable failure handled, wait for ops to
    // come down after version sync xhr returns
    sendOpsToServer(2, false);
    timer.expectedMinTimeLowerBound = 1;
    service.retryFailure();
    randomServerOps(3);
    channelMessages(2);
    assertEquals(2, operationsReceived);
    timer.run();
    assertTrue(service.waiting());
    assertEquals(State.WAITING_SYNC, c.getState());
    service.success(c.revision() + 1);
    assertFalse(service.waiting()); // still have 1 more until version sync
    service.opsWillReachServer = true;
    channelMessages(1);
    assertEquals(3, operationsReceived);
    assertEquals(0, operationsAcked);
    assertTrue(service.waiting()); // now we've sent our retry
    server.serverReceive();
    channelMessages(1);
    assertEquals(3, operationsReceived);
    assertEquals(1, operationsAcked);
    assertTrue(c.isClean());
  }

  public void testXhrFailureHandledStraightAway() {
    // Xhr retriable failure handled, enough ops already
    // when xhr returns.
    sendOpsToServer(2, false);
    timer.expectedMinTimeLowerBound = 1;
    service.retryFailure();
    randomServerOps(2);
    channelMessages(2);
    assertEquals(2, operationsReceived);
    timer.run();
    assertTrue(service.waiting());
    assertEquals(State.WAITING_SYNC, c.getState());
    service.opsWillReachServer = true;
    service.success(c.revision());
    assertTrue(service.waiting()); // sent retry straight away
    randomServerOps(1);
    server.serverReceive();
    channelMessages(2);
    assertEquals(3, operationsReceived);
    assertTrue(c.isClean());
  }


  private void localOps(int num) {
    for (int i = 0; i < num; i++) {
      notifiedClean = false;
      c.send(randomOp(clientDoc));
    }
  }

  private void sendOpsToServer(int num, boolean success) {
    localOps(num);
    service.opsWillReachServer = success;
    timer.run();
    assertTrue(service.waiting());
    if (success) {
      assert server.xhrInFlight();
    }
  }

  private void queueMoreOps(int num) {
    localOps(num);
    if (timer.hasTask()) {
      timer.run();
    }
  }

  private void randomServerOps(int num) {
    for (int i = 0; i < num; i++) {
      server.randomServerOp();
    }
  }

  private void channelMessages(int num) {
    for (int i = 0; i < num; i++) {
      UnitPackage pkg = server.clientReceive();
      channelListener.onMessage(pkg.resultingRevision,
          pkg.clientSourced ? clientSid : otherSid, pkg.onlyOp());
    }
  }

  private DocOp randomOp(Doc doc) {
    DocOp op = RandomDocOpGenerator.generate(r, p, doc.autoDoc);
    try {
      doc.consume(op);
    } catch (OperationException e) {
      throw new RuntimeException(e);
    }
    return op;
  }
}
