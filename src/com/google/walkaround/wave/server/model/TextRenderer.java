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

package com.google.walkaround.wave.server.model;

import com.google.walkaround.util.server.HtmlEscaper;

import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.wave.data.ReadableWaveletData;

/**
 * Renders a wave as HTML text, for indexing. The rendering is just the text of
 * each document, arbitrarily ordered.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class TextRenderer {

  private TextRenderer() {}

  /**
   * Renders a wavelet into simple HTML.
   */
  public static String renderToHtml(ReadableWaveletData data) {
    return HtmlEscaper.HTML_ESCAPER.escape(renderToText(data));
  }

  /** Renders a wavelet into text. */
  public static String renderToText(ReadableWaveletData data) {
    StringBuilder b = new StringBuilder();
    for (String id : data.getDocumentIds()) {
      // TODO(ohler): Find out whether this should be restricted to blip ids.
      render(data.getDocument(id).getContent().asOperation(), b);
    }
    return b.toString();
  }

  /**
   * Renders a document as a paragraph of plain text.
   */
  private static void render(DocInitialization doc, final StringBuilder out) {
    doc.apply(new DocInitializationCursor() {
      @Override
      public void characters(String chars) {
        out.append(chars);
      }

      @Override
      public void elementStart(String type, Attributes attrs) {
        out.append(' ');
      }

      @Override
      public void elementEnd() {
        out.append(' ');
      }

      @Override
      public void annotationBoundary(AnnotationBoundaryMap map) {
        // Ignore
      }
    });
    out.append("\n");
  }
}
