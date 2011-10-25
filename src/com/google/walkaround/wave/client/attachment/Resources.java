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

package com.google.walkaround.wave.client.attachment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * GWT resources for the upload form.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class Resources {

  interface Bundle extends ClientBundle {
    @Source("icon.png")
    ImageResource icon();

    @Source("Upload.css")
    Css css();
  }

  interface Css extends CssResource {
    String icon();
    String form();
    String row();
  }

  static final Css css = GWT.<Bundle>create(Bundle.class).css();

  // Must inject everything synchronously, for popups etc.
  static {
    StyleInjector.inject(css.getText(), true);
  }

  // Utility class.
  private Resources() {
  }
}
