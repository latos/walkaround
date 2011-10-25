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

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.common.net.UriEscapers;
import com.google.inject.Inject;
import com.google.walkaround.wave.server.auth.OAuthedFetchService;
import com.google.walkaround.wave.server.util.AbstractHandler;
import com.google.walkaround.wave.server.util.ProxyHandler;
import com.google.walkaround.wave.shared.SharedConstants;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles photo requests by proxying them to the m8 feed.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class PhotosHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(PhotosHandler.class.getName());

  private final OAuthedFetchService fetch;

  @Inject
  public PhotosHandler(OAuthedFetchService fetch) {
    this.fetch = fetch;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String photoId = requireParameter(req, "photoId");
    // http://code.google.com/apis/contacts/docs/2.0/developers_guide_protocol.html#groups_feed_url
    // says "You can also substitute 'default' for the user's email address,
    // which tells the server to return the contact groups for the user whose
    // credentials accompany the request".  I didn't read the document in enough
    // detail to check whether this is supposed to apply to photos as well, but
    // it seems to work.
    URL targetUrl = new URL("https://www.google.com/m8/feeds/photos/media/default/"
        + UriEscapers.uriQueryStringEscaper(false).escape(photoId));
    HTTPResponse response = fetch.fetch(
        new HTTPRequest(targetUrl, HTTPMethod.GET,
            FetchOptions.Builder.withDefaults().disallowTruncate()));
    if (response.getResponseCode() == 404) {
      // Rather than a broken image, use the unknown avatar.
      log.info("Missing image, using default");
      resp.sendRedirect(SharedConstants.UNKNOWN_AVATAR_URL);
    } else {
      ProxyHandler.copyResponse(response, resp);
    }
    // TODO(hearnden): Do something to make the client cache photos sensibly.
  }
}
