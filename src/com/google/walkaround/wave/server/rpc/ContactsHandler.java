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

package com.google.walkaround.wave.server.rpc;

import com.google.common.net.UriEscapers;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.util.ServiceException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.auth.UserContext;
import com.google.walkaround.wave.server.util.AbstractHandler;
import com.google.walkaround.wave.shared.SharedConstants;

import org.waveprotocol.wave.model.util.Pair;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Serves contact information for the signed-in user.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class ContactsHandler extends AbstractHandler {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ContactsHandler.class.getName());

  private final static int MAX_SIZE =
      com.google.walkaround.wave.shared.ContactsService.MAX_SIZE;

  private static final String FEED_URL = "https://www.google.com/m8/feeds/contacts/";
  private static final String PHOTO_URL = "https://www.google.com/m8/feeds/photos/media/";

  @Inject ParticipantId user;
  @Inject UserContext userContext;
  // Lazy because it will fail to instantiate if we don't have OAuth credentials.
  @Inject Provider<ContactsService> contacts;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String encodedAddress = UriEscapers.uriEscaper().escape(user.getAddress());
    if (!userContext.hasOAuthCredentials()) {
      // Return an empty set of contacts; the client will show unknown avatars
      // and never call PhotosHandler.
      printJson(new ContactFeed(), encodedAddress, resp);
      return;
    }
    Pair<Integer, Integer> range = getRange(req);
    log.info("Fetching contacts for: " + user + ", [" + range.first + ", " + range.second + ")");
    URL url = new URL(FEED_URL + encodedAddress + "/thin"
        + "?start-index=" + range.first + "&max-results=" + (range.second - range.first));
    ContactFeed results;
    try {
      results = contacts.get().getFeed(url, ContactFeed.class);
    } catch (ServiceException e) {
      throw new IOException("Contact fetch failed: ", e);
    }
    log.info("Fetched " + results.getEntries().size() + " contacts for " + user);

    // Support ?format=html for debugging.
    if ("html".equals(req.getParameter("format"))) {
      printHtml(results, encodedAddress, resp);
    } else {
      printJson(results, encodedAddress, resp);
    }
  }

  /**
   * Gets the request range, and validates it.
   */
  private static Pair<Integer, Integer> getRange(HttpServletRequest req)
      throws BadRequestException {
    String fromStr = req.getParameter("from");
    String toStr = req.getParameter("to");
    int from;
    int to;
    try {
      from = fromStr != null ? Integer.parseInt(fromStr) : 1;
      to = toStr != null ? Integer.parseInt(toStr) : (from + MAX_SIZE);
    } catch (NumberFormatException e) {
      throw new BadRequestException("Range not numeric: " + req.getQueryString(), e);
    }
    if (1 > from || from > to) {
      throw new BadRequestException("Invalid range: [" + from + ", " + to + ")");
    }
    if (to > from + MAX_SIZE) {
      throw new BadRequestException(
          "Range too large (max " + MAX_SIZE + "): [" + from + ", " + to + ")");
    }
    assert from <= to && to <= from + MAX_SIZE;
    return Pair.of(from, to);
  }

  /**
   * Prints a contact feed as pretty HTML into a servlet response. This feature
   * is only for debugging.
   */
  private void printHtml(ContactFeed results, String encodedAddress, HttpServletResponse resp)
      throws IOException {
    PrintWriter pw = resp.getWriter();
    pw.println("<html>");
    pw.println("<body>");
    for (ContactEntry e : results.getEntries()) {
      pw.println("<p>");
      pw.println(
          "<img src='/photos" + shortPhotoUrl(encodedAddress, e.getContactPhotoLink()) + "'>");
      pw.println(e.getTitle().getPlainText());
      pw.println("</p>");
    }
    pw.println("</body>");
    pw.println("</html>");
  }

  /**
   * Prints a contact feed as JSON into a servlet response.
   */
  private void printJson(ContactFeed results, String encodedAddress, HttpServletResponse resp)
      throws IOException {
    // Since the fetch API is index based, this handler must return exactly one
    // result per contact entry. If a contact has 0 email addresses, then an
    // empty contact is returned, since without the email address, the
    // information is useless. If a contact has >1 email addresses, a contact
    // object based on just the first one is returned, due to the
    // exactly-one-result-per-feed-entry constraint.
    JsonArray res = new JsonArray();
    for (ContactEntry e : results.getEntries()) {
      JsonObject contact = new JsonObject();
      List<Email> emails = e.getEmailAddresses();
      if (!emails.isEmpty()) {
        contact.add("a", new JsonPrimitive(emails.get(0).getAddress()));
        contact.add("n", new JsonPrimitive(e.getTitle().getPlainText()));
        contact.add("p", new JsonPrimitive(shortPhotoUrl(encodedAddress, e.getContactPhotoLink())));
      }
      res.add(contact);
    }
    resp.getWriter().print(SharedConstants.XSSI_PREFIX + "(" + res.toString() + ")");
  }

  /**
   * Produces a possibly-shortened path for a contact's photo.
   *
   * @param encodedAddress url-encoded address of the viewer
   * @param link contact link
   */
  private String shortPhotoUrl(String encodedAddress, Link link) {
    String baseUrl = PHOTO_URL + encodedAddress;
    String href = link.getHref();
    return href.startsWith(baseUrl) ? href.substring(baseUrl.length()) : href;
  }
}
