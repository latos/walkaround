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

package org.waveprotocol.box.profile.impl;

import org.waveprotocol.box.profile.ProfileRequest;
import org.waveprotocol.box.profile.ProfileRequestUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProfileRequest.
 *
 * Generated from org/waveprotocol/box/profile/profiles.proto. Do not edit.
 */
public class ProfileRequestImpl implements ProfileRequest {
  private final List<String> addresses = new ArrayList<String>();
  public ProfileRequestImpl() {
  }

  public ProfileRequestImpl(ProfileRequest message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProfileRequest message) {
    clearAddresses();
    for (String field : message.getAddresses()) {
      addAddresses(field);
    }
  }

  @Override
  public List<String> getAddresses() {
    return Collections.unmodifiableList(addresses);
  }

  @Override
  public void addAllAddresses(List<String> values) {
    this.addresses.addAll(values);
  }

  @Override
  public String getAddresses(int n) {
    return addresses.get(n);
  }

  @Override
  public void setAddresses(int n, String value) {
    this.addresses.set(n, value);
  }

  @Override
  public int getAddressesSize() {
    return addresses.size();
  }

  @Override
  public void addAddresses(String value) {
    this.addresses.add(value);
  }

  @Override
  public void clearAddresses() {
    addresses.clear();
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.addresses.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProfileRequestImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProfileRequest) {
      return ProfileRequestUtil.isEqual(this, (ProfileRequest) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProfileRequestUtil.getHashCode(this);
  }

}