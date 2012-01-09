// Copyright 2012 Google Inc. All Rights Reserved.

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
