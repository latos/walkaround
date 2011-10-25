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
import com.google.walkaround.wave.shared.ContactsService.ContactList;

/**
 * A JSO DTO for Contacts.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class ContactListJsoImpl extends JavaScriptObject implements ContactList {

  protected ContactListJsoImpl() {}

  @Override
  public native int size() /*-{
    return this.length;
  }-*/;

  @Override
  public native ContactJsoImpl get(int i) /*-{
    return this[i];
  }-*/;
}
