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

package com.google.walkaround.util.server.auth;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.walkaround.util.server.auth.DigestUtils2.Secret;

import java.util.logging.Logger;

/**
 * Creates and verifies security tokens.
 *
 * A token is generated from a visible part, a hidden part, and an HMAC.  It has
 * the form:
 *
 *   token = (hmac(hidden, visible), visible)
 *
 * To paraphrase, the HMAC covers the visible and hidden parts.  The visible
 * part remains visible in the token after signing and can be recovered, while
 * the hidden part only affects the HMAC and can not be recovered.  Verifying a
 * token requires knowing the hidden part.
 *
 * The hidden part doesn't have to be hard to predict (that's what the secret is
 * for); it's merely a mechanism for producing different tokens that have the
 * same visible part, or for adding more data to a token without making it
 * bigger.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class SecurityTokenHelper {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(SecurityTokenHelper.class.getName());

  private final Secret secret;

  @Inject public SecurityTokenHelper(Secret secret) {
    this.secret = secret;
  }

  private String getSignature(String hiddenPart, String visiblePart) {
    Preconditions.checkArgument(!hiddenPart.contains("\0"),
        "Hidden part contains NUL: %s", hiddenPart);
    return DigestUtils2.hexHmac(secret, hiddenPart + "\0" + visiblePart);
  }

  /** Output is not URL-safe. */
  public String createToken(String hiddenPart, String visiblePart) {
    return getSignature(hiddenPart, visiblePart) + " " + visiblePart;
  }

  public String verifyAndGetVisiblePart(String expectedHiddenPart, String purportedToken)
      throws InvalidSecurityTokenException {
    int space = purportedToken.indexOf(' ');
    if (space == -1) {
      throw new InvalidSecurityTokenException("No space: " + purportedToken);
    }
    String actualVisiblePart = purportedToken.substring(space + 1);
    String actualSignature = purportedToken.substring(0, space);
    String expectedSignature = getSignature(expectedHiddenPart, actualVisiblePart);
    if (!actualSignature.equals(expectedSignature)) {
      throw new InvalidSecurityTokenException(
          "Signature mismatch for actualVisiblePart " + actualVisiblePart
          + ", expectedHiddenPart " + expectedHiddenPart
          + " (expected signature " + expectedSignature + "): " + purportedToken);
    }
    return actualVisiblePart;
  }

}
