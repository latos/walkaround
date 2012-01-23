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
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.gxp.ClientFragment;

import org.waveprotocol.wave.model.wave.ParticipantId;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ClientHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ClientHandler.class.getName());

  @Inject ParticipantId participantId;
  @Inject @Flag(FlagName.ANNOUNCEMENT_HTML) String announcementHtml;
  @Inject PageSkinWriter page;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    page.write("Walkaround", participantId.getAddress(),
        ClientFragment.getGxpClosure(announcementHtml()));
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
