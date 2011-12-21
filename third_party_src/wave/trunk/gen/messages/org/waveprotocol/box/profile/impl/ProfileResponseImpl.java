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

import org.waveprotocol.box.profile.ProfileResponse.FetchedProfile;
import org.waveprotocol.box.profile.impl.ProfileResponseImpl.FetchedProfileImpl;
import org.waveprotocol.box.profile.ProfileResponse;
import org.waveprotocol.box.profile.ProfileResponseUtil;
import org.waveprotocol.box.profile.ProfileResponse.FetchedProfile;
import org.waveprotocol.box.profile.ProfileResponseUtil.FetchedProfileUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of ProfileResponse.
 *
 * Generated from org/waveprotocol/box/profile/profiles.proto. Do not edit.
 */
public class ProfileResponseImpl implements ProfileResponse {

  public static class FetchedProfileImpl implements FetchedProfile {
    private String address;
    private String name;
    private String imageUrl;
    private String profileUrl;
    public FetchedProfileImpl() {
    }

    public FetchedProfileImpl(FetchedProfile message) {
      copyFrom(message);
    }

    @Override
    public void copyFrom(FetchedProfile message) {
      setAddress(message.getAddress());
      setName(message.getName());
      setImageUrl(message.getImageUrl());
      if (message.hasProfileUrl()) {
        setProfileUrl(message.getProfileUrl());
      } else {
        clearProfileUrl();
      }
    }

    @Override
    public String getAddress() {
      return address;
    }

    @Override
    public void setAddress(String value) {
      this.address = value;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String value) {
      this.name = value;
    }

    @Override
    public String getImageUrl() {
      return imageUrl;
    }

    @Override
    public void setImageUrl(String value) {
      this.imageUrl = value;
    }

    @Override
    public boolean hasProfileUrl() {
      return profileUrl != null;
    }

    @Override
    public void clearProfileUrl() {
      profileUrl = null;
    }

    @Override
    public String getProfileUrl() {
      return profileUrl;
    }

    @Override
    public void setProfileUrl(String value) {
      this.profileUrl = value;
    }

    /** Provided to subclasses to clear all fields, for example when deserializing. */
    protected void reset() {
      this.address = null;
      this.name = null;
      this.imageUrl = null;
      this.profileUrl = null;
    }

    @Override
    public boolean equals(Object o) {
      return (o instanceof FetchedProfileImpl) && isEqualTo(o);
    }

    @Override
    public boolean isEqualTo(Object o) {
      if (o == this) {
        return true;
      } else if (o instanceof FetchedProfile) {
        return FetchedProfileUtil.isEqual(this, (FetchedProfile) o);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return FetchedProfileUtil.getHashCode(this);
    }

  }

  private final List<FetchedProfileImpl> profiles = new ArrayList<FetchedProfileImpl>();
  public ProfileResponseImpl() {
  }

  public ProfileResponseImpl(ProfileResponse message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(ProfileResponse message) {
    clearProfiles();
    for (FetchedProfile field : message.getProfiles()) {
      addProfiles(new FetchedProfileImpl(field));
    }
  }

  @Override
  public List<FetchedProfileImpl> getProfiles() {
    return Collections.unmodifiableList(profiles);
  }

  @Override
  public void addAllProfiles(List<? extends FetchedProfile> messages) {
    for (FetchedProfile message : messages) {
      addProfiles(message);
    }
  }

  @Override
  public FetchedProfileImpl getProfiles(int n) {
    return new FetchedProfileImpl(profiles.get(n));
  }

  @Override
  public void setProfiles(int n, FetchedProfile message) {
    this.profiles.set(n, new FetchedProfileImpl(message));
  }

  @Override
  public int getProfilesSize() {
    return profiles.size();
  }

  @Override
  public void addProfiles(FetchedProfile message) {
    this.profiles.add(new FetchedProfileImpl(message));
  }

  @Override
  public void clearProfiles() {
    profiles.clear();
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.profiles.clear();
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof ProfileResponseImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ProfileResponse) {
      return ProfileResponseUtil.isEqual(this, (ProfileResponse) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return ProfileResponseUtil.getHashCode(this);
  }

}