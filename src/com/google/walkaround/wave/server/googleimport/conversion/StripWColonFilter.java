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

/**
 * Strips the "w:" prefix from elements.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class StripWColonFilter extends NindoCursorDecorator {

  public StripWColonFilter(NindoCursor target) {
    super(target);
  }

  @Override
  public void elementStart(String type, Attributes attrs) {
    super.elementStart(strip(type), attrs);
  }

  private String strip(String type) {
    return type.startsWith("w:") ? type.substring(2) : type;
  }

}
