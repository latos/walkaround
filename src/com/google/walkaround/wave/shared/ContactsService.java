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

package com.google.walkaround.wave.shared;

/**
 * Fetches a segment of contacts from a user's contacts list.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public interface ContactsService {
  /** Maximum number of contacts that can be fetched with a single call. */
  int MAX_SIZE = 100;

  /**
   * A contact.
   */
  public interface Contact {
    /** Returns the email address; never null. */
    String getAddress();

    /** Returns the full name of this contact; may be null. */
    String getName();

    /** Returns the id of the photo for this contact; may be null. */
    String getPhotoId();
  }

  /**
   * A list of contacts.
   */
  public interface ContactList {
    /** @return number of contacts in this list. */
    int size();

    /** @return contact at {@code i}. */
    Contact get(int i);
  }

  /** Callback for results. */
  interface Callback {
    void onSuccess(ContactList result);
    void onFailure();
  }

  /**
   * Fetches a segment of a user's contacts list.
   *
   * @param from first index to retrieve, inclusive
   * @param to last index to retrieve, exclusive
   * @param callback callback to notify with results
   * @throws IllegalArgumentException if the range is invalid.
   */
  void fetch(int from, int to, Callback callback);
}
