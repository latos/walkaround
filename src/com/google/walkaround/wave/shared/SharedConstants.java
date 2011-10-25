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
 * Shared constants between server and client.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class SharedConstants {

  /**
   * Prefix added to data returned from XHRs to guard against XSSI attacks.
   *
   * See http://google-gruyere.appspot.com/part4
   */
  public static final String XSSI_PREFIX = "])}while(1);</x>//";

  public static final String UNKNOWN_AVATAR_URL = "/static/images/unknown.jpg";

  /**
   * Request parameter keys for referencing various values.
   */
  public static class Params {
    private Params() {}

    /** SignedObjectSession protobuf. */
    public static final String SESSION = "session";

    /** Object version. */
    public static final String REVISION = "version";

    /** Start version in a range (inclusive). */
    public static final String START_REVISION = "start";

    /** End version in a range (exclusive). */
    public static final String END_REVISION = "end";
  }

  /** Service names. */
  public static class Services {
    private Services() {}

    public static final String CHANNEL = "channel";
    public static final String CONNECT = "connect";
    public static final String HISTORY = "history";
    public static final String SUBMIT_DELTA = "submitdelta";
  }

}
