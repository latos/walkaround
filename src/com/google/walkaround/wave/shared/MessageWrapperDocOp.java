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

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.AttributesUpdate;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.DocOpComponentType;
import org.waveprotocol.wave.model.document.operation.DocOpCursor;
import org.waveprotocol.wave.model.document.operation.impl.AnnotationBoundaryMapImpl;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.AttributesUpdateImpl;
import org.waveprotocol.wave.model.document.operation.impl.DocOpUtil;
import org.waveprotocol.wave.model.document.operation.util.ImmutableStateMap.Attribute;
import org.waveprotocol.wave.model.document.operation.util.ImmutableUpdateMap.AttributeUpdate;
import org.waveprotocol.wave.model.document.util.DocOpScrub;

import java.util.ArrayList;
import java.util.List;

/**
 * Thin wrapper over DocumentOperation to provide a DocOp interface. This is
 * particularly important for efficiency. Especially on the client.
 *
 * Note, this class is purposely unchecked for speed.
 *
 * @author zdwang@google.com (David Wang)
 */
class MessageWrapperDocOp implements DocOp {

  /**
   * A provider which returns the ProtocolDocumentOperation to be used.
   *
   * @author reuben@google.com (Reuben Kan)
   */
  interface DocOpMessageProvider {
    ProtocolDocumentOperation getContent();
  }

  /**
   * Thrown by accessors on MessageWrapperDocOps when they detect that the
   * underlying data is invalid. Since MessageWrapperDocOp does not check the
   * validity of its underlying data on construction (for efficiency), and the
   * DocOp interface assumes valid data and thus does not declare any checked
   * exceptions, this is a RuntimeException.
   */
  public static class DelayedInvalidInputException extends RuntimeException {
    public DelayedInvalidInputException(String message) {
      super(message);
    }
    public DelayedInvalidInputException(Throwable cause) {
      super(cause);
    }
    public DelayedInvalidInputException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private final DocOpMessageProvider provider;
  private final boolean checkAttributes;

  /**
   * @param provider The provider of the message to wrap
   * @param checkAttributes If the attributes should be checked when you use this object.
   */
  public MessageWrapperDocOp(DocOpMessageProvider provider, boolean checkAttributes) {
    this.provider = provider;
    this.checkAttributes = checkAttributes;
  }

  @Override
  public void applyComponent(int i, DocOpCursor cursor) {
    Component component = provider.getContent().getComponent(i);
    applyComponent(cursor, component);
  }

  @Override
  public AnnotationBoundaryMap getAnnotationBoundary(int i) {
    Component component = provider.getContent().getComponent(i);
    return annotationBoundaryFrom(component.getAnnotationBoundary());
  }

  @Override
  public String getCharactersString(int i) {
    Component component = provider.getContent().getComponent(i);
    return component.getCharacters();
  }

  @Override
  public String getDeleteCharactersString(int i) {
    Component component = provider.getContent().getComponent(i);
    return component.getDeleteCharacters();
  }

  @Override
  public Attributes getDeleteElementStartAttributes(int i) {
    Component component = provider.getContent().getComponent(i);
    ElementStart elementStart = component.getDeleteElementStart();
    return attributesFrom(elementStart);
  }

  @Override
  public String getDeleteElementStartTag(int i) {
    Component component = provider.getContent().getComponent(i);
    ElementStart elementStart = component.getDeleteElementStart();
    return elementStart.getType();
  }

  @Override
  public Attributes getElementStartAttributes(int i) {
    Component component = provider.getContent().getComponent(i);
    ElementStart elementStart = component.getElementStart();
    return attributesFrom(elementStart);
  }

  @Override
  public String getElementStartTag(int i) {
    Component component = provider.getContent().getComponent(i);
    ElementStart elementStart = component.getElementStart();
    return elementStart.getType();
  }

  @Override
  public Attributes getReplaceAttributesNewAttributes(int i) {
    Component component = provider.getContent().getComponent(i);
    ReplaceAttributes r = component.getReplaceAttributes();
    return newAttributesFrom(r);
  }

  @Override
  public Attributes getReplaceAttributesOldAttributes(int i) {
    Component component = provider.getContent().getComponent(i);
    ReplaceAttributes r = component.getReplaceAttributes();
    return oldAttributesFrom(r);
  }

