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

import org.waveprotocol.box.profile.ProfileResponse.*;
import org.waveprotocol.box.profile.ProfileResponse.FetchedProfile;
import org.waveprotocol.box.profile.ProfileResponseUtil.FetchedProfileUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.Iterator;
import java.util.List;

/**
 * Compares {@link ProfileResponse}s for equality.
 *
 * Generated from org/waveprotocol/box/profile/profiles.proto. Do not edit.
 */
public final class ProfileResponseUtil {

  public static final class FetchedProfileUtil {
    private FetchedProfileUtil() {
    }

    /** Returns true if m1 and m2 are structurally equal. */
    public static boolean isEqual(FetchedProfile m1, FetchedProfile m2) {
      if (!m1.getAddress().equals(m2.getAddress())) return false;
      if (!m1.getName().equals(m2.getName())) return false;
      if (!m1.getImageUrl().equals(m2.getImageUrl())) return false;
      if (m1.hasProfileUrl() != m2.hasProfileUrl()) return false;
      if (m1.hasProfileUrl() && !m1.getProfileUrl().equals(m2.getProfileUrl())) return false;
      return true;
    }

    /** Returns true if m1 and m2 are equal according to isEqual. */
    public static boolean areAllEqual(List<? extends FetchedProfile> m1,
    List<? extends FetchedProfile> m2) {
      if (m1.size() != m2.size()) return false;
      Iterator<? extends FetchedProfile> i1 = m1.iterator();
      Iterator<? extends FetchedProfile> i2 = m2.iterator();
      while (i1.hasNext()) {
        if (!isEqual(i1.next(), i2.next())) return false;
      }
      return true;
    }

    /** Returns a structural hash code of message. */
    public static int getHashCode(FetchedProfile message) {
      int result = 1;
      result = (31 * result) + message.getAddress().hashCode();
      result = (31 * result) + message.getName().hashCode();
      result = (31 * result) + message.getImageUrl().hashCode();
      result = (31 * result) + (message.hasProfileUrl() ? message.getProfileUrl().hashCode() : 0);
      return result;
    }

  }

  private ProfileResponseUtil() {
  }

  /** Returns true if m1 and m2 are structurally equal. */
  public static boolean isEqual(ProfileResponse m1, ProfileResponse m2) {
    if (!FetchedProfileUtil.areAllEqual(m1.getProfiles(), m2.getProfiles())) return false;
    return true;
  }

  /** Returns true if m1 and m2 are equal according to isEqual. */
  public static boolean areAllEqual(List<? extends ProfileResponse> m1,
  List<? extends ProfileResponse> m2) {
    if (m1.size() != m2.size()) return false;
    Iterator<? extends ProfileResponse> i1 = m1.iterator();
    Iterator<? extends ProfileResponse> i2 = m2.iterator();
    while (i1.hasNext()) {
      if (!isEqual(i1.next(), i2.next())) return false;
    }
    return true;
  }

  /** Returns a structural hash code of message. */
  public static int getHashCode(ProfileResponse message) {
    int result = 1;
    result = (31 * result) + message.getProfiles().hashCode();
    return result;
  }

}