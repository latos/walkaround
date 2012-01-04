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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * A wrapper around App Engine's MemcacheService that provides stronger typing
 * and prepends a user-defined prefix to all keys to make it easier to avoid
 * collisions.
 *
 * @author ohler@google.com (Christian Ohler)
 *
 * @param <K> key type
 * @param <V> value type
 */
public class MemcacheTable<K extends Serializable, V extends Serializable> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(MemcacheTable.class.getName());

  public interface Factory {
    <K extends Serializable, V extends Serializable> MemcacheTable<K, V> create(String tag);
  }

  // Guice says "Factory cannot be used as a key; It is not fully specified."
  // Looks like we can't use FactoryModuleBuilder and have to write our own
  // implementation.
  public static class FactoryImpl implements Factory {
    private final MemcacheService service;
    private final Queue deletionQueue;

    @Inject
    public FactoryImpl(MemcacheService service, @MemcacheDeletionQueue Queue deletionQueue) {
      this.service = service;
      this.deletionQueue = deletionQueue;
    }

    @Override public <K extends Serializable, V extends Serializable> MemcacheTable<K, V> create(
        String tag) {
      return new MemcacheTable<K, V>(service, deletionQueue, tag);
    }
  }

  private static class TaggedKey<K extends Serializable> implements Serializable {
    private static final long serialVersionUID = 267550222157163337L;

    private final String tag;
    @Nullable private final K key;

    public TaggedKey(String tag, @Nullable K key) {
      this.tag = checkNotNull(tag, "Null tag");
      this.key = key;
    }

    public String getTag() {
      return tag;
    }

    @Nullable public K getKey() {
      return key;
    }

    @Override public String toString() {
      return "TaggedKey(" + tag + ", " + key + ")";
    }

    @Override public final boolean equals(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof TaggedKey)) { return false; }
      TaggedKey other = (TaggedKey) o;
      return Objects.equal(tag, other.tag)
          && Objects.equal(key, other.key);
    }

    @Override public final int hashCode() {
      return Objects.hashCode(tag, key);
    }
  }

  public static class IdentifiableValue<V> {
    private final MemcacheService.IdentifiableValue inner;

    IdentifiableValue(MemcacheService.IdentifiableValue inner) {
      this.inner = checkNotNull(inner, "Null inner");
    }

    @Override public String toString() {
      return getClass().getSimpleName() + "(" + inner.getValue() + ")";
    }

    @SuppressWarnings("unchecked")
    public V getValue() {
      return (V) inner.getValue();
    }
  }

  private final MemcacheService service;
  private final String tag;
  private final Queue deletionQueue;

  /**
   * @param tag a unique tag that distinguishes this table from other tables.
   * Since memcache persists through application reloads, you have to explicitly
   * clear the cache first if you ever want to re-use a tag for different data.
   */
  @Inject
  public MemcacheTable(MemcacheService service, @MemcacheDeletionQueue Queue deletionQueue,
      @Assisted String tag) {
    this.service = service;
    this.deletionQueue = deletionQueue;
    this.tag = tag;
  }

  private static class DeletionTask implements DeferredTask {
    private static final long serialVersionUID = 483212086178559149L;

    private final TaggedKey keyToDelete;

    DeletionTask(TaggedKey keyToDelete) {
      this.keyToDelete = checkNotNull(keyToDelete, "Null keyToDelete");
    }

    @Override public void run() {
      log.info("Deferred deletion for memcache key " + keyToDelete);
      MemcacheServiceFactory.getMemcacheService().delete(keyToDelete);
    }
  }

  public void enqueueDeletion(CheckedTransaction tx, @Nullable K key)
      throws RetryableFailure, PermanentFailure {
    tx.enqueueTask(deletionQueue, TaskOptions.Builder.withPayload(new DeletionTask(tagKey(key))));
  }

  // Cast is safe under the assumption that tag is not re-used for a different
  // type
  @SuppressWarnings("unchecked")
  @Nullable private V castRawValue(Object rawValue) {
    return (V) rawValue;
  }

  private TaggedKey<K> tagKey(@Nullable K key) {
    return new TaggedKey<K>(tag, key);
  }

  public void delete(@Nullable K key) {
    TaggedKey<K> taggedKey = tagKey(key);
    log.info("cache delete " + taggedKey);
    service.delete(taggedKey);
  }

  public void put(@Nullable K key, @Nullable V value) {
    put(key, value, null);
  }

  public void put(@Nullable K key, @Nullable V value, @Nullable Expiration expires) {
    put(key, value, expires, SetPolicy.SET_ALWAYS);
  }

  /**
   * @return true if a new entry was created, false if not because of the
   *         policy.
   */
  public boolean put(@Nullable K key, @Nullable V value, @Nullable Expiration expires,
      SetPolicy policy) {
    TaggedKey<K> taggedKey = tagKey(key);
    String expiresString = expires == null ? null : "" + expires.getMillisecondsValue();
    log.info("cache put " + taggedKey + " = " + value + ", " + expiresString + ", " + policy);
    return service.put(taggedKey, value, expires, policy);
  }

  /**
   * @return the set of keys for which new entries were created (some may not
   *         have been created because of the policy).
   */
  public Set<K> putAll(Map<K, V> mappings, @Nullable Expiration expires, SetPolicy policy) {
    Map<TaggedKey<K>, V> rawMappings = Maps.newHashMapWithExpectedSize(mappings.size());
    for (Map.Entry<K, V> entry : mappings.entrySet()) {
      rawMappings.put(tagKey(entry.getKey()), entry.getValue());
    }
    Set<TaggedKey<K>> rawResult = service.putAll(rawMappings, expires, policy);
    Set<K> result = Sets.newHashSetWithExpectedSize(rawResult.size());
    for (TaggedKey<K> key : rawResult) {
      result.add(key.getKey());
    }
    return result;
  }

  @Nullable public V get(@Nullable K key) {
    TaggedKey<K> taggedKey = tagKey(key);
    Object rawValue;
    try {
      rawValue = service.get(taggedKey);
    } catch (InvalidValueException e) {
      // Probably a deserialization error (incompatible serialVersionUID or similar).
      log.log(Level.WARNING, "Error getting object from memcache, key: " + key, e);
      return null;
    }
    if (rawValue == null) {
      log.info("cache miss " + taggedKey);
      return null;
    } else {
      log.info("cache hit " + taggedKey + " = " + rawValue);
      // TODO(ohler): check actual type
      return castRawValue(rawValue);
    }
  }

  public Map<K, V> getAll(Set<K> keys) {
    Set<TaggedKey<K>> taggedKeys = Sets.newHashSetWithExpectedSize(keys.size());
    for (K key : keys) {
      taggedKeys.add(tagKey(key));
    }
    Map<TaggedKey<K>, Object> rawMappings;
    try {
      rawMappings = service.getAll(taggedKeys);
    } catch (InvalidValueException e) {
      // Probably a deserialization error (incompatible serialVersionUID or similar).
      log.log(Level.WARNING, "Error getting objects from memcache, keys: " + keys, e);
      return Collections.emptyMap();
    }

    Map<K, V> mappings = Maps.newHashMapWithExpectedSize(rawMappings.size());
    for (Map.Entry<TaggedKey<K>, Object> entry : rawMappings.entrySet()) {
      mappings.put(entry.getKey().getKey(), castRawValue(entry.getValue()));
    }

    log.info("Found " + mappings.size() + " of " + keys.size() + " objects in memcache: "
        + mappings);

    return mappings;
  }

  @Nullable public IdentifiableValue<V> getIdentifiable(@Nullable K key) {
    TaggedKey<K> taggedKey = tagKey(key);
    MemcacheService.IdentifiableValue rawValue;
    try {
      rawValue = service.getIdentifiable(taggedKey);
    } catch (InvalidValueException e) {
      // Probably a deserialization error (incompatible serialVersionUID or similar).
      log.log(Level.WARNING, "Error getting object from memcache, key: " + key, e);
      return null;
    }
    if (rawValue == null) {
      log.info("cache miss " + taggedKey);
      return null;
    } else {
      log.info("cache hit " + taggedKey + " = " + rawValue);
      return new IdentifiableValue<V>(rawValue);
    }
  }

  /**
   * NOTE: This differs from {@link MemcacheService} in that it allows oldValue
   * to be null, in which case it will put unconditionally.
   */
  public boolean putIfUntouched(@Nullable K key, IdentifiableValue<V> oldValue,
      @Nullable V newValue) {
    return putIfUntouched(key, oldValue, newValue, null);
  }

  /**
   * NOTE: This differs from {@link MemcacheService} in that it allows oldValue
   * to be null, in which case it will put unconditionally.
   */
  public boolean putIfUntouched(@Nullable K key, IdentifiableValue<V> oldValue,
      @Nullable V newValue, @Nullable Expiration expires) {
    TaggedKey<K> taggedKey = tagKey(key);
    String expiresString = expires == null ? null : "" + expires.getMillisecondsValue();
    boolean result;
    if (oldValue == null) {
      service.put(taggedKey, newValue, expires);
      result = true;
    } else {
      result = service.putIfUntouched(taggedKey, oldValue.inner, newValue, expires);
    }
    log.info("cache putIfUntouched " + taggedKey + " = " + newValue
        + ", " + expiresString + ": " + result);
    return result;
  }

  // TODO(ohler): Add more methods here to match MemcacheService.
}