  @Override
  public int getRetainItemCount(int i) {
    Component component = provider.getContent().getComponent(i);
    return component.getRetainItemCount();
  }

  @Override
  public DocOpComponentType getType(int i) {
    Component component = provider.getContent().getComponent(i);
    return getType(component);
  }

  @Override
  public AttributesUpdate getUpdateAttributesUpdate(int i) {
    Component component = provider.getContent().getComponent(i);
    UpdateAttributes r = component.getUpdateAttributes();
    return attributesUpdateFrom(r);
  }

  @Override
  public int size() {
    return provider.getContent().getComponentSize();
  }

  @Override
  public void apply(DocOpCursor cursor) {
    for (int i = 0; i < provider.getContent().getComponentSize(); ++i) {
      Component component = provider.getContent().getComponent(i);
      applyComponent(cursor, component);
    }
  }

  /**
   * Gets the DocOpComponentType of the ProtocolDocumentOperation.Component.
   */
  private DocOpComponentType getType(ProtocolDocumentOperation.Component component) {
    // This if/elseif chain is not ideal; I'm sorting it by expected frequency.
    // TODO(ohler): Find out if this if/elseif chain is slow.
    if (component.hasRetainItemCount()) {
      return DocOpComponentType.RETAIN;
    } else if (component.hasAnnotationBoundary()) {
      return DocOpComponentType.ANNOTATION_BOUNDARY;
    } else if (component.hasCharacters()) {
      return DocOpComponentType.CHARACTERS;
    } else if (component.hasDeleteCharacters()) {
      return DocOpComponentType.DELETE_CHARACTERS;
    } else if (component.hasElementStart()) {
      return DocOpComponentType.ELEMENT_START;
    } else if (component.hasElementEnd()) {
      return DocOpComponentType.ELEMENT_END;
    } else if (component.hasDeleteElementStart()) {
      return DocOpComponentType.DELETE_ELEMENT_START;
    } else if (component.hasDeleteElementEnd()) {
      return DocOpComponentType.DELETE_ELEMENT_END;
    } else if (component.hasReplaceAttributes()) {
      return DocOpComponentType.REPLACE_ATTRIBUTES;
    } else if (component.hasUpdateAttributes()) {
      return DocOpComponentType.UPDATE_ATTRIBUTES;
    } else {
      throw new DelayedInvalidInputException("Fell through in getType: " + provider.getContent());
    }
  }

  /**
   * Applies a single component to the cursor.
   */
  private void applyComponent(DocOpCursor cursor, Component component) {
    // This if/elseif chain is not ideal; I'm sorting it by expected frequency.
    // TODO(ohler): Find out if this if/elseif chain is slow.
    if (component.hasRetainItemCount()) {
      cursor.retain(component.getRetainItemCount());
    } else if (component.hasAnnotationBoundary()) {
      cursor.annotationBoundary(annotationBoundaryFrom(component.getAnnotationBoundary()));
    } else if (component.hasCharacters()) {
      cursor.characters(component.getCharacters());
    } else if (component.hasDeleteCharacters()) {
      cursor.deleteCharacters(component.getDeleteCharacters());
    } else if (component.hasElementStart()) {
      ElementStart elementStart = component.getElementStart();
      cursor.elementStart(elementStart.getType(), attributesFrom(elementStart));
    } else if (component.hasElementEnd()) {
      if (!component.getElementEnd()) {
        throw new DelayedInvalidInputException("Element end present but false: " +
            provider.getContent());
      }
      cursor.elementEnd();
    } else if (component.hasDeleteElementStart()) {
      ElementStart elementStart = component.getDeleteElementStart();
      cursor.deleteElementStart(elementStart.getType(), attributesFrom(elementStart));
    } else if (component.hasDeleteElementEnd()) {
      if (!component.getDeleteElementEnd()) {
        throw new DelayedInvalidInputException("Delete element end present but false: " + provider);
      }
      cursor.deleteElementEnd();
    } else if (component.hasReplaceAttributes()) {
      ReplaceAttributes r = component.getReplaceAttributes();
      cursor.replaceAttributes(oldAttributesFrom(r), newAttributesFrom(r));
    } else if (component.hasUpdateAttributes()) {
      UpdateAttributes r = component.getUpdateAttributes();
      cursor.updateAttributes(attributesUpdateFrom(r));
    } else {
      throw new DelayedInvalidInputException("Fell through in applyComponent: " + provider);
    }
  }

