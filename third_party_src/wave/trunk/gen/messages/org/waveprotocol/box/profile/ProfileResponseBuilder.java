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

import org.waveprotocol.box.profile.ProfileResponse.FetchedProfile;
import org.waveprotocol.box.profile.ProfileResponseBuilder.FetchedProfileBuilder;
import org.waveprotocol.box.profile.ProfileResponseUtil;
import org.waveprotocol.box.profile.ProfileResponseUtil.FetchedProfileUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for ProfileResponses.
 *
 * Generated from org/waveprotocol/box/profile/profiles.proto. Do not edit.
 */
public final class ProfileResponseBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    ProfileResponse create();
  }

  public static final class FetchedProfileBuilder {

    /** Factory to pass to {@link #build()}. */
    public interface Factory {
      FetchedProfile create();
    }

    private String address;
    private String name;
    private String imageUrl;
    private String profileUrl;
    public FetchedProfileBuilder() {
    }

    public FetchedProfileBuilder setAddress(String value) {
      this.address = value;
      return this;
    }

    public FetchedProfileBuilder setName(String value) {
      this.name = value;
      return this;
    }

    public FetchedProfileBuilder setImageUrl(String value) {
      this.imageUrl = value;
      return this;
    }

    public FetchedProfileBuilder clearProfileUrl() {
      profileUrl = null;
      return this;
    }

    public FetchedProfileBuilder setProfileUrl(String value) {
      this.profileUrl = value;
      return this;
    }

    /** Builds a {@link FetchedProfile} using this builder and a factory. */
    public FetchedProfile build(Factory factory) {
      FetchedProfile message = factory.create();
      message.setAddress(address);
      message.setName(name);
      message.setImageUrl(imageUrl);
      message.setProfileUrl(profileUrl);
      return message;
    }

  }

  private final List<FetchedProfile> profiles = new ArrayList<FetchedProfile>();
  public ProfileResponseBuilder() {
  }

  public ProfileResponseBuilder addAllProfiles(List<? extends FetchedProfile> messages) {
    for (FetchedProfile message : messages) {
      addProfiles(message);
    }
    return this;
  }

  public ProfileResponseBuilder setProfiles(int n, FetchedProfile message) {
    this.profiles.set(n, message);
    return this;
  }

  public ProfileResponseBuilder addProfiles(FetchedProfile message) {
    this.profiles.add(message);
    return this;
  }

  public ProfileResponseBuilder clearProfiles() {
    profiles.clear();
    return this;
  }

  /** Builds a {@link ProfileResponse} using this builder and a factory. */
  public ProfileResponse build(Factory factory) {
    ProfileResponse message = factory.create();
    message.clearProfiles();
    message.addAllProfiles(profiles);
    return message;
  }

}