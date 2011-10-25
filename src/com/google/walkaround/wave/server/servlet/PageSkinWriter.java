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

import com.google.gxp.base.GxpContext;
import com.google.gxp.html.HtmlClosure;
import com.google.inject.Inject;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.gxp.PageSkin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Helper to invoke {@code PageSkin.write()} with common arguments.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class PageSkinWriter {

  private final HttpServletRequest req;
  private final HttpServletResponse resp;
  private final String analyticsAccount;

  @Inject
  public PageSkinWriter(HttpServletRequest req,
      HttpServletResponse resp,
      @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount) {
    this.req = req;
    this.resp = resp;
    this.analyticsAccount = analyticsAccount;
  }

  public void write(String title, String userEmail, HtmlClosure content) throws IOException {
    PageSkin.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, title, userEmail, content);
  }

}
