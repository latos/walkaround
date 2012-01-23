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

package com.google.walkaround.wave.server;

import com.google.appengine.api.backends.BackendService;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.search.IndexManager;
import com.google.appengine.api.search.IndexManagerFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Joiner;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.walkaround.slob.server.MutationLog;
import com.google.walkaround.slob.server.PostCommitActionIntervalMillis;
import com.google.walkaround.slob.server.PostCommitTaskUrl;
import com.google.walkaround.slob.server.SlobLocalCacheExpirationMillis;
import com.google.walkaround.slob.server.AffinityMutationProcessor.StoreBackendInstanceCount;
import com.google.walkaround.slob.server.AffinityMutationProcessor.StoreBackendName;
import com.google.walkaround.slob.server.SlobMessageRouter.SlobChannelExpirationSeconds;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.Util;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.DatastoreUtil;
import com.google.walkaround.util.server.appengine.MemcacheDeletionQueue;
import com.google.walkaround.util.server.appengine.MemcacheTable;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.auth.DigestUtils2.Secret;
import com.google.walkaround.util.server.flags.FlagDeclaration;
import com.google.walkaround.util.server.flags.FlagFormatException;
import com.google.walkaround.util.server.flags.JsonFlags;
import com.google.walkaround.util.server.gwt.ZipSymbolMapsDirectory;
import com.google.walkaround.util.server.gwt.StackTraceDeobfuscator.SymbolMapsDirectory;
import com.google.walkaround.util.shared.RandomProviderAdapter;
import com.google.walkaround.util.shared.RandomBase64Generator.RandomProvider;
import com.google.walkaround.wave.server.auth.OAuthInterstitialHandler.Scopes;
import com.google.walkaround.wave.server.conv.PermissionCache.PermissionCacheExpirationSeconds;
import com.google.walkaround.wave.server.googleimport.ImportTaskQueue;
import com.google.walkaround.wave.server.model.LegacyDeltaEntityConverter;
import com.google.walkaround.wave.server.model.ServerMessageSerializer;
import com.google.walkaround.wave.shared.MessageSerializer;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class WalkaroundServerModule extends AbstractModule {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(WalkaroundServerModule.class.getName());

  private static final String CONTACTS_SCOPE = "https://www.google.com/m8/feeds/";
  private static final String WAVE_SCOPE = "http://wave.googleusercontent.com/api/rpc";

  private static final String SECRET_ENTITY_KIND = "Secret";
  private static final com.google.appengine.api.datastore.Key SECRET_KEY =
      KeyFactory.createKey(SECRET_ENTITY_KIND, "secret");
  private static final String SECRET_PROPERTY = "secret";

  private <T> void bindToFlag(Class<T> type, Class<? extends Annotation> annotation,
      FlagName flagName) {
    bind(Key.get(type, annotation))
        .toProvider(getProvider(Key.get(type, new FlagName.FlagImpl(flagName))));
  }

  @Override
  protected void configure() {
    // How heavyweight is SecureRandom?  Until we know, let's use only one.
    //
    // This seems preferable over the .toInstance() below, but it doesn't work.
    //bind(SecureRandom.class).in(Singleton.class);
    final SecureRandom random = new SecureRandom();
    bind(SecureRandom.class).toInstance(random);
    bind(Random.class).to(SecureRandom.class);
    bind(RandomProvider.class).to(RandomProviderAdapter.class);

    bind(MemcacheTable.Factory.class).to(MemcacheTable.FactoryImpl.class);

    bind(SystemProperty.Environment.Value.class).toInstance(SystemProperty.environment.value());

    bind(Key.get(String.class, Scopes.class)).toInstance(
        Joiner.on(" ").join(CONTACTS_SCOPE, WAVE_SCOPE));

    bind(Key.get(Queue.class, ImportTaskQueue.class)).toInstance(QueueFactory.getQueue("import"));
    bind(Key.get(Queue.class, MemcacheDeletionQueue.class)).toInstance(
        QueueFactory.getQueue("memcache-deletion"));

    bind(String.class).annotatedWith(PostCommitTaskUrl.class).toInstance(
        WalkaroundServletModule.POST_COMMIT_TASK_PATH);

    bind(MessageSerializer.class).to(ServerMessageSerializer.class);
    bind(MutationLog.DeltaEntityConverter.class).to(LegacyDeltaEntityConverter.class);

    JsonFlags.bind(binder(), Arrays.asList(FlagName.values()),
        binder().getProvider(
            Key.get(new TypeLiteral<Map<FlagDeclaration, Object>>() {}, FlagConfiguration.class)));

    bindToFlag(Integer.class, PermissionCacheExpirationSeconds.class,
        FlagName.ACCESS_CACHE_EXPIRATION_SECONDS);
    bindToFlag(String.class, StoreBackendName.class, FlagName.STORE_SERVER);
    bindToFlag(Integer.class, StoreBackendInstanceCount.class, FlagName.NUM_STORE_SERVERS);
    bindToFlag(Integer.class, SlobChannelExpirationSeconds.class,
        FlagName.OBJECT_CHANNEL_EXPIRATION_SECONDS);
    bindToFlag(Integer.class, PostCommitActionIntervalMillis.class,
        FlagName.POST_COMMIT_ACTION_INTERVAL_MILLIS);
    bindToFlag(Integer.class, SlobLocalCacheExpirationMillis.class,
        FlagName.SLOB_LOCAL_CACHE_EXPIRATION_MILLIS);
  }

  @Provides
  // The secret is read from the datastore; @Singleton to cache it for
  // efficiency.  If an admin deletes the secret entity, a new secret will be
  // generated, but existing instances will continue to use the old one.
  // Re-deploying should fix this since it restarts all instances.
  @Singleton
  Secret provideSecret(final CheckedDatastore datastore, final Random random)
      throws PermanentFailure {
    return new RetryHelper().run(
        new RetryHelper.Body<Secret>() {
          @Override public Secret run() throws RetryableFailure, PermanentFailure {
            CheckedTransaction tx = datastore.beginTransaction();
            try {
              {
                Entity e = tx.get(SECRET_KEY);
                if (e != null) {
                  log.info("Using stored secret");
                  return Secret.of(
                      DatastoreUtil.getExistingProperty(e, SECRET_PROPERTY, Blob.class).getBytes());
                }
              }
              Secret newSecret = Secret.generate(random);
              Entity e = new Entity(SECRET_KEY);
              DatastoreUtil.setNonNullUnindexedProperty(e, SECRET_PROPERTY,
                  new Blob(newSecret.getBytes()));
              tx.put(e);
              tx.commit();
              log.info("Generated new secret");
              return newSecret;
            } finally {
              tx.close();
            }
          }
        });
  }

  @Provides
  @FlagConfiguration
  @Singleton
  Map<FlagDeclaration, Object> provideFlagConfiguration(@Named("raw flag data") String rawFlagData)
      throws FlagFormatException {
    return JsonFlags.parse(Arrays.asList(FlagName.values()), rawFlagData);
  }

  @Provides
  @Named("raw flag data")
  @Singleton
  String provideRawFlagData(@Named("webinf root") String webinfRoot) {
    return Util.slurpRequired(webinfRoot + "/flags.json");
  }

  // TODO(ohler): Make this @Singleton without breaking GuiceSetupTest.
  @Provides @Named("buildinfo")
  String provideBuildinfo(@Named("webinf root") String webinfRoot) {
    return Util.slurpRequired(webinfRoot + "/buildinfo.txt");
  }

  @Provides
  DatastoreService provideDatastore() {
    return DatastoreProvider.strongReads();
  }

  @Provides
  MemcacheService provideMemcache() {
    return MemcacheServiceFactory.getMemcacheService();
  }

  @Provides
  URLFetchService provideUrlFetchService() {
    return URLFetchServiceFactory.getURLFetchService();
  }

  @Provides
  BlobstoreService provideBlobstoreService() {
    return BlobstoreServiceFactory.getBlobstoreService();
  }

  @Provides
  BlobInfoFactory provideBlobInfoFactory() {
    return new BlobInfoFactory();
  }

  @Provides
  ImagesService provideImagesService() {
    return ImagesServiceFactory.getImagesService();
  }

  @Provides
  ChannelService provideChannelService() {
    return ChannelServiceFactory.getChannelService();
  }

  @Provides
  UserService provideUserService() {
    return UserServiceFactory.getUserService();
  }

  @Provides
  BackendService provideBackendService() {
    return BackendServiceFactory.getBackendService();
  }

  @Singleton // NOTE(danilatos): The indexing documentation recommends a singleton.
  @Provides
  IndexManager provideIndexManager() {
    return IndexManagerFactory.newIndexManager();
  }

  @Provides
  SymbolMapsDirectory provideSymbolMapsDir(@Named("webinf root") String webinf) throws IOException {
    return new ZipSymbolMapsDirectory(
        new JarFile(new File(webinf, "gwt-extra.jar")), "symbolMaps");
  }

  @Provides @Named("channel api url")
  String provideChannelApiUrl() {
    return "/_ah/channel/jsapi";
  }

}
