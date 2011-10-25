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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.UIObject;

import org.waveprotocol.wave.client.common.util.DomHelper;
import org.waveprotocol.wave.client.common.util.DomHelper.JavaScriptEventListener;
import org.waveprotocol.wave.client.scheduler.Scheduler;

/**
 * Simple debug menu
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class DebugMenu {
  private final Element element;

  public DebugMenu() {
    element = Document.get().createDivElement();
    element.setClassName("debug-menu");
  }

  public void install() {
    Document.get().getBody().appendChild(element);
  }

  public void addItem(String text, final Scheduler.Task command) {
    Element elem = Document.get().createAnchorElement();
    elem.setInnerText(text);
    DomHelper.registerEventHandler(elem, "click", new JavaScriptEventListener() {
      @Override public void onJavaScriptEvent(String name, Event event) {
        command.execute();
      }
    });
    element.appendChild(elem);
  }

  public void setVisible(boolean visible) {
    UIObject.setVisible(element, visible);
  }
}
