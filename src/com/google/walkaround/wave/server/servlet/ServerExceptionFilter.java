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

package com.google.walkaround.wave.server.servlet;

import com.google.appengine.api.users.UserService;
import com.google.common.base.Throwables;
import com.google.gxp.html.HtmlClosure;
import com.google.gxp.html.HtmlClosures;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.walkaround.util.server.servlet.HttpException;
import com.google.walkaround.wave.server.gxp.ErrorPage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ohler@google.com (Christian Ohler)
 */
@Singleton
public class ServerExceptionFilter implements Filter {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ServerExceptionFilter.class.getName());

  /**
   * Whether the current user whose request we are currently processing is trusted
   * or untrusted.
   *
   * We keep this orthogonal to admin status for now.
   *
   * @author ohler@google.com (Christian Ohler)
   */
  public enum UserTrustStatus {
    UNTRUSTED,
    TRUSTED;
  }

  @Inject Provider<UserService> userService;
  @Inject Provider<UserTrustStatus> trusted;
  @Inject Provider<PageSkinWriter> pageSkinWriter;

  @Override public void init(FilterConfig filterConfig) {}

  @Override public void destroy() {}

  private void sendError(HttpServletRequest req, HttpServletResponse response, Level logLevel,
    int errorCode, String publicMessage, Throwable t) throws IOException {
    log.log(logLevel,
        t.getClass().getSimpleName() + "; sending " + errorCode + ": " + publicMessage,
        t);
    try {
      log.info("Trust status: " + trusted.get());
      boolean isTrusted = trusted.get() == UserTrustStatus.TRUSTED
          && req.getParameter("simulateUntrusted") == null;
      response.setStatus(errorCode);
      UserService service = userService.get();
      String userEmail = service.isUserLoggedIn()
          ? service.getCurrentUser().getEmail() : "(not logged in)";
      pageSkinWriter.get().write("Error " + errorCode, userEmail,
          ErrorPage.getGxpClosure(userEmail, "" + errorCode, isTrusted, publicMessage,
              renderInternalMessage(Throwables.getStackTraceAsString(t))));
      log.info("Successfully sent GXP error page");
    } catch (RuntimeException e) {
      // This can happen if the uncaught exception t occurred after the servlet
      // had already called response.setContentType().  In that case, jetty
      // throws an IllegalStateException("STREAM") when we call
      // response.getWriter() above.
      log.log(Level.SEVERE, "RuntimeException trying to serve error page", e);
      // Try a more primitive mechanism.
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "An error occurred while reporting an error, see logs.  Original error:  "
          + errorCode + " " + publicMessage);
      log.info("Successfully sent primitive error page");
    }
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
      throws IOException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) resp;
    try {
      filterChain.doFilter(req, resp);
    } catch (HttpException e) {
      sendError(request, response, Level.INFO, e.getResponseCode(), e.getPublicMessage(), e);
    } catch (Throwable t) {
      sendError(request, response, Level.SEVERE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "Internal server error", t);
    }
  }

  // TODO(danilatos): Move this into the template.
  private HtmlClosure renderInternalMessage(String message) {
    return HtmlClosures.fromHtml(
        com.google.walkaround.util.server.HtmlEscaper.HTML_ESCAPER.escape(message)
            .replaceAll("walkaround", "<b>walkaround</b>"));
  }
}
