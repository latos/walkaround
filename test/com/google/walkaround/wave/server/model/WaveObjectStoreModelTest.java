// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.walkaround.wave.server.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.wave.shared.WaveSerializer;

import junit.framework.TestCase;

import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;
import org.waveprotocol.wave.model.operation.wave.BlipContentOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletBlipOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperationContext;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.util.List;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveObjectStoreModelTest extends TestCase {

  private static final WaveSerializer SERIALIZER = new WaveSerializer(
      new ServerMessageSerializer());
  private static final WaveObjectStoreModel MODEL = new WaveObjectStoreModel(
      new ServerMessageSerializer());

  private static final ParticipantId AUTHOR = ParticipantId.ofUnsafe("a@example.com");
  private static final long TIMESTAMP = 12345;
  private static final WaveletOperationContext DEFAULT_CONTEXT =
      new WaveletOperationContext(AUTHOR, TIMESTAMP, 1);
  private static final String DOC_ID = "doc_id";
  private static final ClientId CLIENT_ID_2 = new ClientId("client1");
  private static final ClientId CLIENT_ID_1 = new ClientId("client2");

  private WaveletOperation waveletOp(DocOp docOp) {
    return new WaveletBlipOperation(DOC_ID, new BlipContentOperation(DEFAULT_CONTEXT, docOp));
  }

  public void testBasicTransform() throws Exception {
    WaveletOperation op1 = waveletOp(new DocOpBuilder().characters("a").build());
    WaveletOperation op2 = waveletOp(new DocOpBuilder().characters("b").build());
    WaveletOperation expectedTransformedOp1 =
        waveletOp(new DocOpBuilder().characters("a").retain(1).build());
    List<String> result = MODEL.transform(
        ImmutableList.of(new ChangeData<String>(CLIENT_ID_1, SERIALIZER.serializeDelta(op1))),
        ImmutableList.of(new ChangeData<String>(CLIENT_ID_2, SERIALIZER.serializeDelta(op2))));
    WaveletOperation resultOp = SERIALIZER.deserializeDelta(Iterables.getOnlyElement(result));
    assertEquals(expectedTransformedOp1, resultOp);
  }

}