  private Attributes createAttributesImpl(List<Attribute> sortedAttributes) {
    try {
      if (checkAttributes) {
        return AttributesImpl.fromSortedAttributes(sortedAttributes);
      } else {
        return AttributesImpl.fromSortedAttributesUnchecked(sortedAttributes);
      }
    } catch (IllegalArgumentException e) {
      throw new DelayedInvalidInputException("Invalid attributes: " + e, e);
    }
  }

  private AttributesUpdate createAttributesUpdateImpl(List<AttributeUpdate> sortedUpdates) {
    try {
      if (checkAttributes) {
        return AttributesUpdateImpl.fromSortedUpdates(sortedUpdates);
      } else {
        return AttributesUpdateImpl.fromSortedUpdatesUnchecked(sortedUpdates);
      }
    } catch (IllegalArgumentException e) {
      throw new DelayedInvalidInputException("Invalid attributes update: " + e, e);
    }
  }

  private Attributes attributesFrom(ElementStart message) {
    List<Attribute> attributes = new ArrayList<Attribute>();
    for (int i = 0; i < message.getAttributeSize(); i++) {
      KeyValuePair p = message.getAttribute(i);
      attributes.add(new Attribute(p.getKey(), p.getValue()));
    }
    return createAttributesImpl(attributes);
  }

  private Attributes oldAttributesFrom(ReplaceAttributes message) {
    List<Attribute> attributes = new ArrayList<Attribute>();
    for (int i = 0; i < message.getOldAttributeSize(); i++) {
      KeyValuePair p = message.getOldAttribute(i);
      attributes.add(new Attribute(p.getKey(), p.getValue()));
    }
    return createAttributesImpl(attributes);
  }

  private Attributes newAttributesFrom(ReplaceAttributes message) {
    List<Attribute> attributes = new ArrayList<Attribute>();
    for (int i = 0; i < message.getNewAttributeSize(); i++) {
      KeyValuePair p = message.getNewAttribute(i);
      attributes.add(new Attribute(p.getKey(), p.getValue()));
    }
    return createAttributesImpl(attributes);
  }

  private AttributesUpdate attributesUpdateFrom(UpdateAttributes message) {
    List<AttributeUpdate> updates = new ArrayList<AttributeUpdate>();
    for (int i = 0; i < message.getAttributeUpdateSize(); i++) {
      KeyValueUpdate p = message.getAttributeUpdate(i);
      updates.add(new AttributeUpdate(p.getKey(), p.hasOldValue() ? p.getOldValue() : null,
          p.hasNewValue() ? p.getNewValue() : null));
    }
    return createAttributesUpdateImpl(updates);
  }

  private static AnnotationBoundaryMap annotationBoundaryFrom(AnnotationBoundary message) {
    String[] endKeys = new String[message.getEndSize()];
    String[] changeKeys = new String[message.getChangeSize()];
    String[] changeOldValues = new String[message.getChangeSize()];
    String[] changeNewValues = new String[message.getChangeSize()];

    for (int i = 0; i < endKeys.length; i++) {
      endKeys[i] = message.getEnd(i);
    }
    for (int i = 0; i < changeKeys.length; i++) {
      KeyValueUpdate change = message.getChange(i);
      changeKeys[i] = change.getKey();
      changeOldValues[i] = change.hasOldValue() ? change.getOldValue() : null;
      changeNewValues[i] = change.hasNewValue() ? change.getNewValue() : null;
    }

    try {
      return AnnotationBoundaryMapImpl.builder()
          .initializationEnd(endKeys)
          .updateValues(changeKeys, changeOldValues, changeNewValues)
          .build();
    } catch (IllegalArgumentException e) {
      throw new DelayedInvalidInputException("Invalid annotation boundary: " + e, e);
    }
  }

  @Override
  public String toString() {
    return "MessageBased@" + Integer.toHexString(System.identityHashCode(this)) + "[" +
        DocOpUtil.toConciseString(DocOpScrub.maybeScrub(this)) + "]";
  }
}
