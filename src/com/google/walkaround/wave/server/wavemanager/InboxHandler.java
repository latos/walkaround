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

package com.google.walkaround.wave.server.wavemanager;

import com.google.gxp.base.GxpContext;
import com.google.gxp.html.HtmlClosure;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.auth.InvalidSecurityTokenException;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.WaveletCreator;
import com.google.walkaround.wave.server.auth.XsrfHelper;
import com.google.walkaround.wave.server.auth.XsrfHelper.XsrfTokenExpiredException;
import com.google.walkaround.wave.server.gxp.InboxDisplayRecord;
import com.google.walkaround.wave.server.gxp.InboxFragment;
import com.google.walkaround.wave.server.gxp.NoSkin;
import com.google.walkaround.wave.server.servlet.PageSkinWriter;
import com.google.walkaround.wave.server.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Lists waves relevant to the user.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class InboxHandler extends AbstractHandler {
  private static final int RESULTS_PER_PAGE = 30;

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(InboxHandler.class.getName());

  private static final String XSRF_ACTION = "inboxaction";
  private static final String DEFAULT_QUERY = "folder:inbox";

  @Inject XsrfHelper xsrfHelper;
  @Inject CheckedDatastore datastore;
  @Inject Searcher searcher;
  @Inject WaveletCreator waveletCreator;
  @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;
  @Inject PageSkinWriter pageSkinWriter;
  @Inject @Flag(FlagName.ANNOUNCEMENT_HTML) String announcementHtml;



  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    String query = req.getParameter("q");
    int page;
    try {
      String pageParam = req.getParameter("page");
      page = pageParam == null ? 0 : Integer.parseInt(pageParam);
    } catch (NumberFormatException e) {
      page = 0;
    }

    if (query == null || query.trim().isEmpty()) {
      query = DEFAULT_QUERY;
    }

    String displayQuery = query.equals(DEFAULT_QUERY) ? "" : query;
    List<InboxDisplayRecord> waveRecords = searcher.searchWaves(query,
        page * RESULTS_PER_PAGE, RESULTS_PER_PAGE);
    boolean embedded = "true".equals(req.getParameter("embedded"));
    NoSkin.write(resp.getWriter(), new GxpContext(req.getLocale()),
        "Walkaround", analyticsAccount,
        InboxFragment.getGxpClosure(xsrfHelper.createToken(XSRF_ACTION), displayQuery,
            embedded, embedded ? "wave" : "", waveRecords,
            // Only show announcement if mobile.
            RequestUtil.isMobile(req) ? announcementHtml() : null,
            page));
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      xsrfHelper.verify(XSRF_ACTION, requireParameter(req, "token"));
    } catch (XsrfTokenExpiredException e) {
      throw new BadRequestException(e);
    } catch (InvalidSecurityTokenException e) {
      throw new BadRequestException(e);
    }
    String action = requireParameter(req, "action");
    if ("newwave".equals(action)) {
      SlobId newWaveId = waveletCreator.newConvWithGeneratedId(null);
      // TODO(ohler): Send 303, not 302.  See
      // http://en.wikipedia.org/wiki/Post/Redirect/Get .
      resp.sendRedirect(Searcher.makeWaveLink(newWaveId));
    } else {
      throw new BadRequestException("Unknown action: " + action);
    }
  }

  private HtmlClosure announcementHtml() {
    return announcementHtml.isEmpty()
        ? null
        : new HtmlClosure() {
          @Override public void write(Appendable out, GxpContext context) throws IOException {
            out.append(announcementHtml);
          }
        };
  }
}
