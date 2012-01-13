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

import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.Nindo.NindoCursor;
import org.waveprotocol.wave.model.document.operation.NindoCursorDecorator;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.id.InvalidIdException;
import org.waveprotocol.wave.model.id.LegacyIdSerialiser;
import org.waveprotocol.wave.model.id.ModernIdSerialiser;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.StringMap;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Converts anchorWavelet attributes of the form googlewave.com!conv+root to
 * googlewave.com/conv+root and similar, since WaveletBasedConversation expects
 * the latter.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class PrivateReplyAnchorLegacyIdConverter extends NindoCursorDecorator {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(
      PrivateReplyAnchorLegacyIdConverter.class.getName());

  private final String documentId;

  public PrivateReplyAnchorLegacyIdConverter(String documentId, NindoCursor target) {
    super(target);
    this.documentId = documentId;
  }

  @Override
  public void elementStart(String type, Attributes attrs) {
    if ("conversation".equals(documentId) && "conversation".equals(type)) {
      StringMap<String> newAttrs = CollectionUtils.newStringMap();
      for (Map.Entry<String, String> entry : attrs.entrySet()) {
        if (!"anchorWavelet".equals(entry.getKey())) {
          newAttrs.put(entry.getKey(), entry.getValue());
        } else {
          String newValue;
          try {
            newValue = ModernIdSerialiser.INSTANCE.serialiseWaveletId(
                LegacyIdSerialiser.INSTANCE.deserialiseWaveletId(entry.getValue()));
          } catch (InvalidIdException e) {
            throw new InvalidInputException("Failed to convert anchorWavelet: " + attrs);
          }
          log.info("Replacing " + entry + " with value " + newValue);
          newAttrs.put(entry.getKey(), newValue);
        }
      }
      attrs = AttributesImpl.fromStringMap(newAttrs);
    }
    super.elementStart(type, attrs);
  }

}
