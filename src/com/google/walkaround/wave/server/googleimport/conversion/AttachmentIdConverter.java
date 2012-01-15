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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.walkaround.wave.server.attachment.AttachmentId;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.AttributesUpdate;
import org.waveprotocol.wave.model.document.operation.DocOpCursor;
import org.waveprotocol.wave.model.document.operation.Nindo.NindoCursor;
import org.waveprotocol.wave.model.document.operation.NindoCursorDecorator;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
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
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.StringMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Converts attachment ids.  Needs to run after the converter that strips the
 * "w:" prefixes.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class AttachmentIdConverter extends NindoCursorDecorator {

  private static final String ATTACHMENT_ID_ATTRIBUTE_NAME = "attachment";
  private static final String IMAGE_ELEMENT_TYPE = "image";

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AttachmentIdConverter.class.getName());

  private final Map<String, AttachmentId> mapping;

  public AttachmentIdConverter(Map<String, AttachmentId> mapping,
      NindoCursor target) {
    super(target);
    this.mapping = mapping;
  }

  @Override
  public void elementStart(String type, Attributes attrs) {
    if (IMAGE_ELEMENT_TYPE.equals(type)) {
      StringMap<String> newAttrs = CollectionUtils.newStringMap();
      for (Map.Entry<String, String> entry : attrs.entrySet()) {
        if (!ATTACHMENT_ID_ATTRIBUTE_NAME.equals(entry.getKey())) {
          newAttrs.put(entry.getKey(), entry.getValue());
        } else {
          String newValue;
          AttachmentId mapped = mapping.get(entry.getValue());
          if (mapped == null) {
            log.severe("Attachment id not found: " + entry);
            // Preserve; not sure this is a good idea but it's probably better
            // than dropping the value.
            newValue = entry.getValue();
          } else {
            log.info("Replacing " + entry + " with value " + mapped);
            newValue = mapped.getId();
          }
          newAttrs.put(entry.getKey(), newValue);
        }
      }
      attrs = AttributesImpl.fromStringMap(newAttrs);
    }
    super.elementStart(type, attrs);
  }

  // TODO(ohler): currently unused; consider deleting
  public static Set<String> findAttachmentIds(List<WaveletOperation> history) {
    final Set<String> out = Sets.newHashSet();
    for (WaveletOperation op : history) {
      op.acceptVisitor(new WaveletOperationVisitor() {
          @Override public void visitNoOp(NoOp op) {}
          @Override public void visitVersionUpdateOp(VersionUpdateOp op) {
            throw new AssertionError("Unexpected visitVersionUpdateOp(" + op + ")");
          }
          @Override public void visitAddParticipant(AddParticipant op) {}
          @Override public void visitRemoveParticipant(RemoveParticipant op) {}
          @Override public void visitWaveletBlipOperation(final WaveletBlipOperation waveletOp) {
            waveletOp.getBlipOp().acceptVisitor(new BlipOperationVisitor() {
                @Override public void visitBlipContentOperation(BlipContentOperation blipOp) {
                  blipOp.getContentOp().apply(new DocOpCursor() {
                    @Override public void annotationBoundary(AnnotationBoundaryMap m) {}
                    @Override public void characters(String chars) {}
                    @Override public void elementEnd() {}
                    @Override public void deleteCharacters(String chars) {}
                    @Override public void deleteElementEnd() {}
                    @Override public void replaceAttributes(Attributes a, Attributes b) {}
                    @Override public void retain(int count) {}
                    @Override public void updateAttributes(AttributesUpdate update) {}
                    @Override public void elementStart(String type, Attributes attrs) {
                      if (IMAGE_ELEMENT_TYPE.equals(type)) {
                        for (Map.Entry<String, String> entry : attrs.entrySet()) {
                          if (!ATTACHMENT_ID_ATTRIBUTE_NAME.equals(entry.getKey())) {
                            out.add(entry.getValue());
                          }
                        }
                      }
                    }
                    @Override public void deleteElementStart(String type, Attributes attrs) {
                      // We can ignore this since any attachment ids must also be in the
                      // corresponding elementStart().
                    }
                  });
                }
                @Override public void visitSubmitBlip(SubmitBlip blipOp) {
                  throw new AssertionError("Unexpected visitSubmitBlip(" + blipOp + ")");
                }
              });
          }
        });
    }
    return ImmutableSet.copyOf(out);
  }

}
