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

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.appengine.api.users.User;
import com.google.common.net.UriEscapers;
import com.google.gxp.base.GxpContext;
import com.google.inject.Inject;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.auth.OAuthInterstitialHandler.CallbackPath;
import com.google.walkaround.wave.server.gxp.AuthPopup;

import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the callback in the OAuth flow.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class OAuthCallbackHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(OAuthCallbackHandler.class.getName());

  @Inject AccountStore accountStore;
  @Inject UserContext userContext;
  @Inject @CallbackPath String callbackUrl;
  @Inject @Flag(FlagName.OAUTH_CLIENT_ID) String clientId;
  @Inject @Flag(FlagName.OAUTH_CLIENT_SECRET) String clientSecret;
  @Inject User user;
  @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;

  private void writeAccountRecordFromContext() throws IOException {
    try {
      accountStore.put(
          new AccountStore.Record(userContext.getUserId(), userContext.getParticipantId(),
              userContext.getOAuthCredentials()));
    } catch (PermanentFailure e) {
      throw new IOException("Failed to write account record", e);
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String errorCode = req.getParameter("error");
    if (errorCode != null) {
      String errorDescription = req.getParameter("error_description");
      log.info("error: " + errorCode + ", description: " + errorDescription);
      String errorMessage;
      if ("access_denied".equals(errorCode)) {
        errorMessage = "To enable these features, please click enable, then allow access.";
      } else {
        errorMessage = "An error occured (" + errorCode + "): " + errorDescription;
      }
      log.info("errorMessage=" + errorMessage);
      writeRegularError(req, resp, errorMessage);
      return;
    }

    String code = requireParameter(req, "code");
    log.info("code=" + code);

    log.info("clientId=" + clientId + ", clientSecret=" + clientSecret + ", code=" + code
        + ", callbackUrl=" + callbackUrl);
    GoogleAuthorizationCodeGrant authRequest = new GoogleAuthorizationCodeGrant(
        new UrlFetchTransport(), new JacksonFactory(), clientId, clientSecret, code, callbackUrl);

    AccessTokenResponse authResponse;
    try {
      authResponse = authRequest.execute();
    } catch (IOException e) {
      log.log(Level.WARNING, "Failed attempt, trying again", e);
      if (e instanceof HttpResponseException) {
        HttpResponseException f = (HttpResponseException) e;
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        f.getResponse().getRequest().getContent().writeTo(o);
        // TODO(ohler): Use correct character set.
        log.warning("content of rejected request: " + o.toString());
        log.warning("rejection response body: " + f.getResponse().parseAsString());
      }
      resp.sendRedirect(req.getRequestURI() + "?code=" + urlEncode(code) + "&tryagain=true");
      return;
    }
    String refreshToken = authResponse.refreshToken;
    String accessToken = authResponse.accessToken;

    if (refreshToken == null) {
      writeRegularError(req, resp, "Error gaining authorization: no refresh token");
      return;
    }
    if (accessToken == null) {
      writeRegularError(req, resp, "Error gaining authorization: no access token");
      return;
    }

    // TODO(ohler): Disable user switching in OAuth dialog once Google's OAuth
    // API supports that.  (We don't need to be too defensive about account
    // mismatches since there's no real harm in allowing a user to use another
    // Google account for contact information and to import waves from; but it
    // is confusing, so we should disable it.)
    userContext.setOAuthCredentials(new OAuthCredentials(refreshToken, accessToken));
    userContext.setUserId(new StableUserId(user.getUserId()));
    userContext.setParticipantId(ParticipantId.ofUnsafe(user.getEmail()));
    log.info("User context: " + userContext);
    writeAccountRecordFromContext();

    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    AuthPopup.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, null);
  }

  private void writeRegularError(HttpServletRequest req, HttpServletResponse resp,
      String errorMessage) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    AuthPopup.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, errorMessage);
  }

  private String urlEncode(String s) {
    return UriEscapers.uriQueryStringEscaper(false).escape(s);
  }

}
