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

package com.google.walkaround.slob.server;

import com.google.appengine.api.backends.BackendService;
import com.google.inject.Inject;
import com.google.walkaround.util.server.auth.DigestUtils2.Secret;
import com.google.walkaround.util.server.servlet.PermissionDeniedException;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Security checks for slob store requests.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
// If we add any other backend servlets, we should make this a servlet filter.
public class StoreAccessChecker {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(StoreAccessChecker.class.getName());

  public static final String WALKAROUND_TRUSTED_HEADER = "X-Walkaround-Trusted";

  private final BackendService backends;
  private final Secret secret;

  @Inject
  public StoreAccessChecker(BackendService backends, Secret secret) {
    this.backends = backends;
    this.secret = secret;
  }

  public void checkPermittedStoreRequest(HttpServletRequest req) {
    // Either Check A or B are sufficient, but we do both for defense in depth.
    boolean checkA, checkB;

    String headerSecret = req.getHeader(WALKAROUND_TRUSTED_HEADER);
    if (headerSecret == null) {
      log.warning("No store access: Missing header secret");
      checkA = false;
    } else if (!secret.getHexData().equals(headerSecret)) {
      log.warning("No store access: Wrong header secret " + obscured(headerSecret));
      checkA = false;
    } else {
      checkA = true;
    }

    boolean runningOnBackend = (backends.getCurrentBackend() != null);
    if (!runningOnBackend) {
      log.warning("Access check failure - Not running on backend! "
          + "(running additional checks before dying...)");
      checkB = false;
    } else {
      checkB = true;
    }

    if (checkA != checkB) {
      log.severe("Access checks mismatched: a=" + checkA + ", b=" + checkB);
    }

    if (checkA && checkB) {
      log.info("Store access permitted");
      return;
    } else {
      throw new PermissionDeniedException("No store access");
    }
  }

  /**
   * Obscures secret strings (short strings aren't obscured, but they aren't
   * very secure to begin with).
   */
  private String obscured(String str) {
    if (str.length() < 4) {
      return str;
    }
    return str.substring(0, 4) + "...";
  }
}
