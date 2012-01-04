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

package com.google.walkaround.wave.server.auth;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedPreparedQuery;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.appengine.DatastoreUtil;
import com.google.walkaround.util.server.appengine.MemcacheTable;
import com.google.walkaround.util.server.appengine.MemcacheTable.IdentifiableValue;

import org.waveprotocol.wave.model.wave.ParticipantId;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Stores user account information.
 *
 * @author ohler@google.com (Christian Ohler)
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class AccountStore {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AccountStore.class.getName());

  /**
   * Information about a user stored in {@link AccountStore}.
   */
  public static final class Record implements Serializable {
    private static final long serialVersionUID = 759933263092669762L;

    private final StableUserId userId;
    private final ParticipantId participantId;
    @Nullable private final OAuthCredentials oAuthCredentials;

    public Record(StableUserId userId,
        ParticipantId participantId,
        @Nullable OAuthCredentials oAuthCredentials) {
      this.userId = checkNotNull(userId, "Null userId");
      this.participantId = checkNotNull(participantId, "Null participantId");
      this.oAuthCredentials = oAuthCredentials;
    }

    public StableUserId getUserId() {
      return userId;
    }

    public ParticipantId getParticipantId() {
      return participantId;
    }

    @Nullable public OAuthCredentials getOAuthCredentials() {
      return oAuthCredentials;
    }

    @Override public String toString() {
      return "Record(" + userId + ", " + participantId + ", " + oAuthCredentials + ")";
    }

    @Override public final boolean equals(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof Record)) { return false; }
      Record other = (Record) o;
      return Objects.equal(userId, other.userId)
          && Objects.equal(participantId, other.participantId)
          && Objects.equal(oAuthCredentials, other.oAuthCredentials);
    }

    @Override public final int hashCode() {
      return Objects.hashCode(userId, participantId, oAuthCredentials);
    }
  }

  // Incremented to 2 because user ids have changed.
  private static final String ENTRY_KIND = "AccountRecord2";

  // TODO(ohler): The user id is already in the key, remove the property.
  private static final String STABLE_USER_ID_PROPERTY = "UserId";
  private static final String USER_EMAIL_PROPERTY = "UserEmail";
  private static final String REFRESH_TOKEN_PROPERTY = "RefreshToken";
  private static final String ACCESS_TOKEN_PROPERTY = "AccessToken";

  private static final String MEMCACHE_TAG = "AccountStore";

  private final CheckedDatastore datastore;
  private final MemcacheTable<StableUserId, Record> memcache;

  @Inject
  public AccountStore(CheckedDatastore datastore, MemcacheTable.Factory memcacheFactory) {
    this.datastore = datastore;
    this.memcache = memcacheFactory.create(MEMCACHE_TAG);
  }

  private static Key makeKey(StableUserId userId) {
    return KeyFactory.createKey(ENTRY_KIND, "u" + userId.getId());
  }

  public void put(final Record record) throws PermanentFailure {
    Preconditions.checkNotNull(record, "Null record");
    log.info("Putting record " + record);

    final StableUserId userId = record.getUserId();
    ParticipantId participantId = record.getParticipantId();
    OAuthCredentials credentials = record.getOAuthCredentials();
    String refreshToken = credentials == null ? null : credentials.getRefreshToken();
    String accessToken = credentials == null ? null : credentials.getAccessToken();

    final Entity entity = new Entity(makeKey(userId));
    DatastoreUtil.setNonNullIndexedProperty(entity, STABLE_USER_ID_PROPERTY, userId.getId());
    DatastoreUtil.setNonNullIndexedProperty(
        entity, USER_EMAIL_PROPERTY, participantId.getAddress());
    DatastoreUtil.setOrRemoveUnindexedProperty(entity, REFRESH_TOKEN_PROPERTY, refreshToken);
    DatastoreUtil.setOrRemoveUnindexedProperty(entity, ACCESS_TOKEN_PROPERTY, accessToken);

    new RetryHelper().run(new RetryHelper.VoidBody() {
        @Override public void run() throws RetryableFailure, PermanentFailure {
          CheckedTransaction tx = datastore.beginTransaction();
          log.info("About to put " + entity);
          tx.put(entity);
          memcache.enqueueDeletion(tx, userId);
          tx.commit();
          log.info("Committed " + tx);
        }
      });
    memcache.delete(userId);
  }

  public void delete(final StableUserId userId) throws PermanentFailure {
    Preconditions.checkNotNull(userId, "Null userId");
    log.info("Deleting record for user " + userId);
    final Key key = makeKey(userId);
    new RetryHelper().run(new RetryHelper.VoidBody() {
        @Override public void run() throws RetryableFailure, PermanentFailure {
          CheckedTransaction tx = datastore.beginTransaction();
          log.info("About to delete " + key);
          tx.delete(key);
          memcache.enqueueDeletion(tx, userId);
          tx.commit();
          log.info("Committed " + tx);
        }
      });
    memcache.delete(userId);
  }

  @Nullable private Record convertEntity(@Nullable Entity e) {
    if (e == null) {
      return null;
    } else if (e.hasProperty(REFRESH_TOKEN_PROPERTY)) {
      return new Record(
          new StableUserId(
              DatastoreUtil.getExistingProperty(e, STABLE_USER_ID_PROPERTY, String.class)),
          ParticipantId.ofUnsafe(
              DatastoreUtil.getExistingProperty(e, USER_EMAIL_PROPERTY, String.class)),
          new OAuthCredentials(
              DatastoreUtil.getExistingProperty(e, REFRESH_TOKEN_PROPERTY, String.class),
              DatastoreUtil.getExistingProperty(e, ACCESS_TOKEN_PROPERTY, String.class)));
    } else {
      return new Record(
          new StableUserId(
              DatastoreUtil.getExistingProperty(e, STABLE_USER_ID_PROPERTY, String.class)),
          ParticipantId.ofUnsafe(
              DatastoreUtil.getExistingProperty(e, USER_EMAIL_PROPERTY, String.class)),
          null);
    }
  }

  @Nullable public Record get(final StableUserId userId) throws PermanentFailure {
    Preconditions.checkNotNull(userId, "Null userId");
    IdentifiableValue<Record> cached = memcache.getIdentifiable(userId);
    if (cached != null) {
      log.info("Account record found in cache: " + cached);
      return cached.getValue();
    }
    log.info("Fetching user credentials for user " + userId);
    Entity e = new RetryHelper().run(
        new RetryHelper.Body<Entity>() {
          @Override public Entity run() throws RetryableFailure, PermanentFailure {
            CheckedTransaction tx = datastore.beginTransaction();
            try {
              Entity entity = tx.get(makeKey(userId));
              log.info("Got " + (entity == null ? null : entity.getKey()));
              return entity;
            } finally {
              tx.rollback();
            }
          }
        });
    Record read = convertEntity(e);
    memcache.putIfUntouched(userId, cached, read);
    return read;
  }

  /**
   * Returns info for the given e-mail, or null if not found.  Note that this
   * datastore read is only eventually consistent, so this method may return
   * null for a short while after a record for this user has been stored.
   */
  @Nullable public Record findByEmail(final String userEmail) throws PermanentFailure {
    Preconditions.checkNotNull(userEmail, "Null email");
    log.info("Fetching user credentials for email " + userEmail);
    Entity e = new RetryHelper().run(
        new RetryHelper.Body<Entity>() {
          @Override public Entity run() throws RetryableFailure, PermanentFailure {
            CheckedPreparedQuery q = datastore.prepareNontransactionalQuery(
                new Query(ENTRY_KIND).addFilter(
                    USER_EMAIL_PROPERTY, FilterOperator.EQUAL, userEmail));
            return q.asSingleEntity();
          }
        });
    return convertEntity(e);
  }

}
