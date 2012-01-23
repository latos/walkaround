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

package com.google.walkaround.wave.server.index;

import org.waveprotocol.wave.model.conversation.TitleHelper;
import org.waveprotocol.wave.model.document.Document;
import org.waveprotocol.wave.model.document.Doc.E;
import org.waveprotocol.wave.model.document.util.DocHelper;
import org.waveprotocol.wave.model.document.util.Range;
import org.waveprotocol.wave.model.id.IdConstants;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;

import java.util.logging.Logger;

/**
*
* @author danilatos@google.com
*/
class Util {
  private static final Logger log = Logger.getLogger(Util.class.getName());

  static String extractTitle(WaveletDataImpl conv) {
    Document manifest = getDoc(conv, IdConstants.MANIFEST_DOCUMENT_ID);
    if (manifest == null) {
      return "";
    }
    E blipElem = DocHelper.getElementWithTagName(manifest, "blip");
    if (blipElem == null) {
      return "";
    }
    String rootBlipId = manifest.getAttribute(blipElem, "id");

    Document rootDoc = getDoc(conv, rootBlipId);
    if (rootDoc == null) {
      return "";
    }

    String title = TitleHelper.extractTitle(rootDoc).trim();
    if (!title.isEmpty()) {
      log.info("Got title");
      return title;
    }
    Range range = TitleHelper.findImplicitTitle(rootDoc);
    log.info("No title, inferring...");
    return DocHelper.getText(rootDoc, range.getStart(), range.getEnd());
  }

  private static Document getDoc(WaveletDataImpl conv, String id) {
    return (Document) conv.getDocument(id).getContent();
  }
}
