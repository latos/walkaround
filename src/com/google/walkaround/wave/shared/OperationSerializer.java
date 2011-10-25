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

import com.google.walkaround.proto.ProtocolDocumentOperation;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.AnnotationBoundary;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.ElementStart;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.KeyValuePair;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.KeyValueUpdate;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.ReplaceAttributes;
import com.google.walkaround.proto.ProtocolDocumentOperation.Component.UpdateAttributes;
import com.google.walkaround.proto.ProtocolWaveletOperation;
import com.google.walkaround.proto.ProtocolWaveletOperation.MutateDocument;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.AttributesUpdate;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.DocOpCursor;
import org.waveprotocol.wave.model.operation.wave.AddParticipant;
import org.waveprotocol.wave.model.operation.wave.BlipContentOperation;
import org.waveprotocol.wave.model.operation.wave.BlipOperationVisitor;
import org.waveprotocol.wave.model.operation.wave.NoOp;
import org.waveprotocol.wave.model.operation.wave.RemoveParticipant;
import org.waveprotocol.wave.model.operation.wave.SubmitBlip;
import org.waveprotocol.wave.model.operation.wave.VersionUpdateOp;
import org.waveprotocol.wave.model.operation.wave.WaveletBlipOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperationVisitor;

import java.util.Map;

/**
 * A class for serializing operations.
 *
 * @author Alexandre Mah
 */
public class OperationSerializer {

  /**
   * A class that holds a single message that has been converted from a DocumentOperation.
   */
  private static class Visitor implements WaveletOperationVisitor, BlipOperationVisitor {
    private final ProtocolWaveletOperation message = MessageFactoryHelper.createWaveOp();
    private String blipId;

    @Override
    public void visitWaveletBlipOperation(WaveletBlipOperation op) {
      blipId = op.getBlipId();
      op.getBlipOp().acceptVisitor(this);
      blipId = null;
    }

    @Override
    public void visitBlipContentOperation(BlipContentOperation op) {
      DocOp mutationOp = op.getContentOp();

      message.setMutateDocument(createMutateDocumentMessage(blipId, mutationOp));
    }

    @Override
    public void visitAddParticipant(AddParticipant op) {
      message.setAddParticipant(op.getParticipantId().getAddress());
    }

    @Override
    public void visitNoOp(NoOp op) {
      message.setNoOp(true);
    }

    @Override
    public void visitVersionUpdateOp(VersionUpdateOp op) {
      // Not serialisable
    }

    @Override
    public void visitRemoveParticipant(RemoveParticipant op) {
      message.setRemoveParticipant(op.getParticipantId().getAddress());
    }

    @Override
    public void visitSubmitBlip(SubmitBlip op) {
      // NOTE(piotrkaleta): Not existing anymore
    }

    /**
     * @return The message representing the written operation.
     */
    ProtocolWaveletOperation getMessage() {
      return message;
    }
  }

  /**
   * Converts an operation to a message.
   *
   * @param operation The operation to convert.
   * @return The message representing the operation.
   */
  public static ProtocolWaveletOperation createMessage(WaveletOperation operation) {
    Visitor visitor = new Visitor();
    operation.acceptVisitor(visitor);
    return visitor.getMessage();
  }

  private static KeyValuePair createKeyValuePair(String key, String value) {
    KeyValuePair attribute = MessageFactoryHelper.createDocumentKeyValuePair();
    attribute.setKey(key);
    if (value != null) {
      attribute.setValue(value);
    }
    return attribute;
  }

  private static KeyValueUpdate createKeyValueUpdate(
      String key, String oldValue, String newValue) {
    KeyValueUpdate attribute = MessageFactoryHelper.createDocumentKeyValueUpdate();
    attribute.setKey(key);
    if (oldValue != null) {
      attribute.setOldValue(oldValue);
    }
    if (newValue != null) {
      attribute.setNewValue(newValue);
    }
    return attribute;
  }

  private static ElementStart createElementStart(String type, Attributes attrs) {
    ElementStart elementStart = MessageFactoryHelper.createDocumentElementStart();
    elementStart.setType(type);
    for (Map.Entry<String, String> attribute : attrs.entrySet()) {
      elementStart.addAttribute(
          createKeyValuePair(attribute.getKey(), attribute.getValue()));
    }
    return elementStart;
  }

