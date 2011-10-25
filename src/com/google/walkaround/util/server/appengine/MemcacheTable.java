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
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

  public static <K extends Serializable, V extends Serializable> MemcacheTable<K, V>
      of(MemcacheService service, String tag) {
    return new MemcacheTable<K, V>(service, tag);
  }

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(MemcacheTable.class.getName());

  private static class TaggedKey<K extends Serializable> implements Serializable {
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

  private final MemcacheService service;
  private final String tag;

  /**
   * @param tag a unique tag that distinguishes this table from other
   * tables.  Since memcache persists through application reloads
   * (TODO(ohler): confirm this), you have to explicitly clear the cache first
   * if you ever want to re-use a tag for different data.
   */
  public MemcacheTable(MemcacheService service, String tag) {
    this.service = checkNotNull(service, "Null service");
    this.tag = checkNotNull(tag, "Null tag");
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

  // TODO(ohler): Add more methods here to match MemcacheService.
}
