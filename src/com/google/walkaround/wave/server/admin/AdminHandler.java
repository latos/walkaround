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

package com.google.walkaround.wave.server.admin;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.gxp.base.GxpContext;
import com.google.gxp.html.HtmlClosure;
import com.google.inject.Inject;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.gxp.Admin;
import com.google.walkaround.wave.server.util.AbstractHandler;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Serves a page that has a few options for admins.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class AdminHandler extends AbstractHandler {
  private static final HtmlClosure EMPTY_CONTENT =  new HtmlClosure() {
    @Override public void write(Appendable out, GxpContext gxpContext) { }
  };

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AdminHandler.class.getName());

  @Inject UserService userService;
  @Inject User user;
  @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.warning("Admin page requested by " + user.getEmail());
    if (!userService.isUserAdmin()) {
      log.severe("Admin page requested by non-admin user!");
      throw new BadRequestException();
    }
    resp.setContentType("text/html");
    Admin.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, EMPTY_CONTENT);
  }
}
