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

package com.google.walkaround.wave.server.googleimport.conversion;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

import org.waveprotocol.wave.model.document.indexed.IndexedDocument;
import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.Nindo;
import org.waveprotocol.wave.model.document.raw.impl.Element;
import org.waveprotocol.wave.model.document.raw.impl.Node;
import org.waveprotocol.wave.model.document.raw.impl.Text;
import org.waveprotocol.wave.model.document.util.DocProviders;
import org.waveprotocol.wave.model.operation.OperationException;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class DocumentHistoryConverter {

  private final Function<Nindo, Nindo> nindoConverter;
  private final IndexedDocument<Node, Element, Text> doc = DocProviders.POJO.parse("");

  public DocumentHistoryConverter(Function<Nindo, Nindo> nindoConverter) {
    this.nindoConverter = checkNotNull(nindoConverter, "Null nindoConverter");
  }

  public DocOp convertAndApply(Nindo old) throws OperationException {
    return doc.consumeAndReturnInvertible(nindoConverter.apply(old));
  }

  public DocOp convertAndApply(DocOp old) throws OperationException {
    return convertAndApply(Nindo.fromDocOp(old, false));
  }

  public DocInitialization getCurrentState() {
    return doc.asOperation();
  }

}
