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

import com.google.common.collect.Lists;
import com.google.walkaround.slob.client.ChannelTestUtil.Doc;
import com.google.walkaround.slob.client.ChannelTestUtil.FakeServer;
import com.google.walkaround.slob.client.ChannelTestUtil.UnitPackage;

import junit.framework.TestCase;

import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.util.DocOpScrub;
import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator.Parameters;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator.RandomProvider;
import org.waveprotocol.wave.model.testing.RandomProviderImpl;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TransformQueueRandomTest extends TestCase {

  /**
   * Concurrent events, with different relative likelihoods.
   *
   * Some may be no-ops if they are not possible with the current state.
   * Likelihoods are crafted to ensure all interesting scenarios are well
   * exercised.
   */
  enum Event {
    // keep these alphabetical, for easy visual checking
    /** Client pulls a transformed op out of the queue and applies it */
    CLIENT_APPLY(9),
    /** Client sends an op into the queue */
    CLIENT_OP(5),
    /** Response with version number for sending ops */
    XHR_RESPONSE(2),
    /** Client receives an op (either its own or another op) from the browserchannel */
    CLIENT_RECEIVE(5),
    /** Flush all ops in the browserchannel (helps convergence to happen more often) */
    CLIENT_RECEIVE_ALL(5),
    /** Client sends out its queued ops */
    CLIENT_SEND(3),
    /** Server generates its own op */
    SERVER_OP(5),
    /** Server receives operations from the client submission */
    SERVER_RECEIVE(4);

    static final int total;
    static {
      int temp = 0;
      for (Event e : values()) {
        temp += e.likelihood;
      }
      total = temp;
    }

    int likelihood;

    private Event(int l) {
      likelihood = l;
    }
  }
  RandomProvider r;
  Parameters p;

  public void testSmallRandom() throws OperationException {
    long start = System.currentTimeMillis();
    new TransformQueueRandomTest().randomTest(7, 100);
    System.out.println("Time: " + (System.currentTimeMillis() - start) / 1000.0);
  }

  public void testRandom() throws OperationException {
    long start = System.currentTimeMillis();
    // Break the test up into 10 chunks because it slows down as the document grows.
    for (int i = 0; i < 20; i++) {
      if (i == 6) {
        // Failes the "useful test" test.
        continue;
      }
      System.out.println("\nSEED " + i + " ======");
      randomTest(i, 700);
    }
    System.out.println("Time: " + (System.currentTimeMillis() - start) / 1000.0);
  }

  private static final PrintStream NULL_STREAM = new PrintStream(new OutputStream() {
    @Override public void write(int b) { }
  });

  public void randomTest(int seed, int numIterations) throws OperationException {
    try {
      assert false;
      fail("Assertions not turned on");
    } catch (AssertionError e) { }
    Map<Event, Integer> eventCounts = new TreeMap<Event, Integer>();

    DocOpScrub.setShouldScrubByDefault(false);
    r = RandomProviderImpl.ofSeed(seed);
    p = new Parameters();
    p.setMaxOpeningComponents(5);
    Doc client = new Doc();
    FakeServer server = new FakeServer(r, p);
    LinkedList<Integer> xhrResponses = Lists.newLinkedList();

    TransformQueue<DocOp> q = new TransformQueue<DocOp>(
        ChannelTestUtil.randomlyCompactingTransformer(0));
    q.init(0);

    int[] scenario = new int[8];

//    PrintStream out = System.out;
    PrintStream out = NULL_STREAM;

    int numConvergences = 0;
    for (int iter = 0; iter < numIterations; iter++) {
      Event e = randomEvent();
      out.println("\n" + iter + " " + e + " ========");
      eventCounts.put(e, (eventCounts.containsKey(e) ? eventCounts.get(e) : 0) + 1);
      switch (e) {
      case CLIENT_OP:
        DocOp op = randomOp(client);
        q.clientOp(op);
        break;
      case CLIENT_SEND:
        if (!q.hasUnacknowledgedClientOps() && q.hasQueuedClientOps()) {
          server.sendToServer(q.revision(), q.pushQueuedOpsToUnacked());
        }
        break;
      case SERVER_OP:
        server.randomServerOp();
        break;
      case XHR_RESPONSE:
        while (!xhrResponses.isEmpty()) {
          int responseVersion = xhrResponses.remove();
          List<DocOp> ops = q.ackOpsIfVersionMatches(responseVersion);
        }
        break;
      case CLIENT_RECEIVE:
      case CLIENT_RECEIVE_ALL:
        while (server.channelPending()) {
          UnitPackage p = server.clientReceive();
          if (p.clientSourced) {
            out.println("client");
            if (!q.expectedAck(p.resultingRevision)) {
              q.ackClientOp(p.resultingRevision);
            }
          } else {
            q.serverOp(p.resultingRevision, p.onlyOp());
          }

          if (e == Event.CLIENT_RECEIVE) {
            break;
          }
        }
        break;
      case CLIENT_APPLY:
        if (q.hasServerOp()) {
          client.consume(q.removeServerOp());
        }
        break;
      case SERVER_RECEIVE:
        if (server.serverReceive()) {
          xhrResponses.add(server.revision());
        }
        break;
      default:
        throw new AssertionError("Unhandled event");
      }

//      out.println(server);
//      out.println(q);
//      out.println(client);

      // Extra checking, simulate convergence
      if (iter % 10 == 0) {
        Doc clientCopy = client.copy();
        Doc serverCopy = server.at(q.revision()).copy();
        for (DocOp pendingServerOp : q.serverOps) {
          clientCopy.consume(pendingServerOp);
        }
        for (DocOp pendingClientOp : q.unackedClientOps) {
          serverCopy.consume(pendingClientOp);
        }
        for (DocOp pendingClientOp : q.queuedClientOps) {
          serverCopy.consume(pendingClientOp);
        }
        assertEquals("Unequal states", clientCopy.repr(), serverCopy.repr());
      }

      boolean qco = q.hasQueuedClientOps();
      boolean uco = q.hasUnacknowledgedClientOps();
      boolean so = q.hasServerOp();

      if (!qco && !uco && !so) {
        out.println("CONVERGED STATE (" + iter + ") " + client.doc.size());
        numConvergences++;
        assertEquals("Unequal states", client.repr(), server.at(q.revision()).repr());
      }

      scenario[(qco ? 1<<2 : 0) + (uco ? 1<<1 : 0) + (so ? 1<<0 : 0)]++;
    }

    // Make sure the test was useful
    String msg = "numConvergeances: " + numConvergences + "/" + numIterations;
    System.out.println(msg);
    assertTrue(msg, numConvergences >= 5);
    assertTrue(msg, numConvergences < numIterations / 5);
    int minScenario = numIterations / 200;
    for (int i = 0; i < scenario.length; i++) {
      msg = "scenario[" + i + "]: " + scenario[i];
      System.out.println(msg);
      assertTrue(msg, scenario[i] >= minScenario);
    }
    for (Event e : eventCounts.keySet()) {
      System.out.println(e + " " + (1.0 * eventCounts.get(e) * Event.total / numIterations));
    }
  }

  private Event randomEvent() {
    int index = r.nextInt(Event.total);
    int tot = 0;
    for (Event e : Event.values()) {
      tot += e.likelihood;
      if (index < tot) {
        return e;
      }
    }

    throw new AssertionError("Bug");
  }

  private DocOp randomOp(Doc doc) throws OperationException {
    DocOp op = RandomDocOpGenerator.generate(r, p, doc.autoDoc);
    doc.consume(op);
    return op;
  }
}
