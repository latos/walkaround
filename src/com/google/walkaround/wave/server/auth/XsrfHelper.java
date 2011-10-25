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

package com.google.walkaround.wave.server.auth;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.walkaround.util.server.auth.InvalidSecurityTokenException;
import com.google.walkaround.util.server.auth.SecurityTokenHelper;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;

import java.util.logging.Logger;

/**
 * Utility for creating and verifying XSRF tokens.
 *
 * @author ohler@google.com (Christian Ohler)
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class XsrfHelper {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(XsrfHelper.class.getName());

  public static class XsrfTokenExpiredException extends Exception {
    public XsrfTokenExpiredException() {
    }

    public XsrfTokenExpiredException(String message) {
      super(message);
    }

    public XsrfTokenExpiredException(Throwable cause) {
      super(cause);
    }

    public XsrfTokenExpiredException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  private final SecurityTokenHelper helper;
  private final long expiryMs;
  private final StableUserId userId;

  @Inject
  public XsrfHelper(SecurityTokenHelper helper,
      @Flag(FlagName.XSRF_TOKEN_EXPIRY_SECONDS) int expirySeconds,
      StableUserId userId) {
    Preconditions.checkArgument(!userId.getId().contains(" "),
        "userId contains spaces: %s", userId);
    this.helper = helper;
    this.expiryMs = expirySeconds * 1000;
    this.userId = userId;
  }

  private String makeHiddenPart(String action) {
    return userId.getId() + " " + action;
  }

  public String createToken(String action) {
    long timestamp = System.currentTimeMillis();
    return helper.createToken(makeHiddenPart(action), "" + timestamp);
  }

  /**
   * Verifies the token matches the current user and action and has not expired.
   */
  public void verify(String action, String token)
      throws XsrfTokenExpiredException, InvalidSecurityTokenException {
    String visiblePart = helper.verifyAndGetVisiblePart(makeHiddenPart(action), token);
    long tokenTime;
    try {
      tokenTime = Long.parseLong(visiblePart);
    } catch (NumberFormatException e) {
      throw new BadRequestException(e);
    }

    if (tokenTime + expiryMs < System.currentTimeMillis()) {
      throw new XsrfTokenExpiredException("Token expired: " + action + ", " + userId
          + ", " + token);
    }
  }

}
