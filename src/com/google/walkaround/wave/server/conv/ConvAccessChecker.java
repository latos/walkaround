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

package com.google.walkaround.wave.server.conv;

import com.google.inject.Inject;
import com.google.walkaround.slob.server.AccessChecker;
import com.google.walkaround.slob.server.AccessDeniedException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.wave.server.conv.PermissionCache.Permissions;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * {@link AccessChecker} for conversation wavelets.
 *
 * @author ohler@google.com (Christian Ohler)
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ConvAccessChecker implements AccessChecker {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ConvAccessChecker.class.getName());

  private final PermissionCache permissionCache;

  @Inject
  public ConvAccessChecker(PermissionCache permissionCache) {
    this.permissionCache = permissionCache;
  }

  @Override
  public void checkCanRead(SlobId id) throws AccessDeniedException {
    Permissions perms = getPerms(id);
    if (!perms.canRead()) {
      throw new AccessDeniedException("No read access to " + id + ": " + perms);
    }
  }

  @Override
  public void checkCanModify(SlobId id) throws AccessDeniedException {
    Permissions perms = getPerms(id);
    if (!perms.canWrite()) {
      throw new AccessDeniedException("No write access to " + id + ": " + perms);
    }
  }

  @Override
  public void checkCanCreate(SlobId id) throws AccessDeniedException {
    // SlobStoreImpl calls this on newObject(), but we only call that (on conv
    // wavelets) through WaveletCreator.newConvWithGeneratedId().  Creation of a
    // new object with a randomly-generated ID is always permitted.
  }

  private Permissions getPerms(SlobId objectId) {
    try {
      return permissionCache.getPermissions(objectId);
    } catch (IOException e) {
      // TODO(danilatos): Figure out a better way to handle this.
      // For now, crashing should trigger the appropriate retry logic in clients.
      throw new RuntimeException("Failed to get permissions for " + objectId, e);
    }
  }

}
