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

import com.google.common.collect.Lists;
import com.google.gxp.base.GxpContext;
import com.google.gxp.html.HtmlClosure;
import com.google.inject.Inject;
import com.google.walkaround.slob.server.MutationLog;
import com.google.walkaround.slob.server.SlobStoreSelector;
import com.google.walkaround.slob.server.MutationLog.DeltaIterator;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.StoreType;
import com.google.walkaround.wave.server.gxp.Admin;
import com.google.walkaround.wave.server.gxp.StoreViewFragment;

import org.waveprotocol.wave.model.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class StoreViewHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(StoreViewHandler.class.getName());

  @Inject CheckedDatastore datastore;
  @Inject SlobStoreSelector storeSelector;
  @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String storeType = optionalParameter(req, "storeType", "Wavelet");
    String id = optionalParameter(req, "id", "");
    String snapshotVersion = optionalParameter(req, "snapshotVersion", "");
    String historyStart = optionalParameter(req, "historyStart", "0");
    String historyEnd = optionalParameter(req, "historyEnd", "");

    @Nullable Long objectVersion = null;
    List<Pair<Long, String>> items = Lists.newArrayList();
    String snapshot = "";

    if (!id.isEmpty()) {
      try {
        SlobId objectId = new SlobId(id);
        CheckedTransaction tx = datastore.beginTransaction();
        try {
          MutationLog mutationLog = storeSelector.get(storeType).getMutationLogFactory()
              .create(tx, objectId);
          objectVersion = mutationLog.getVersion();
          if (!historyStart.isEmpty()) {
            long start = Long.parseLong(historyStart);
            DeltaIterator it = mutationLog.forwardHistory(start,
                historyEnd.isEmpty() ? start + 1000 : Long.parseLong(historyEnd));
            for (long version = start; it.hasNext(); version++) {
              items.add(Pair.of(version, "" + it.next()));
            }
          }
          if (!snapshotVersion.isEmpty()) {
            snapshot = mutationLog.reconstruct(Long.parseLong(snapshotVersion))
                .getState().snapshot();
          }
        } finally {
          tx.rollback();
        }
      } catch (NumberFormatException e) {
        throw new BadRequestException(e);
      } catch (PermanentFailure e) {
        throw new IOException(e);
      } catch (RetryableFailure e) {
        throw new IOException(e);
      }
    }

    HtmlClosure content = StoreViewFragment.getGxpClosure(
        storeType, id, objectVersion == null ? "" : ("" + objectVersion),
        historyStart, historyEnd, items, snapshotVersion, snapshot);
    resp.setContentType("text/html");
    Admin.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, content);
  }

}
