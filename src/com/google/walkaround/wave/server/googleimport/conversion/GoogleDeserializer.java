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

import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent.AnnotationBoundary;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent.Component;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent.ElementStart;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent.KeyValuePair;
import com.google.walkaround.proto.GoogleImport.GoogleDocumentContent.KeyValueUpdate;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.impl.AnnotationBoundaryMapImpl;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.DocInitializationBuilder;
import org.waveprotocol.wave.model.document.operation.util.ImmutableStateMap.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns Google legacy protobufs into model objects.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class GoogleDeserializer {

  private GoogleDeserializer() {}

  private static Attributes attributesFrom(ElementStart in) {
    List<Attribute> attributes = new ArrayList<Attribute>();
    for (KeyValuePair p : in.getAttributeList()) {
      attributes.add(new Attribute(p.getKey(), p.getValue()));
    }
    try {
      return AttributesImpl.fromSortedAttributes(attributes);
    } catch (IllegalArgumentException e) {
      throw new InvalidInputException("Invalid attributes: " + e, e);
    }
  }

  private static AnnotationBoundaryMap annotationBoundaryFrom(AnnotationBoundary in) {
    String[] endKeys = new String[in.getEndCount()];
    String[] changeKeys = new String[in.getChangeCount()];
    String[] changeOldValues = new String[in.getChangeCount()];
    String[] changeNewValues = new String[in.getChangeCount()];

    for (int i = 0; i < endKeys.length; i++) {
      endKeys[i] = in.getEnd(i);
    }
    for (int i = 0; i < changeKeys.length; i++) {
      KeyValueUpdate change = in.getChange(i);
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
      throw new InvalidInputException("Invalid annotation boundary: " + e, e);
    }
  }

  private static void applyComponent(Component component, DocInitializationBuilder out) {
    if (component.hasAnnotationBoundary()) {
      out.annotationBoundary(annotationBoundaryFrom(component.getAnnotationBoundary()));
    } else if (component.hasCharacters()) {
      out.characters(component.getCharacters());
    } else if (component.hasElementStart()) {
      ElementStart elementStart = component.getElementStart();
      out.elementStart(elementStart.getType(), attributesFrom(elementStart));
    } else if (component.hasElementEnd()) {
      if (!component.getElementEnd()) {
        throw new InvalidInputException("Element end present but false: " + component);
      }
      out.elementEnd();
    } else {
      throw new InvalidInputException("Fell through in applyComponent: " + component);
    }
  }

  public static DocInitialization deserializeContent(GoogleDocumentContent in) {
    DocInitializationBuilder out = new DocInitializationBuilder();
    for (Component component : in.getComponentList()) {
      applyComponent(component, out);
    }
    return out.build();
  }

}
