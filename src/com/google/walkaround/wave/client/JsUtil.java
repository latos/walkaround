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

package com.google.walkaround.wave.client;

import com.google.gwt.core.client.JavaScriptException;
import com.google.walkaround.slob.shared.MessageException;

import org.waveprotocol.wave.client.common.util.JsoView;

/**
 * Application-independent GWT utilities.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class JsUtil {

  public static JsoView eval(String data) throws MessageException {
    // TODO(danilatos): Use JSON.parse() instead of surrounding with parens
    // in browsers that support it.
    if (!data.startsWith("(")) {
      data = "(" + data + ")";
    }
    try {
      return evalNative(data);
    } catch (JavaScriptException e) {
      throw new MessageException(e);
    }
  }

  static native JsoView evalNative(String data) throws JavaScriptException /*-{
    return eval(data);
  }-*/;

}
