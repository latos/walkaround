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

package com.google.walkaround.slob.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.Util;
import com.google.walkaround.util.server.appengine.MemcacheTable;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Router that connects client channels as listeners to objects in an m:n
 * fashion, and provides the token required for channel set up.
 *
 * <p>
 * Messages are not guaranteed to be delivered, nor are they guaranteed to be in
 * order, nor is there any guaranteed about lack of duplicate messages. The
 * message contents should provide enough information to allow clients to deal
 * with these situations.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class SlobMessageRouter {

  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  public @interface SlobChannelExpirationSeconds {}

  public static class TooManyListenersException extends Exception {
    private static final long serialVersionUID = 455819249880278222L;

    public TooManyListenersException(String message, Throwable cause) {
      super(message, cause);
    }

    public TooManyListenersException(String message) {
      super(message);
    }

    public TooManyListenersException(Throwable cause) {
      super(cause);
    }
  }

  private static class ListenerAlreadyPresent extends Exception {
    private static final long serialVersionUID = 144800949601544909L;

    private final ListenerKey key;
    private final ClientId clientId;
    private ListenerAlreadyPresent(ListenerKey key, ClientId clientId) {
      super(key + " " + clientId);
      this.key = key;
      this.clientId = clientId;
    }
  }

  private static class ListenerKey implements Serializable {
    private static final long serialVersionUID = 601407287266649008L;

    private final SlobId id;
    private final int listenerNum;

    public ListenerKey(SlobId id, int listenerNum) {
      this.id = checkNotNull(id, "Null id");
      this.listenerNum = listenerNum;
    }

    public SlobId getId() {
      return id;
    }

    public int getListenerNum() {
      return listenerNum;
    }

    @Override public String toString() {
      return "ListenerKey(" + id + ", " + listenerNum + ")";
    }

    @Override public final boolean equals(Object o) {
      if (o == this) { return true; }
      if (!(o instanceof ListenerKey)) { return false; }
      ListenerKey other = (ListenerKey) o;
      return listenerNum == other.listenerNum
          && Objects.equal(id, other.id);
    }

    @Override public final int hashCode() {
      return Objects.hashCode(id, listenerNum);
    }
  }

  private static final Logger log = Logger.getLogger(SlobMessageRouter.class.getName());

  private static final int MAX_LISTENERS = 51;

  private static final String LISTENER_MEMCACHE_TAG = "ORL";
  private static final String CLIENTS_MEMCACHE_TAG = "ORC";
  private final MemcacheTable<ListenerKey, ClientId> objectListeners;
  private final MemcacheTable<ClientId, String> clientTokens;
  private final ChannelService channelService;
  private final int expirationSeconds;

  @Inject
  public SlobMessageRouter(MemcacheTable.Factory memcacheFactory, ChannelService channelService,
      @SlobChannelExpirationSeconds int expirationSeconds) {
    this.objectListeners = memcacheFactory.create(LISTENER_MEMCACHE_TAG);
    this.clientTokens = memcacheFactory.create(CLIENTS_MEMCACHE_TAG);
    this.channelService = channelService;
    this.expirationSeconds = expirationSeconds;
  }

  /**
   * Publishes messages to clients listening on an object.
   */
  public void publishMessages(SlobId object, String jsonString) {
    log.info("Publishing " + object + " " + jsonString);

    Map<?, ClientId> takenMappings = getMappings(object);
    for (ClientId listener : takenMappings.values()) {
      sendData(listener, jsonString);
    }
  }

  /**
   * Connects a client as a listener to an object. A client may listen to more
   * than one object.
   *
   * <p>
   * Returns the token the client should use to set up its browser channel. A
   * client will only use one token, even if it is listening to multiple
   * objects. The router keeps track of this, and will return the client's
   * existing token if it already has one.
   */
  public String connectListener(SlobId objectId, ClientId clientId)
      throws TooManyListenersException {
    log.info("Connecting " + clientId + " to " + objectId);

    String channelToken;
    int maxAttempts = 10;
    for (int attempt = 0; attempt < maxAttempts; attempt++) {
      boolean success;
      try {
        int freeId = getFreeKeyForListener(objectId, clientId);
        assert freeId >= 0 && freeId < MAX_LISTENERS;

        success = objectListeners.put(new ListenerKey(objectId, freeId), clientId,
            Expiration.byDeltaSeconds(expirationSeconds),
            SetPolicy.ADD_ONLY_IF_NOT_PRESENT);

        if (success) {
          log.info("Created new listener: " + clientId);
        }
      } catch (ListenerAlreadyPresent e) {
        // This is just an expiry refresh, ideally we would rather not clobber
        // a different listener in the unlikely event it got changed in this
        // brief instant, but that doesn't matter.
        objectListeners.put(e.key, e.clientId,
            Expiration.byDeltaSeconds(expirationSeconds),
            SetPolicy.SET_ALWAYS);
        success = true;
        log.info("Refreshed listener: " + e.clientId);
      }

      if (success) {
        return tokenFor(clientId);
      }

      log.info("Failed to create listener, might retry...");
    }

    log.warning("Max attempts to set a listener exceeded");
    throw new TooManyListenersException("Max attempts to set a listener exceeded");
  }

  private String tokenFor(ClientId clientId) {
    String existing = clientTokens.get(clientId);
    if (existing != null) {
      log.info("Got existing token for client " + clientId + ": " + existing);
      return existing;
    }

    // This might screw up a concurrent attempt to do the same thing but
    // doesn't really matter.
    String token = channelService.createChannel(clientId.getId());
    clientTokens.put(clientId, token);

    log.info("Got new token for client " + clientId + ": " + token);
    return token;
  }

  private void sendData(ClientId clientId, String data) {
    log.info("Sending to " + clientId + ", " + Util.abbrev(data, 50));
    try {
      channelService.sendMessage(new ChannelMessage(clientId.getId(), data));
    } catch (ChannelFailureException e) {
      // Channel service is best-effort anyway, so it's safe to discard the
      // exception after taking note of it.
      log.log(Level.SEVERE, "Channel service failed for " + clientId, e);
    }
  }

  private int getFreeKeyForListener(SlobId object, ClientId clientId)
      throws TooManyListenersException, ListenerAlreadyPresent {
    Map<ListenerKey, ClientId> takenMappings = getMappings(object);

    for (Map.Entry<ListenerKey, ClientId> entry : takenMappings.entrySet()) {
      if (clientId.equals(entry.getValue())) {
        throw new ListenerAlreadyPresent(entry.getKey(), entry.getValue());
      }
    }

    // MAX_LISTENERS is small, and we need to iterate up to it anyway
    // in getMappings()
    for (int i = 0; i < MAX_LISTENERS; i++) {
      if (!takenMappings.containsKey(new ListenerKey(object, i))) {
        return i;
      }
    }

    throw new TooManyListenersException(object + " has too many listeners");
  }

  private Map<ListenerKey, ClientId> getMappings(SlobId object) {
    Set<ListenerKey> keys = Sets.newHashSet();
    for (int i = 0; i < MAX_LISTENERS; i++) {
      keys.add(new ListenerKey(object, i));
    }
    return objectListeners.getAll(keys);
  }
}
