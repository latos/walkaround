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

import com.google.appengine.api.users.User;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.walkaround.wave.server.auth.ServletAuthHelper.UserServiceAccountLookup;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter that initializes {@link UserContext} from App Engine's
 * {@link UserService}.  Leaves {@link UserContext.oAuthCredentials} null if no
 * OAuth token is stored for the user.
 *
 * @author ohler@google.com (Christian Ohler)
 */
@Singleton
public class InteractiveAuthFilter implements Filter {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(InteractiveAuthFilter.class.getName());

  private final Provider<UserServiceAccountLookup> accountLookup;
  private final Provider<ServletAuthHelper> helper;
  private final Provider<User> user;

  @Inject
  public InteractiveAuthFilter(Provider<UserServiceAccountLookup> accountLookup,
      Provider<ServletAuthHelper> helper,
      Provider<User> user) {
    this.accountLookup = accountLookup;
    this.helper = helper;
    this.user = user;
  }

  @Override
  public void init(FilterConfig config) {}

  @Override
  public void destroy() {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    final HttpServletRequest req = (HttpServletRequest) request;
    final HttpServletResponse resp = (HttpServletResponse) response;
    helper.get().filter(req, resp, filterChain, accountLookup.get(),
        new ServletAuthHelper.NeedNewOAuthTokenHandler() {
          @Override public void sendNeedTokenResponse() throws IOException {
            helper.get().redirectToOAuthPage(req, resp, user.get().getEmail());
          }
        });
  }

}
