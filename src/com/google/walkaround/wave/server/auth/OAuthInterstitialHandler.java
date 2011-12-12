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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.gxp.Auth;
import com.google.walkaround.wave.server.servlet.PageSkinWriter;
import com.google.walkaround.wave.server.util.AbstractHandler;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Shows the "enable wave" page.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class OAuthInterstitialHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(OAuthInterstitialHandler.class.getName());

  /** Binding annotation for the OAuth callback URL path. */
  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  public @interface CallbackPath { }

  /** Binding annotation for the OAuth scopes. */
  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  public @interface Scopes { }

  @Inject @Flag(FlagName.OAUTH_CLIENT_ID) String clientId;
  @Inject @CallbackPath String callbackPath;
  @Inject @Scopes String oAuthScopes;
  @Inject PageSkinWriter pageSkinWriter;
  @Inject User user;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String userEmail = user.getEmail();
    String originalRequest = requireParameter(req, "originalRequest");
    String authorizeUrl =
        new GoogleAuthorizationRequestUrl(clientId, callbackPath, oAuthScopes).build()
        // TODO(ohler): Find out if GoogleAuthorizationRequestUrl offers an API
        // to do this rather than appending the literal string.  Also consider
        // using server-side auto-approval rather than offline access ("perform
        // these operations when I'm not using the application") since that's
        // scary -- but import is meant to be able to run in the background over
        // a few hours, so maybe we do need it.
        // http://googlecode.blogspot.com/2011/10/upcoming-changes-to-oauth-20-endpoint.html
        // has more details.
        + "&access_type=offline&approval_prompt=force";

    log.info("userEmail=" + userEmail
        + ", originalRequest=" + originalRequest
        + ", authorizeUrl=" + authorizeUrl);

    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    pageSkinWriter.write("Enable walkaround", userEmail,
        Auth.getGxpClosure(authorizeUrl, originalRequest, userEmail));
  }

}
