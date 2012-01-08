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

import com.google.inject.Inject;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.servlet.AbstractHandler;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Deletes the current user's OAuth token.  This is useful to make walkaround
 * forget a token to test the OAuth flow.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class DeleteOAuthTokenHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(DeleteOAuthTokenHandler.class.getName());

  private final AccountStore accountStore;
  private final StableUserId userId;

  @Inject public DeleteOAuthTokenHandler(AccountStore accountStore,
      StableUserId userId) {
    this.accountStore = accountStore;
    this.userId = userId;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      accountStore.delete(userId);
    } catch (PermanentFailure e) {
      throw new IOException("Failed to delete token for " + userId);
    }
    resp.sendRedirect("/");
  }
}
