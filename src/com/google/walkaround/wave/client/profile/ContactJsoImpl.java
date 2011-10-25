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

package com.google.walkaround.wave.client.profile;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.walkaround.wave.shared.ContactsService.Contact;

/**
 * A JSO DTO for a Contact.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class ContactJsoImpl extends JavaScriptObject implements Contact {

  protected ContactJsoImpl() {}

  @Override
  public native String getAddress() /*-{
    return this.a;
  }-*/;

  @Override
  public native String getName() /*-{
    return this.n;
  }-*/;

  @Override
  public native String getPhotoId() /*-{
    // String leading slash.
    return this.p.substring(1);
  }-*/;
}
