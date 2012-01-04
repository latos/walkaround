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

import static com.google.walkaround.slob.server.StoreAccessChecker.WALKAROUND_TRUSTED_HEADER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.appengine.api.backends.BackendService;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.base.Charsets;
import com.google.common.net.UriEscapers;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.walkaround.proto.ServerMutateRequest;
import com.google.walkaround.proto.ServerMutateResponse;
import com.google.walkaround.proto.gson.ServerMutateResponseGsonImpl;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.server.MonitoringVars;
import com.google.walkaround.util.server.Util;
import com.google.walkaround.util.server.appengine.MemcacheTable;
import com.google.walkaround.util.server.auth.DigestUtils2.Secret;
import com.google.walkaround.util.server.servlet.TryAgainLaterException;
import com.google.walkaround.util.shared.RandomBase64Generator;

import org.waveprotocol.wave.communication.gson.GsonSerializable;
import org.waveprotocol.wave.model.util.Pair;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Used by frontends to process mutations.
 *
 * Processes mutations by trying to forward them to a backend to do the actual
 * work. Implements a best-effort affinity policy so that in general writes to
 * the same object hit one backend. Falls back to writing on the frondend under
 * certain circumstances.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class AffinityMutationProcessor {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AffinityMutationProcessor.class.getName());

  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  public @interface StoreBackendName {}
  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  public @interface StoreBackendInstanceCount {}

  private class PostRequest {
    private final StringBuilder urlBuilder = new StringBuilder();
    private final StringBuilder contentBuilder = new StringBuilder();

    void urlParam(String key, String value) {
      urlBuilder.append((urlBuilder.length() == 0 ? "?" : "&") + key + "=" + urlEncode(value));
    }

    void postParam(String key, String value) {
      contentBuilder.append(key + "=" + urlEncode(value) + "&");
    }

    /**
     * @return the response body as a String.
     * @throws IOException for 500 or above or general connection problems.
     * @throws InvalidStoreRequestException for any response code not 200.
     */
    String send(String base) throws IOException {
      URL url = new URL(base + urlBuilder.toString());
      HTTPRequest req = new HTTPRequest(url, HTTPMethod.POST, getFetchOptions());

      // TODO(ohler): use multipart/form-data for efficiency
      req.setHeader(new HTTPHeader("Content-Type", "application/x-www-form-urlencoded"));
      req.setHeader(new HTTPHeader(WALKAROUND_TRUSTED_HEADER, secret.getHexData()));
      // NOTE(danilatos): Appengine will send 503 if the backend is at the
      // max number of concurrent requests. We might come up with a use for
      // handling this error code specifically. For now, we didn't go through
      // the code to make sure that all other overload situations also manifest
      // themselves as 503 rather than random exceptions that turn into 500s.
      // Therefore, the code in this class treats all 5xx responses as an
      // indication of possible overload.
      req.setHeader(new HTTPHeader("X-AppEngine-FailFast", "true"));
      req.setPayload(contentBuilder.toString().getBytes(Charsets.UTF_8));

      log.info("Sending to " + url);
      String ret = fetch(req);
      log.info("Request completed");
      return ret;
    }

    private FetchOptions getFetchOptions() {
      FetchOptions options = FetchOptions.Builder
        .disallowTruncate()
        .doNotFollowRedirects();
      return options;
    }

    private String describeResponse(HTTPResponse resp) {
      StringBuilder b = new StringBuilder(resp.getResponseCode()
          + " with " + resp.getContent().length + " bytes of content");
      for (HTTPHeader h : resp.getHeaders()) {
        b.append("\n" + h.getName() + ": " + h.getValue());
      }
      b.append("\n" + new String(resp.getContent(), Charsets.UTF_8));
      return "" + b;
    }

    private String fetch(HTTPRequest req) throws IOException {
      HTTPResponse response = fetchService.fetch(req);
      int responseCode = response.getResponseCode();

      if (responseCode >= 300 && responseCode < 400) {
        throw new RuntimeException("Unexpected redirect for url " + req.getURL()
            + ": " + describeResponse(response));
      }

      byte[] rawResponseBody = response.getContent();
      String responseBody;
      if (rawResponseBody == null) {
        responseBody = "";
      } else {
        responseBody = new String(rawResponseBody, Charsets.UTF_8);
      }

      if (responseCode != 200) {
        String msg = req.getURL() + " gave response code " + responseCode
            + ", body: " + responseBody;
        if (responseCode >= 500) {
          throw new IOException(msg);
        } else {
          throw new InvalidStoreRequestException(msg);
        }
      }

      return responseBody;
    }

    private String urlEncode(String s) {
      return UriEscapers.uriQueryStringEscaper(false).escape(s);
    }
  }

  private static final String MEMCACHE_TAG = "OSM";

  // TODO(danilatos): Make these flags.
  private static final int AFFINITY_MIN_EXPIRATION_SECONDS = 30;
  private static final int AFFINITY_MAX_EXPIRATION_SECONDS = 45;

  private static final int AFFINITY_EXPIRATION_SPREAD_SECONDS =
      AFFINITY_MAX_EXPIRATION_SECONDS - AFFINITY_MIN_EXPIRATION_SECONDS;

  private final Random random;
  private final RandomBase64Generator random64;
  private final URLFetchService fetchService;
  private final BackendService backends;
  private final LocalMutationProcessor localProcessor;
  private final MemcacheTable<SlobId, Integer> objectServerMappings;
  private final Secret secret;
  private final int numStoreServers;
  private final String storeServerName;
  private final MonitoringVars monitoring;

  @Inject
  public AffinityMutationProcessor(
      Random random,
      RandomBase64Generator random64,
      URLFetchService fetchService,
      BackendService backends,
      LocalMutationProcessor localProcessor,
      MemcacheTable.Factory memcacheFactory,
      Secret secret,
      @StoreBackendInstanceCount int numStoreServers,
      @StoreBackendName String storeServer,
      MonitoringVars monitoring) {
    this.random = random;
    this.random64 = random64;
    this.fetchService = fetchService;
    this.backends = backends;
    this.localProcessor = localProcessor;
    this.objectServerMappings = memcacheFactory.create(MEMCACHE_TAG);
    this.secret = secret;
    this.numStoreServers = numStoreServers;
    this.storeServerName = storeServer;
    this.monitoring = monitoring;
  }

  public ServerMutateResponse mutateObject(ServerMutateRequest req) throws IOException {
    // TODO(danilatos): Document strategy.

    ServerMutateResponse result;
    SlobId objectId = new SlobId(req.getSession().getObjectId());

    if (numStoreServers == 0) {
      monitoring.incrementCounter("affinity-backends-disabled");
      result = localProcessor.mutateObject(req);
    } else {
      Pair<Boolean, Integer> info = serverFor(objectId);
      if (info == null) {
        log.warning("Could not establish a mapping, falling back to processing on frontend");
        monitoring.incrementCounter("affinity-could-not-establish-mapping");
        // It's unlikely there is a backend owning this object for us to interfere with,
        // so attempting to process on the frontend is better than nothing.
        result = localProcessor.mutateObject(req);
      } else {
        try {
          // Attempt normal situation - process on the backend to which
          // the object has affinity.
          int serverId = info.getSecond();
          result = processOnBackend(serverId, req);
          monitoring.incrementCounter("affinity-processed-on-backend");
        } catch (IOException e) { // "500" type errors.
          boolean wasMapped = info.getFirst();
          if (wasMapped) {
            // Maybe the particular object is under high load.
            // In such a case we don't know we won't be making matters worse
            // by choosing another server or doing it locally, because we
            // may increase contention on the object's entity group and slow
            // things down further. (While one object won't make much difference,
            // this policy applies on aggregate). By getting the client to
            // back off, we degrade smoothly - and if it's just that server
            // that's under load, the situation will rectify itself after
            // the memcache association expires.
            log.log(Level.WARNING, "Backend threw exception, getting client to back off", e);
            monitoring.incrementCounter("affinity-backend-overloaded-backing-off");
            throw new TryAgainLaterException("Client back off due to load", e);
          } else {
            // Maybe we're under-provisioned in terms of store servers.
            // In this case, where the object was not mapped, it's far less
            // likely that doing the work locally would contend with a backend
            // trying to process mutations for that object. So we do the
            // work locally to ensure progress. We also remove the mapping
            // so that if it's just that server that was under load, the next
            // attempt to write might choose a different backend and have
            // more success. If all backends are over-loaded, then we degrade
            // gracefully by processing the surplus writes on frontends for
            // objects that fail to "claim" a mapping.
            log.log(Level.WARNING, "Backend threw exception, attempting mutation on frontend", e);
            monitoring.incrementCounter("affinity-backend-overloaded-processing-locally");
            // Remove mapping and process locally.
            objectServerMappings.delete(objectId);
            result = localProcessor.mutateObject(req);
          }
        }
      }
    }

    return result;
  }

  /**
   * Returns and maybe creates a mapping to a server for the given object id.
   *
   * @return the mapped server, and true if that mapping already existed, false
   *         if it was created by this method.
   *
   *         WARNING: if a mapping could not be established, returns null.
   */
  @Nullable
  private Pair<Boolean, Integer> serverFor(SlobId objectId) {
    boolean wasMapped;
    int serverId;

    Integer maybeServerId = objectServerMappings.get(objectId);
    if (maybeServerId != null) {
      wasMapped = true;
      serverId = maybeServerId;
      monitoring.incrementCounter("affinity-mapping-found");
    } else {
      int newServerId = random.nextInt(numStoreServers);
      int expiration = AFFINITY_MIN_EXPIRATION_SECONDS
          + random.nextInt(AFFINITY_EXPIRATION_SPREAD_SECONDS);
      log.info("No mapping for " + objectId + ", generated " + newServerId +
        ", expiration " + expiration);
      boolean putSucceeded = objectServerMappings.put(objectId, newServerId,
          Expiration.byDeltaSeconds(expiration),
          SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
      if (putSucceeded) {
        serverId = newServerId;
        wasMapped = false;
        monitoring.incrementCounter("affinity-mapping-generated");
      } else {
        log.warning("Mapping was generated concurrently");
        maybeServerId = objectServerMappings.get(objectId);
        if (maybeServerId != null) {
          serverId = maybeServerId;
          wasMapped = true;
          monitoring.incrementCounter("affinity-mapping-generated-but-overwritten");
        } else {
          log.warning("Concurrently generated mapping promptly disappeared!");
          monitoring.incrementCounter("affinity-mapping-generated-but-lost");
          return null;
        }
      }
    }

   return Pair.of(wasMapped, serverId);
  }

  private ServerMutateResponse processOnBackend(int serverId, ServerMutateRequest req)
      throws IOException {
    String base = "http://" + backends.getBackendAddress(storeServerName, serverId);
    // For debugging, to match up requests in the logs.
    String requestId = random64.next(10) + "____" + req.getSession().getObjectId();
    log.info("Using backend " + base + " for requestId " + requestId);

    PostRequest post = new PostRequest();
    post.urlParam("requestId", requestId);
    post.postParam("req", GsonProto.toJson((GsonSerializable) req));

    String response = post.send(Util.pathCat(base, "store/mutate"));
    if (!response.startsWith("OK")) {
      throw new RuntimeException("Backend gave junk " + response);
    }

    try {
      return GsonProto.fromGson(new ServerMutateResponseGsonImpl(),
          response.substring(2));
    } catch (MessageException e) {
      throw new RuntimeException("Backend gave incompatible JSON: " + response, e);
    }
  }

}
