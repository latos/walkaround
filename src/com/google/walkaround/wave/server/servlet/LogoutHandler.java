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

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gxp.base.GxpContext;
import com.google.inject.Inject;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.gxp.AuthPopup;
import com.google.walkaround.wave.server.util.AbstractHandler;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logs out the current user.  This is useful for switching between admin and
 * non-admin when running locally.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class LogoutHandler extends AbstractHandler {

  public static class SelfClosingPageHandler extends AbstractHandler {
    @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      AuthPopup.write(resp.getWriter(), new GxpContext(req.getLocale()),
          analyticsAccount, null);
    }
  }

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(LogoutHandler.class.getName());

  public LogoutHandler() {
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String redirect;
    if (req.getParameter("switchUser") != null) {
      redirect = "/switchSuccess";
    } else {
      redirect = "/loggedout.html";
    }
    resp.sendRedirect(UserServiceFactory.getUserService().createLogoutURL(redirect));
  }
}
