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

package com.google.walkaround.util.server.appengine;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.Body;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * A directory in the datastore that stores a set of T, where each T contains an
 * id of type I.  Each entity is its own entity group.
 *
 * @author ohler@google.com (Christian Ohler)
 *
 * @param <T> entry type
 * @param <I> id type
 */
public abstract class AbstractDirectory<T, I> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AbstractDirectory.class.getName());

  private final CheckedDatastore datastore;
  private final String entityKind;

  public AbstractDirectory(CheckedDatastore datastore, String entityKind) {
    Preconditions.checkNotNull(datastore, "Null datastore");
    Preconditions.checkNotNull(entityKind, "Null entityKind");
    this.datastore = datastore;
    this.entityKind = entityKind;
  }

  protected abstract String serializeId(I id);
  protected abstract I getId(T e);
  protected abstract void populateEntity(T in, Entity out);
  protected abstract T parse(Entity e);

  @VisibleForTesting
  public Key makeKey(I id) {
    return KeyFactory.createKey(entityKind, serializeId(id));
  }

  @Nullable public T get(I id) throws IOException {
    Key key = makeKey(id);
    Entity result;
    try {
      log.info("Looking up " + key);
      CheckedTransaction tx = datastore.beginTransaction();
      try {
        result = tx.get(key);
      } finally {
        tx.rollback();
      }
    } catch (PermanentFailure e) {
      log.log(Level.SEVERE, "Failed to look up " + id, e);
      throw new IOException(e);
    } catch (RetryableFailure e) {
      log.log(Level.SEVERE, "Failed to look up " + id, e);
      throw new IOException(e);
    }

    if (result != null) {
      log.info("Looked up " + key + ": " + result);
      return parse(result);
    } else {
      log.info("Looked up " + key + ": not found");
      return null;
    }
  }

  /**
   * Transactionally checks if an entry with the same key as newEntry exists,
   * and adds newEntry if not.  If an entry already exists, returns the existing
   * entry; otherwise (i.e., if newEntry was added), returns null.
   */
  // NOTE(danilatos): Don't be tempted to implement a version of this method that
  // accepts a Provider or something in order to only create the newEntry if one
  // does not exist in the datastore, because megastore transactions run in parallel
  // and just fail on commit (there is no locking).
  // Instead, use get() and then getOrAdd() if get returns null.
  @Nullable public T getOrAdd(final T newEntry) throws IOException {
    Preconditions.checkNotNull(newEntry, "Null newEntry");

    final Key key = makeKey(getId(newEntry));
    RetryHelper r = new RetryHelper();

    try {
      return r.run(new RetryHelper.Body<T>() {
        @Override public T run() throws RetryableFailure, PermanentFailure {
          CheckedTransaction tx = datastore.beginTransaction();
          try {
            Entity existing = tx.get(key);
            if (existing != null) {
              log.info("Read " + existing + " in " + tx);
              return parse(existing);
            } else {
              Entity newEntity = new Entity(key);
              populateEntity(newEntry, newEntity);
              // For now, we verify that it parses with no exceptions.  We can turn
              // this off if it's too expensive.
              parse(newEntity);
              log.info("About to put " + newEntity + " in " + tx);
              tx.put(newEntity);
              tx.commit();
              log.info("Committed " + tx);
              return null;
            }
          } finally {
            tx.close();
          }
        }
      });
    } catch (PermanentFailure e) {
      throw new IOException(e);
    }
  }

  public List<T> hackReadAllEntries() {
    ImmutableList.Builder<T> b = ImmutableList.builder();
    for (Entity e : datastore.unsafe().prepare(new Query(entityKind)).asIterable()) {
      b.add(parse(e));
    }
    return b.build();
  }

}
