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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.base.Objects;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.appengine.MemcacheTable;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.auth.StableUserId;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Retrieves permissions, with caching.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class PermissionCache {

  public interface PermissionSource {
    /** Gets permissions for the current user. */
    Permissions getPermissions(SlobId slobId) throws IOException;
  }

  public static final class Permissions implements Serializable {
    // Really, canWrite > canRead, but yeah.
    private final boolean canRead;
    private final boolean canWrite;

    public Permissions(boolean canRead, boolean canWrite) {
      this.canRead = canRead;
      this.canWrite = canWrite;
    }

    public boolean canRead() {
      return canRead;
    }

    public boolean canWrite() {
      return canWrite;
    }

    @Override
    public String toString() {
      return "Perms("
          + (canRead() ? "r" : "")
          + (canWrite() ? "w" : "")
          + ")";
    }
  }

  private static class AccessKey implements Serializable {
    private final StableUserId userId;
    private final SlobId slobId;

    public AccessKey(StableUserId userId,
        SlobId slobId) {
      this.userId = checkNotNull(userId, "Null userId");
      this.slobId = checkNotNull(slobId, "Null slobId");
    }

    public StableUserId getUserId() {
      return userId;
    }

    public SlobId getSlobId() {
      return slobId;
    }

    @Override public String toString() {
      return "AccessKey(" + userId + ", " + slobId + ")";
    }

    @Override public final boolean equals(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof AccessKey)) { return false; }
      AccessKey other = (AccessKey) o;
      return Objects.equal(userId, other.userId)
          && Objects.equal(slobId, other.slobId);
    }

    @Override public final int hashCode() {
      return Objects.hashCode(userId, slobId);
    }
  }

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(PermissionCache.class.getName());

  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  public @interface PermissionCacheExpirationSeconds {}

  private static final String MEMCACHE_TAG = "UAC";

  private final MemcacheTable<AccessKey, Permissions> memcache;
  private final Random random;
  private final PermissionSource source;
  private final int expirationSeconds;
  private final StableUserId userId;

  @Inject
  public PermissionCache(MemcacheTable.Factory memcacheFactory,
      Random random,
      PermissionSource source,
      @PermissionCacheExpirationSeconds int expirationSeconds,
      StableUserId userId) {
    this.memcache = memcacheFactory.create(MEMCACHE_TAG);
    this.expirationSeconds = expirationSeconds;
    this.source = source;
    this.random = random;
    this.userId = userId;
  }

  public Permissions getPermissions(SlobId slobId) throws IOException {
    AccessKey key = new AccessKey(userId, slobId);
    Permissions p = memcache.get(key);
    if (p == null) {
      p = source.getPermissions(slobId);
      Assert.check(p != null);
      memcache.put(key, p, Expiration.byDeltaSeconds(
          (int) (expirationSeconds * (0.6 + 0.4 * random.nextDouble()))));
    }
    return p;
  }
}
