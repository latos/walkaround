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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.waveprotocol.wave.model.document.indexed.IndexedDocument;
import org.waveprotocol.wave.model.document.operation.Automatons;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.algorithm.DocOpCollector;
import org.waveprotocol.wave.model.document.operation.algorithm.DocOpInverter;
import org.waveprotocol.wave.model.document.operation.algorithm.Transformer;
import org.waveprotocol.wave.model.document.operation.automaton.AutomatonDocument;
import org.waveprotocol.wave.model.document.operation.impl.DocOpUtil;
import org.waveprotocol.wave.model.document.raw.impl.Element;
import org.waveprotocol.wave.model.document.raw.impl.Node;
import org.waveprotocol.wave.model.document.raw.impl.Text;
import org.waveprotocol.wave.model.document.util.DocProviders;
import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.operation.OperationPair;
import org.waveprotocol.wave.model.operation.OperationRuntimeException;
import org.waveprotocol.wave.model.operation.TransformException;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator.Parameters;
import org.waveprotocol.wave.model.testing.RandomDocOpGenerator.RandomProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public final class ChannelTestUtil {
  private ChannelTestUtil(){}

  private static final TransformQueue.Transformer<DocOp> transformer =
      new TransformQueue.Transformer<DocOp>() {
        @Override
        public OperationPair<DocOp> transform(DocOp clientOp, DocOp serverOp) {
          try {
            return Transformer.transform(clientOp, serverOp);
          } catch (TransformException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public List<DocOp> compact(List<DocOp> clientOps) {
          if (clientOps.isEmpty()) {
            return clientOps;
          }
          DocOpCollector c = new DocOpCollector();
          for (DocOp op : clientOps) {
            c.add(op);
          }
          return ImmutableList.of(c.composeAll());
        }
      };

  public static class SometimesCompactingTransformer implements TransformQueue.Transformer<DocOp> {
    /** Set this to control compacting behaviour */
    public boolean compact = true;

    @Override
    public OperationPair<DocOp> transform(DocOp clientOp, DocOp serverOp) {
      return transformer.transform(clientOp, serverOp);
    }

    @Override
    public List<DocOp> compact(List<DocOp> clientOps) {
      if (compact) {
        return transformer.compact(clientOps);
      } else {
        return clientOps;
      }
    }
  }

  public static final TransformQueue.Transformer<DocOp> randomlyCompactingTransformer(
      final int seed) {
    return new TransformQueue.Transformer<DocOp>() {
      final Random r = new Random(seed);

      @Override
      public OperationPair<DocOp> transform(DocOp clientOp, DocOp serverOp) {
        return transformer.transform(clientOp, serverOp);
      }

      @Override
      public List<DocOp> compact(List<DocOp> clientOps) {
        if (r.nextBoolean()) {
          return clientOps;
        } else {
          return transformer.compact(clientOps);
        }
      }
    };
  }



  /**
   * Convenience wrapper around a document
   */
  static class Doc {
    final IndexedDocument<Node, Element, Text> doc = DocProviders.POJO.parse("");
    final AutomatonDocument autoDoc = Automatons.fromReadable(doc);
    void consume(DocOp op) throws OperationException {
      doc.consume(op);
    }
    Doc copy() {
      Doc copy = new Doc();
      try {
        copy.consume(doc.asOperation());
        return copy;
      } catch (OperationException e) {
        throw new OperationRuntimeException("Invalid other document", e);
      }
    }
    String repr() {
      return DocOpUtil.toXmlString(doc.asOperation());
    }
  }

  /**
   * Represents a message on the wire.
   */
  static class Package {
    /**
     * Whether the op originated from the client. This can be true even after
     * the op has gone through the server and been transformed, and sent back
     * to the client from the server.
     */
    final boolean clientSourced;
    final int appliesToRevision;
    final int resultingRevision;
    final ImmutableList<DocOp> ops;


    public Package(int appliesToRevision, boolean clientSourced, List<DocOp> ops) {
      this.appliesToRevision = appliesToRevision;
      this.resultingRevision = appliesToRevision + 1;
      this.clientSourced = clientSourced;
      this.ops = ImmutableList.copyOf(ops);
    }

    @Override public String toString() {
      return "[" + (clientSourced ? "C" : "S") + appliesToRevision + ", " + ops.size() + ops + "]";
    }
  }

  /** Restricts to one op in the package */
  static class UnitPackage extends Package {
    public UnitPackage(int appliesToRevision, boolean clientSourced, DocOp op) {
      super(appliesToRevision, clientSourced, Collections.singletonList(op));
    }

    DocOp onlyOp() {
      assert ops.size() == 1;
      return ops.get(0);
    }
  }

  /**
   * Simulation of the server and the internet between the client and server.
   */
  static class FakeServer {
    private final RandomProvider r;
    private final Parameters p;
    FakeServer(RandomProvider r, Parameters p) {
      this.r = r;
      this.p = p;
    }

    Doc doc = new Doc();
    int nextSendAppliesAtRevision = 0;
    int lastClientAppliedAtRevision = 0;
    /** Start of the history log */
    int baseVersion = 0;
    /** Current server revision */
    int endRevision = 0;
    /** Recent operation history */
    ArrayList<UnitPackage> history = new ArrayList<UnitPackage>();
    /** Operations in flight down the browserchannel to the client */
    LinkedList<UnitPackage> browserchannel = new LinkedList<UnitPackage>();
    /** Operations in flight on an xhr to the server */
    Package xhr;

    @Override public String toString() {
      return "Server{ " + baseVersion + " - " + endRevision + "\n  " +
          doc +
          "\n  h:" + history +
          "\n  b:" + browserchannel.size() + browserchannel +
          "\n  x:" + xhr + "\n}";
    }

    void sendToServer(int revision, List<DocOp> ops) {
      xhr = new Package(revision, true, Lists.newArrayList(ops));
    }

    /**
     * @return true if there was an op to send
     */
    boolean sendToClient() {
      assert nextSendAppliesAtRevision <= endRevision;
      if (nextSendAppliesAtRevision == endRevision) {
        return false;
      }
      browserchannel.add(opAppliedAt(nextSendAppliesAtRevision));
      nextSendAppliesAtRevision++;
      return true;
    }

    boolean channelPending() {
      return !browserchannel.isEmpty();
    }

    UnitPackage clientReceive() {
      return browserchannel.remove();
    }

    /**
     * @return true if there was a pending xhr request
     */
    boolean serverReceive() {
      if (xhr == null) {
        return false;
      }

      int appliesToRev = xhr.appliesToRevision;
      assert xhr.clientSourced;
      lastClientAppliedAtRevision = appliesToRev;

      List<DocOp> ops = Lists.newArrayList(xhr.ops);
      assert ops.size() > 0;
      xhr = null;

      for (int i = appliesToRev; i < endRevision; i++) {
        DocOp serverOp = opAppliedAt(i).onlyOp();
        for (int j = 0; j < ops.size(); j++) {
          OperationPair<DocOp> pair = transformer.transform(ops.get(j), serverOp);
          serverOp = pair.serverOp();
          ops.set(j, pair.clientOp());
        }
      }

      for (DocOp op : ops) {
        apply(op, true);
      }

      return true;
    }

    UnitPackage opAppliedAt(int revision) {
      return history.get(revision - baseVersion);
    }

    void randomServerOp() {
      apply(RandomDocOpGenerator.generate(r, p, doc.autoDoc), false);
    }

    void apply(DocOp op, boolean clientSourced) {
      try {
        doc.consume(op);
      } catch (OperationException e) {
        throw new RuntimeException(e);
      }
      history.add(new UnitPackage(endRevision, clientSourced, op));
      endRevision++;

      while (sendToClient()){}

      if (endRevision % 50 == 0) {
        discardOldHistory();
      }

      checkState();
    }

    Doc at(int historicalRevision) {
      assert historicalRevision >= 0 && historicalRevision <= endRevision;
      Doc copy = doc.copy();
      int rev = endRevision - 1;
      for (rev = endRevision - 1; rev >= historicalRevision; rev--) {
        try {
          copy.consume(DocOpInverter.invert(opAppliedAt(rev).onlyOp()));
        } catch (OperationException e) {
          throw new RuntimeException(e);
        }
      }
      return copy;
    }

    void discardOldHistory() {
      int opsToDiscard =
          Math.min(lastClientAppliedAtRevision, nextSendAppliesAtRevision) - baseVersion;
      baseVersion += opsToDiscard;
      history = Lists.newArrayList(history.subList(opsToDiscard, history.size()));
    }

    void checkState() {
      assert endRevision == history.size() + baseVersion;
    }

    int revision() {
      return endRevision;
    }

    boolean xhrInFlight() {
      return xhr != null;
    }
  }
}
