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

import com.google.common.collect.ImmutableList;
import com.google.common.net.UriEscapers;
import com.google.gxp.base.GxpContext;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.auth.InvalidSecurityTokenException;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.WaveletCreator;
import com.google.walkaround.wave.server.auth.XsrfHelper;
import com.google.walkaround.wave.server.auth.XsrfHelper.XsrfTokenExpiredException;
import com.google.walkaround.wave.server.gxp.InboxDisplayRecord;
import com.google.walkaround.wave.server.gxp.InboxFragment;
import com.google.walkaround.wave.server.gxp.NoSkin;
import com.google.walkaround.wave.server.index.Indexer;
import com.google.walkaround.wave.server.index.Indexer.UserIndexEntry;
import com.google.walkaround.wave.server.servlet.PageSkinWriter;
import com.google.walkaround.wave.server.util.AbstractHandler;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.waveprotocol.wave.model.wave.ParticipantId;

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

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(InboxHandler.class.getName());

  private static final String XSRF_ACTION = "inboxaction";

  @Inject ParticipantId participantId;
  @Inject XsrfHelper xsrfHelper;
  @Inject CheckedDatastore datastore;
  @Inject Indexer userIndex;
  @Inject WaveletCreator waveletCreator;
  @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;
  @Inject PageSkinWriter pageSkinWriter;

  private String queryEscape(String s) {
    return UriEscapers.uriQueryStringEscaper(false).escape(s);
  }

  private String makeWaveLink(SlobId objectId) {
    return "/wave?id=" + queryEscape(objectId.getId());
  }

  // XXX XXX Mess
  private List<InboxDisplayRecord> getWavesInner(String query) throws IOException {
    ImmutableList.Builder<InboxDisplayRecord> out = ImmutableList.builder();
    List<UserIndexEntry> waves = userIndex.findWaves(participantId, query, 0, 50);
    for (UserIndexEntry wave : waves) {
      out.add(new InboxDisplayRecord(
          wave.getCreator().getAddress(),
          wave.getTitle().trim(),
          wave.getSnippet().trim(),
          "" + new LocalDate(new Instant(wave.getLastModifiedMillis())),
          makeWaveLink(wave.getObjectId())));
    }
    return out.build();
  }

  private List<InboxDisplayRecord> getWaves(final String query) throws IOException {
    try {
      return new RetryHelper().run(
          new RetryHelper.Body<List<InboxDisplayRecord>>() {
            @Override public List<InboxDisplayRecord> run()
                throws RetryableFailure {
              try {
                return getWavesInner(query);
              } catch (IOException e) {
                throw new RetryableFailure(e);
              }
            }
          });
    } catch (PermanentFailure e) {
      throw new IOException("PermanentFailure reading index", e);
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    String query = req.getParameter("q");
    if (query == null) {
      query = "in:inbox";
    }
    List<InboxDisplayRecord> waveRecords = getWaves(query);
    NoSkin.write(resp.getWriter(), new GxpContext(req.getLocale()),
        "Walkaround", analyticsAccount,
        InboxFragment.getGxpClosure(xsrfHelper.createToken(XSRF_ACTION), waveRecords));
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
      resp.sendRedirect(makeWaveLink(newWaveId));
    } else {
      throw new BadRequestException("Unknown action: " + action);
    }
  }

}