  /**
   * Convert the given document mutation op into a message.
   *
   * @param blipId
   * @param mutationOp
   * @return MutateDocument
   */
  public static MutateDocument createMutateDocumentMessage(String blipId, DocOp mutationOp) {
    final ProtocolDocumentOperation mutation = createMutationOp(mutationOp);

    MutateDocument operation = MessageFactoryHelper.createBlipContentMutation();

    operation.setDocumentId(blipId);
    operation.setDocumentOperation(mutation);
    return operation;
  }

  /**
   * Convert the given document mutation op into a mutation op message
   * @param docOp
   * @return DocumentMutation
   */
  public static ProtocolDocumentOperation createMutationOp(DocOp docOp) {
    final ProtocolDocumentOperation mutation =
        MessageFactoryHelper.createDocumentOperation();

    docOp.apply(new DocOpCursor() {

      @Override
      public void retain(int itemCount) {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        component.setRetainItemCount(itemCount);
        mutation.addComponent(component);
      }

      @Override
      public void characters(String characters) {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        component.setCharacters(characters);
        mutation.addComponent(component);
      }

      @Override
      public void elementStart(String type, Attributes attributes) {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        component.setElementStart(createElementStart(type, attributes));
        mutation.addComponent(component);
      }

      @Override
      public void elementEnd() {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        component.setElementEnd(true);
        mutation.addComponent(component);
      }

      @Override
      public void deleteCharacters(String characters) {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        component.setDeleteCharacters(characters);
        mutation.addComponent(component);
      }

      @Override
      public void deleteElementStart(String type, Attributes attributes) {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        component.setDeleteElementStart(createElementStart(type, attributes));
        mutation.addComponent(component);
      }

      @Override
      public void deleteElementEnd() {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        component.setDeleteElementEnd(true);
        mutation.addComponent(component);
      }

      @Override
      public void replaceAttributes(Attributes oldAttributes, Attributes newAttributes) {
        Component component =
            MessageFactoryHelper.createDocumentOperationComponent();
        ReplaceAttributes replace =
            MessageFactoryHelper.createDocumentReplaceAttributes();
        if (oldAttributes.isEmpty() && newAttributes.isEmpty()) {
          replace.setEmpty(true);
        } else {
          for (Map.Entry<String, String> e : oldAttributes.entrySet()) {
            replace.addOldAttribute(createKeyValuePair(e.getKey(), e.getValue()));
          }
          for (Map.Entry<String, String> e : newAttributes.entrySet()) {
            replace.addNewAttribute(createKeyValuePair(e.getKey(), e.getValue()));
          }
        }
        component.setReplaceAttributes(replace);
        mutation.addComponent(component);
      }

      @Override
      public void updateAttributes(AttributesUpdate update) {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        UpdateAttributes updateAttributes =
            MessageFactoryHelper.createDocumentUpdateAttributes();
        int n = update.changeSize();
        if (n == 0) {
          updateAttributes.setEmpty(true);
        } else {
          for (int i = 0; i < n; i++) {
          updateAttributes.addAttributeUpdate(createKeyValueUpdate(
              update.getChangeKey(i), update.getOldValue(i), update.getNewValue(i)));
          }
        }
        component.setUpdateAttributes(updateAttributes);
        mutation.addComponent(component);
      }

      @Override
      public void annotationBoundary(AnnotationBoundaryMap map) {
        Component component = MessageFactoryHelper.createDocumentOperationComponent();
        AnnotationBoundary boundary =
            MessageFactoryHelper.createDocumentAnnotationBoundary();
        int endSize = map.endSize();
        int changeSize = map.changeSize();
        if (endSize == 0 && changeSize == 0) {
          boundary.setEmpty(true);
        } else {
          for (int i = 0; i < endSize; i++) {
            boundary.addEnd(map.getEndKey(i));
          }
          for (int i = 0; i < changeSize; i++) {
            boundary.addChange(createKeyValueUpdate(map.getChangeKey(i),
                map.getOldValue(i), map.getNewValue(i)));
          }
        }
        component.setAnnotationBoundary(boundary);
        mutation.addComponent(component);
      }
    });
    return mutation;
  }

}
