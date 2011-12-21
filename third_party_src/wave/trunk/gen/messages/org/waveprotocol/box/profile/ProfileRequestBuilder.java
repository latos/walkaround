/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.waveprotocol.box.profile;

import org.waveprotocol.box.profile.ProfileRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProfileRequests.
 *
 * Generated from org/waveprotocol/box/profile/profiles.proto. Do not edit.
 */
public final class ProfileRequestBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProfileRequest create();
  }

  private final List<String> addresses = new ArrayList<String>();
  public ProfileRequestBuilder() {
  }

  public ProfileRequestBuilder addAllAddresses(List<String> values) {
    this.addresses.addAll(values);
    return this;
  }

  public ProfileRequestBuilder setAddresses(int n, String value) {
    this.addresses.set(n, value);
    return this;
  }

  public ProfileRequestBuilder addAddresses(String value) {
    this.addresses.add(value);
    return this;
  }

  public ProfileRequestBuilder clearAddresses() {
    addresses.clear();
    return this;
  }

  /** Builds a {@link ProfileRequest} using this builder and a factory. */
  public ProfileRequest build(Factory factory) {
    ProfileRequest message = factory.create();
    message.clearAddresses();
    message.addAllAddresses(addresses);
    return message;
  }

}