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

package com.google.walkaround.wave.shared;

import com.google.common.base.Preconditions;

import org.waveprotocol.wave.model.document.operation.automaton.DocumentSchema;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.id.WaveletName;
import org.waveprotocol.wave.model.operation.OperationException;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.schema.SchemaProvider;
import org.waveprotocol.wave.model.version.HashedVersion;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.data.DocumentFactory;
import org.waveprotocol.wave.model.wave.data.IndexedDocumentFactory;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;

import java.util.List;

/**
 * Utility class for building wavelets, applying deltas to existing wavelet
 * snapshots, etc.
 *
 * @author piotrkaleta@google.com (Piotr Kaleta)
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveletUtil {

  public static final DocumentFactory<?> DEFAULT_DOC_FACTORY =
      new IndexedDocumentFactory(new SchemaProvider() {
        @Override
        public DocumentSchema getSchemaForId(WaveletId waveletId, String documentId) {
          return DocumentSchema.NO_SCHEMA_CONSTRAINTS;
        }
      });

  public static WaveletDataImpl buildWaveletFromInitialOps(WaveletName waveletName,
      List<WaveletOperation> ops, DocumentFactory<?> docFactory) throws OperationException {
    Preconditions.checkArgument(!ops.isEmpty(), "Empty list of ops");
    WaveletDataImpl wavelet = newWaveletFromInitialOp(waveletName, ops.get(0), docFactory);
    applyOps(wavelet, ops);
    return wavelet;
  }

  public static WaveletDataImpl buildWaveletFromInitialOps(WaveletName waveletName,
      List<WaveletOperation> ops) throws OperationException {
    return buildWaveletFromInitialOps(waveletName, ops, DEFAULT_DOC_FACTORY);
  }

  private static WaveletDataImpl newWaveletFromInitialOp(
      WaveletName waveletName, WaveletOperation initialOp, DocumentFactory<?> docFactory) {
    ParticipantId creator = initialOp.getContext().getCreator();
    Preconditions.checkNotNull(creator, "Null creator");
    long createTime = initialOp.getContext().getTimestamp();

    return new WaveletDataImpl(waveletName.waveletId, creator, createTime,
        0, HashedVersion.unsigned(0),
        createTime, waveletName.waveId, docFactory);
  }

  public static void applyOps(WaveletDataImpl wavelet, List<WaveletOperation> ops)
      throws OperationException {
    for (int i = 0; i < ops.size(); i++) {
      ops.get(i).apply(wavelet);
    }
  }

}
