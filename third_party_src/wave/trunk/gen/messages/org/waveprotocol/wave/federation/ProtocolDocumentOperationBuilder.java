/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.waveprotocol.wave.federation;

import org.waveprotocol.wave.federation.ProtocolDocumentOperation.Component;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation.Component.AnnotationBoundary;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation.Component.ElementStart;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation.Component.ReplaceAttributes;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation.Component.UpdateAttributes;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation.Component.KeyValuePair;
import org.waveprotocol.wave.federation.ProtocolDocumentOperation.Component.KeyValueUpdate;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder.ComponentBuilder;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder.ComponentBuilder.AnnotationBoundaryBuilder;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder.ComponentBuilder.ElementStartBuilder;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder.ComponentBuilder.ReplaceAttributesBuilder;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder.ComponentBuilder.UpdateAttributesBuilder;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder.ComponentBuilder.KeyValuePairBuilder;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder.ComponentBuilder.KeyValueUpdateBuilder;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationUtil;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationUtil.ComponentUtil;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationUtil.ComponentUtil.KeyValuePairUtil;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationUtil.ComponentUtil.KeyValueUpdateUtil;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationUtil.ComponentUtil.ElementStartUtil;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationUtil.ComponentUtil.ReplaceAttributesUtil;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationUtil.ComponentUtil.UpdateAttributesUtil;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationUtil.ComponentUtil.AnnotationBoundaryUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProtocolDocumentOperations.
 *
 * Generated from org/waveprotocol/wave/federation/federation.protodevel. Do not edit.
 */
public final class ProtocolDocumentOperationBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProtocolDocumentOperation create();
  }

  public static final class ComponentBuilder {

    /** Factory to pass to {@link #build()}. */
    public interface Factory {
      Component create();
    }

    public static final class KeyValuePairBuilder {

      /** Factory to pass to {@link #build()}. */
      public interface Factory {
        KeyValuePair create();
      }

      private String key;
      private String value;
      public KeyValuePairBuilder() {
      }

      public KeyValuePairBuilder setKey(String value) {
        this.key = value;
        return this;
      }

      public KeyValuePairBuilder setValue(String value) {
        this.value = value;
        return this;
      }

      /** Builds a {@link KeyValuePair} using this builder and a factory. */
      public KeyValuePair build(Factory factory) {
        KeyValuePair message = factory.create();
        message.setKey(key);
        message.setValue(value);
        return message;
      }

    }

    public static final class KeyValueUpdateBuilder {

      /** Factory to pass to {@link #build()}. */
      public interface Factory {
        KeyValueUpdate create();
      }

      private String key;
      private String oldValue;
      private String newValue;
      public KeyValueUpdateBuilder() {
      }

      public KeyValueUpdateBuilder setKey(String value) {
        this.key = value;
        return this;
      }

      public KeyValueUpdateBuilder clearOldValue() {
        oldValue = null;
        return this;
      }

      public KeyValueUpdateBuilder setOldValue(String value) {
        this.oldValue = value;
        return this;
      }

      public KeyValueUpdateBuilder clearNewValue() {
        newValue = null;
        return this;
      }

      public KeyValueUpdateBuilder setNewValue(String value) {
        this.newValue = value;
        return this;
      }

      /** Builds a {@link KeyValueUpdate} using this builder and a factory. */
      public KeyValueUpdate build(Factory factory) {
        KeyValueUpdate message = factory.create();
        message.setKey(key);
        message.setOldValue(oldValue);
        message.setNewValue(newValue);
        return message;
      }

    }

    public static final class ElementStartBuilder {

      /** Factory to pass to {@link #build()}. */
      public interface Factory {
        ElementStart create();
      }

      private String type;
      private final List<KeyValuePair> attribute = new ArrayList<KeyValuePair>();
      public ElementStartBuilder() {
      }

      public ElementStartBuilder setType(String value) {
        this.type = value;
        return this;
      }

      public ElementStartBuilder addAllAttribute(List<? extends KeyValuePair> messages) {
        for (KeyValuePair message : messages) {
          addAttribute(message);
        }
        return this;
      }

      public ElementStartBuilder setAttribute(int n, KeyValuePair message) {
        this.attribute.set(n, message);
        return this;
      }

      public ElementStartBuilder addAttribute(KeyValuePair message) {
        this.attribute.add(message);
        return this;
      }

      public ElementStartBuilder clearAttribute() {
        attribute.clear();
        return this;
      }

      /** Builds a {@link ElementStart} using this builder and a factory. */
      public ElementStart build(Factory factory) {
        ElementStart message = factory.create();
        message.setType(type);
        message.clearAttribute();
        message.addAllAttribute(attribute);
        return message;
      }

    }

    public static final class ReplaceAttributesBuilder {

      /** Factory to pass to {@link #build()}. */
      public interface Factory {
        ReplaceAttributes create();
      }

      private Boolean empty;
      private final List<KeyValuePair> oldAttribute = new ArrayList<KeyValuePair>();
      private final List<KeyValuePair> newAttribute = new ArrayList<KeyValuePair>();
      public ReplaceAttributesBuilder() {
      }

      public ReplaceAttributesBuilder clearEmpty() {
        empty = null;
        return this;
      }

      public ReplaceAttributesBuilder setEmpty(boolean value) {
        this.empty = value;
        return this;
      }

      public ReplaceAttributesBuilder addAllOldAttribute(List<? extends KeyValuePair> messages) {
        for (KeyValuePair message : messages) {
          addOldAttribute(message);
        }
        return this;
      }

      public ReplaceAttributesBuilder setOldAttribute(int n, KeyValuePair message) {
        this.oldAttribute.set(n, message);
        return this;
      }

      public ReplaceAttributesBuilder addOldAttribute(KeyValuePair message) {
        this.oldAttribute.add(message);
        return this;
      }

      public ReplaceAttributesBuilder clearOldAttribute() {
        oldAttribute.clear();
        return this;
      }

      public ReplaceAttributesBuilder addAllNewAttribute(List<? extends KeyValuePair> messages) {
        for (KeyValuePair message : messages) {
          addNewAttribute(message);
        }
        return this;
      }

      public ReplaceAttributesBuilder setNewAttribute(int n, KeyValuePair message) {
        this.newAttribute.set(n, message);
        return this;
      }

      public ReplaceAttributesBuilder addNewAttribute(KeyValuePair message) {
        this.newAttribute.add(message);
        return this;
      }

      public ReplaceAttributesBuilder clearNewAttribute() {
        newAttribute.clear();
        return this;
      }

      /** Builds a {@link ReplaceAttributes} using this builder and a factory. */
      public ReplaceAttributes build(Factory factory) {
        ReplaceAttributes message = factory.create();
        message.setEmpty(empty);
        message.clearOldAttribute();
        message.addAllOldAttribute(oldAttribute);
        message.clearNewAttribute();
        message.addAllNewAttribute(newAttribute);
        return message;
      }

    }

    public static final class UpdateAttributesBuilder {

      /** Factory to pass to {@link #build()}. */
      public interface Factory {
        UpdateAttributes create();
      }

      private Boolean empty;
      private final List<KeyValueUpdate> attributeUpdate = new ArrayList<KeyValueUpdate>();
      public UpdateAttributesBuilder() {
      }

      public UpdateAttributesBuilder clearEmpty() {
        empty = null;
        return this;
      }

      public UpdateAttributesBuilder setEmpty(boolean value) {
        this.empty = value;
        return this;
      }

      public UpdateAttributesBuilder addAllAttributeUpdate(List<? extends KeyValueUpdate> messages) {
        for (KeyValueUpdate message : messages) {
          addAttributeUpdate(message);
        }
        return this;
      }

      public UpdateAttributesBuilder setAttributeUpdate(int n, KeyValueUpdate message) {
        this.attributeUpdate.set(n, message);
        return this;
      }

      public UpdateAttributesBuilder addAttributeUpdate(KeyValueUpdate message) {
        this.attributeUpdate.add(message);
        return this;
      }

      public UpdateAttributesBuilder clearAttributeUpdate() {
        attributeUpdate.clear();
        return this;
      }

      /** Builds a {@link UpdateAttributes} using this builder and a factory. */
      public UpdateAttributes build(Factory factory) {
        UpdateAttributes message = factory.create();
        message.setEmpty(empty);
        message.clearAttributeUpdate();
        message.addAllAttributeUpdate(attributeUpdate);
        return message;
      }

    }

    public static final class AnnotationBoundaryBuilder {

      /** Factory to pass to {@link #build()}. */
      public interface Factory {
        AnnotationBoundary create();
      }

      private Boolean empty;
      private final List<String> end = new ArrayList<String>();
      private final List<KeyValueUpdate> change = new ArrayList<KeyValueUpdate>();
      public AnnotationBoundaryBuilder() {
      }

      public AnnotationBoundaryBuilder clearEmpty() {
        empty = null;
        return this;
      }

      public AnnotationBoundaryBuilder setEmpty(boolean value) {
        this.empty = value;
        return this;
      }

      public AnnotationBoundaryBuilder addAllEnd(List<String> values) {
        this.end.addAll(values);
        return this;
      }

      public AnnotationBoundaryBuilder setEnd(int n, String value) {
        this.end.set(n, value);
        return this;
      }

      public AnnotationBoundaryBuilder addEnd(String value) {
        this.end.add(value);
        return this;
      }

      public AnnotationBoundaryBuilder clearEnd() {
        end.clear();
        return this;
      }

      public AnnotationBoundaryBuilder addAllChange(List<? extends KeyValueUpdate> messages) {
        for (KeyValueUpdate message : messages) {
          addChange(message);
        }
        return this;
      }

      public AnnotationBoundaryBuilder setChange(int n, KeyValueUpdate message) {
        this.change.set(n, message);
        return this;
      }

      public AnnotationBoundaryBuilder addChange(KeyValueUpdate message) {
        this.change.add(message);
        return this;
      }

      public AnnotationBoundaryBuilder clearChange() {
        change.clear();
        return this;
      }

      /** Builds a {@link AnnotationBoundary} using this builder and a factory. */
      public AnnotationBoundary build(Factory factory) {
        AnnotationBoundary message = factory.create();
        message.setEmpty(empty);
        message.clearEnd();
        message.addAllEnd(end);
        message.clearChange();
        message.addAllChange(change);
        return message;
      }

    }

    private AnnotationBoundary annotationBoundary;
    private String characters;
    private ElementStart elementStart;
    private Boolean elementEnd;
    private Integer retainItemCount;
    private String deleteCharacters;
    private ElementStart deleteElementStart;
    private Boolean deleteElementEnd;
    private ReplaceAttributes replaceAttributes;
    private UpdateAttributes updateAttributes;
    public ComponentBuilder() {
    }

    public ComponentBuilder clearAnnotationBoundary() {
      annotationBoundary = null;
      return this;
    }

    public ComponentBuilder setAnnotationBoundary(AnnotationBoundary message) {
      this.annotationBoundary = message;
      return this;
    }

    public ComponentBuilder clearCharacters() {
      characters = null;
      return this;
    }

    public ComponentBuilder setCharacters(String value) {
      this.characters = value;
      return this;
    }

    public ComponentBuilder clearElementStart() {
      elementStart = null;
      return this;
    }

    public ComponentBuilder setElementStart(ElementStart message) {
      this.elementStart = message;
      return this;
    }

    public ComponentBuilder clearElementEnd() {
      elementEnd = null;
      return this;
    }

    public ComponentBuilder setElementEnd(boolean value) {
      this.elementEnd = value;
      return this;
    }

    public ComponentBuilder clearRetainItemCount() {
      retainItemCount = null;
      return this;
    }

    public ComponentBuilder setRetainItemCount(int value) {
      this.retainItemCount = value;
      return this;
    }

    public ComponentBuilder clearDeleteCharacters() {
      deleteCharacters = null;
      return this;
    }

    public ComponentBuilder setDeleteCharacters(String value) {
      this.deleteCharacters = value;
      return this;
    }

    public ComponentBuilder clearDeleteElementStart() {
      deleteElementStart = null;
      return this;
    }

    public ComponentBuilder setDeleteElementStart(ElementStart message) {
      this.deleteElementStart = message;
      return this;
    }

    public ComponentBuilder clearDeleteElementEnd() {
      deleteElementEnd = null;
      return this;
    }

    public ComponentBuilder setDeleteElementEnd(boolean value) {
      this.deleteElementEnd = value;
      return this;
    }

    public ComponentBuilder clearReplaceAttributes() {
      replaceAttributes = null;
      return this;
    }

    public ComponentBuilder setReplaceAttributes(ReplaceAttributes message) {
      this.replaceAttributes = message;
      return this;
    }

    public ComponentBuilder clearUpdateAttributes() {
      updateAttributes = null;
      return this;
    }

    public ComponentBuilder setUpdateAttributes(UpdateAttributes message) {
      this.updateAttributes = message;
      return this;
    }

    /** Builds a {@link Component} using this builder and a factory. */
    public Component build(Factory factory) {
      Component message = factory.create();
      message.setAnnotationBoundary(annotationBoundary);
      message.setCharacters(characters);
      message.setElementStart(elementStart);
      message.setElementEnd(elementEnd);
      message.setRetainItemCount(retainItemCount);
      message.setDeleteCharacters(deleteCharacters);
      message.setDeleteElementStart(deleteElementStart);
      message.setDeleteElementEnd(deleteElementEnd);
      message.setReplaceAttributes(replaceAttributes);
      message.setUpdateAttributes(updateAttributes);
      return message;
    }

  }

  private final List<Component> component = new ArrayList<Component>();
  public ProtocolDocumentOperationBuilder() {
  }

  public ProtocolDocumentOperationBuilder addAllComponent(List<? extends Component> messages) {
    for (Component message : messages) {
      addComponent(message);
    }
    return this;
  }

  public ProtocolDocumentOperationBuilder setComponent(int n, Component message) {
    this.component.set(n, message);
    return this;
  }

  public ProtocolDocumentOperationBuilder addComponent(Component message) {
    this.component.add(message);
    return this;
  }

  public ProtocolDocumentOperationBuilder clearComponent() {
    component.clear();
    return this;
  }

  /** Builds a {@link ProtocolDocumentOperation} using this builder and a factory. */
  public ProtocolDocumentOperation build(Factory factory) {
    ProtocolDocumentOperation message = factory.create();
    message.clearComponent();
    message.addAllComponent(component);
    return message;
  }

}