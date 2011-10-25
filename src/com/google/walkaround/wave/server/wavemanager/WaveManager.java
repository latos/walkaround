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

import com.google.inject.Inject;
import com.google.walkaround.slob.server.SlobManager;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.wave.server.conv.PermissionCache;
import com.google.walkaround.wave.server.conv.PermissionCache.Permissions;

import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Implementation of {@link SlobManager} for walkaround waves.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class WaveManager implements SlobManager, PermissionCache.PermissionSource {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(WaveManager.class.getName());

  private final WaveIndex index;
  private final CheckedDatastore datastore;
  private final ParticipantId participantId;

  @Inject
  public WaveManager(WaveIndex index,
      CheckedDatastore datastore,
      ParticipantId participantId) {
    this.index = index;
    this.datastore = datastore;
    this.participantId = participantId;
  }

  @Override
  public Permissions getPermissions(final SlobId objectId) throws IOException {
    try {
      return new RetryHelper().run(new RetryHelper.Body<Permissions>() {
        @Override public Permissions run() throws RetryableFailure, PermanentFailure {
          CheckedTransaction tx = datastore.beginTransaction();
          try {
            WaveIndex.IndexEntry indexEntry = index.getEntry(tx, objectId);
            if (indexEntry == null) {
              log.info(objectId + " does not exist; " + participantId + " may not access");
              return new Permissions(false, false);
            } else if (indexEntry.getAcl().contains(participantId)) {
              log.info(objectId + " exists and " + participantId + " is on the ACL");
              return new Permissions(true, true);
            } else {
              log.info(objectId + " exists but " + participantId + " is not on the ACL");
              return new Permissions(false, false);
            }
          } finally {
            tx.rollback();
          }
        }
      });
    } catch (PermanentFailure e) {
      throw new IOException(e);
    }
  }

  @Override
  public void update(SlobId objectId, SlobIndexUpdate update) throws IOException {
    // Nothing to do for now since we index in our pre-commit hook.
  }

}
