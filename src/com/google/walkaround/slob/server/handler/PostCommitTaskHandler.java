/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.google.walkaround.slob.server.handler;

import com.google.inject.Inject;
import com.google.walkaround.slob.server.PostCommitAction;
import com.google.walkaround.slob.server.SlobStoreSelector;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Task queue handler that processes {@link PostCommitAction}s.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class PostCommitTaskHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(PostCommitTaskHandler.class.getName());

  public static String STORE_TYPE_PARAM = "store_type";
  public static String SLOB_ID_PARAM = "slob_id";

  @Inject SlobStoreSelector storeSelector;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info(this + ": doPost()");
    if (req.getHeader("X-AppEngine-QueueName") == null) {
      throw new BadRequestException();
    }
    String storeType = requireParameter(req, STORE_TYPE_PARAM);
    SlobId slobId = new SlobId(requireParameter(req, SLOB_ID_PARAM));
    storeSelector.get(storeType).getPostCommitActionScheduler().taskInvoked(slobId);
  }

}
