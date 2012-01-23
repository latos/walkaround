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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.appengine.tools.mapreduce.MapReduceServlet;
import com.google.common.collect.ImmutableMap;
import com.google.gdata.client.AuthTokenFactory;
import com.google.gdata.client.GoogleService.SessionExpiredException;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequestFactory;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.client.http.GoogleGDataRequest;
import com.google.gdata.client.http.HttpAuthToken;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.google.walkaround.slob.server.StoreModuleHelper;
import com.google.walkaround.slob.server.handler.PostCommitTaskHandler;
import com.google.walkaround.util.server.auth.InvalidSecurityTokenException;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.util.server.servlet.ExactPathHandlers;
import com.google.walkaround.util.server.servlet.HandlerServlet;
import com.google.walkaround.util.server.servlet.PrefixPathHandlers;
import com.google.walkaround.util.server.servlet.RedirectServlet;
import com.google.walkaround.util.server.servlet.RequestStatsFilter;
import com.google.walkaround.wave.server.admin.AdminHandler;
import com.google.walkaround.wave.server.admin.BuildinfoHandler;
import com.google.walkaround.wave.server.admin.ClearMemcacheHandler;
import com.google.walkaround.wave.server.admin.FlagsHandler;
import com.google.walkaround.wave.server.admin.StoreViewHandler;
import com.google.walkaround.wave.server.attachment.AttachmentDownloadHandler;
import com.google.walkaround.wave.server.attachment.AttachmentFormHandler;
import com.google.walkaround.wave.server.attachment.AttachmentMetadataHandler;
import com.google.walkaround.wave.server.attachment.AttachmentUploadHandler;
import com.google.walkaround.wave.server.auth.DeleteOAuthTokenHandler;
import com.google.walkaround.wave.server.auth.InteractiveAuthFilter;
import com.google.walkaround.wave.server.auth.OAuthCallbackHandler;
import com.google.walkaround.wave.server.auth.OAuthCredentials;
import com.google.walkaround.wave.server.auth.OAuthInterstitialHandler;
import com.google.walkaround.wave.server.auth.OAuthRequestHelper;
import com.google.walkaround.wave.server.auth.RpcAuthFilter;
import com.google.walkaround.wave.server.auth.StableUserId;
import com.google.walkaround.wave.server.auth.UserContext;
import com.google.walkaround.wave.server.auth.OAuthInterstitialHandler.CallbackPath;
import com.google.walkaround.wave.server.auth.XsrfHelper.XsrfTokenExpiredException;
import com.google.walkaround.wave.server.googleimport.ImportOverviewHandler;
import com.google.walkaround.wave.server.googleimport.ImportTaskHandler;
import com.google.walkaround.wave.server.googleimport.RobotApi;
import com.google.walkaround.wave.server.rpc.ChannelHandler;
import com.google.walkaround.wave.server.rpc.ClientExceptionHandler;
import com.google.walkaround.wave.server.rpc.ClientVersionHandler;
import com.google.walkaround.wave.server.rpc.ConnectHandler;
import com.google.walkaround.wave.server.rpc.ContactsHandler;
import com.google.walkaround.wave.server.rpc.GadgetsHandler;
import com.google.walkaround.wave.server.rpc.HistoryHandler;
import com.google.walkaround.wave.server.rpc.PhotosHandler;
import com.google.walkaround.wave.server.rpc.SubmitDeltaHandler;
import com.google.walkaround.wave.server.servlet.ClientHandler;
import com.google.walkaround.wave.server.servlet.IndexHandler;
import com.google.walkaround.wave.server.servlet.LogoutHandler;
import com.google.walkaround.wave.server.servlet.ServerExceptionFilter;
import com.google.walkaround.wave.server.servlet.StoreMutateHandler;
import com.google.walkaround.wave.server.servlet.UndercurrentHandler;
import com.google.walkaround.wave.server.servlet.LogoutHandler.SelfClosingPageHandler;
import com.google.walkaround.wave.server.wavemanager.InboxHandler;
import com.google.walkaround.wave.shared.SharedConstants.Services;

import org.waveprotocol.wave.model.wave.ParticipantId;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class WalkaroundServletModule extends ServletModule {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(WalkaroundServletModule.class.getName());

  // NOTE(danilatos): The callback path is displayed in the oauth permission
  // dialog, so we're using something that sounds legitimate and secure
  // (e.g. the suggested "oauth2callback" might be a bit odd to users).
  private static final String OAUTH2_CALLBACK_PATH = "authenticate";

  public static final String IMPORT_TASK_PATH = "/taskqueue/import";
  static final String POST_COMMIT_TASK_PATH = "/taskqueue/postcommit";

  /** Path bindings for handlers that serve exact paths only. */
  private static final ImmutableMap<String, Class<? extends AbstractHandler>> EXACT_PATH_HANDLERS =
      new ImmutableMap.Builder<String, Class<? extends AbstractHandler>>()
          // Pages that browsers will navigate to.
          .put("/", IndexHandler.class)
          .put("/client", ClientHandler.class)
          .put("/inbox", InboxHandler.class)
          .put("/wave", UndercurrentHandler.class)
          .put("/logout", LogoutHandler.class)
          .put("/switchSuccess", SelfClosingPageHandler.class)
          .put("/enable", OAuthInterstitialHandler.class)
          .put("/noauth", DeleteOAuthTokenHandler.class)
          .put("/" + OAUTH2_CALLBACK_PATH, OAuthCallbackHandler.class)

          // Endpoints for RPCs etc.
          .put("/" + Services.CHANNEL, ChannelHandler.class)
          .put("/" + Services.CONNECT, ConnectHandler.class)
          .put("/" + Services.HISTORY, HistoryHandler.class)
          .put("/" + Services.SUBMIT_DELTA, SubmitDeltaHandler.class)
          .put("/contacts", ContactsHandler.class)
          .put("/version", ClientVersionHandler.class)
          .put("/photos", PhotosHandler.class)
          .put("/gwterr", ClientExceptionHandler.class)

          // Other stuff.
          .put("/upload", AttachmentUploadHandler.class)
          .put("/uploadform", AttachmentFormHandler.class)
          .put("/download", AttachmentDownloadHandler.class)
          .put("/thumbnail", AttachmentDownloadHandler.class)
          .put("/attachmentinfo", AttachmentMetadataHandler.class)

          .put("/admin", AdminHandler.class)
          .put("/admin/buildinfo", BuildinfoHandler.class)
          .put("/admin/clearmemcache", ClearMemcacheHandler.class)
          .put("/admin/flags", FlagsHandler.class)
          .put("/admin/storeview", StoreViewHandler.class)

          // Backend servers. Could potentially use a separate Guice module.
          .put("/store/mutate", StoreMutateHandler.class)

          // Slob task queue stuff.
          .put(POST_COMMIT_TASK_PATH, PostCommitTaskHandler.class)

          // Import stuff.  Should probably also be in a separate Guice module.
          .put("/import", ImportOverviewHandler.class)
          .put(IMPORT_TASK_PATH, ImportTaskHandler.class)

          .build();

  /** Path bindings for handlers that serve all paths under some prefix. */
  private static final ImmutableMap<String, Class<? extends AbstractHandler>> PREFIX_PATH_HANDLERS =
    new ImmutableMap.Builder<String, Class<? extends AbstractHandler>>()
          .put("/gadgets", GadgetsHandler.class)
        .build();


  /** Checks that there are no conflicts between paths in the handler maps. */
  private static void validatePaths() {
    for (String prefix : PREFIX_PATH_HANDLERS.keySet()) {
      for (String exact : EXACT_PATH_HANDLERS.keySet()) {
        if (exact.startsWith(prefix)) {
          throw new AssertionError(
              "Handler conflict between prefix path " + prefix + " and exact path " + exact);
        }
      }
      for (String otherPrefix : PREFIX_PATH_HANDLERS.keySet()) {
        if (!otherPrefix.equals(prefix) && otherPrefix.startsWith(prefix)) {
          throw new AssertionError(
              "Handler conflict between prefix path " + prefix + " and prefix path " + otherPrefix);
        }
      }
    }
  }

  private final Iterable<? extends Filter> extraFilters;

  public WalkaroundServletModule(Iterable<? extends Filter> extraFilters) {
    this.extraFilters = extraFilters;
  }

  @Provides @CallbackPath
  String provideOauthCallbackPath(@Named("wave url base") String base) {
    return base + OAUTH2_CALLBACK_PATH;
  }

  @Override protected void configureServlets() {
    if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Development) {
      // Doesn't work in local dev server mode.
      filter("*").through(AppstatsFilter.class, ImmutableMap.of("basePath", "/admin/appstats/"));
    }
    filter("*").through(ServerExceptionFilter.class);
    // We want appstats and ServerExceptionFilter as the outermost layers.  We
    // use the extraFilters hook for monitoring, so it has to be before
    // RequestStatsFilter; that means it has to be here.  Other uses might need
    // additional hooks to put filters elsewhere (e.g. after authentication).
    for (Filter f : extraFilters) {
      filter("*").through(f);
    }
    filter("*").through(RequestStatsFilter.class);

    serve("/admin/").with(new RedirectServlet("/admin"));
    serve("/import/").with(new RedirectServlet("/import"));
    serve("/admin/mapreduce").with(new RedirectServlet("/admin/mapreduce/status"));
    serve("/admin/mapreduce/").with(new RedirectServlet("/admin/mapreduce/status"));

    bind(String.class).annotatedWith(Names.named("gadget serve path")).toInstance("/gadgets");
    bind(String.class).annotatedWith(Names.named("gadget server")).toInstance(
        "http://gmodules.com/gadgets");

    install(StoreModuleHelper.factoryModule(RobotApi.Factory.class, RobotApi.class));

    // All of the exact paths in EXACT_PATH_HANDLERS, and all the path prefixes
    // from PREFIX_PATH_HANDLERS, are served with HandlerServlet.
    validatePaths();
    {
      MapBinder<String, AbstractHandler> exactPathBinder =
          MapBinder.newMapBinder(binder(),
              String.class, AbstractHandler.class, ExactPathHandlers.class);
      for (Map.Entry<String, Class<? extends AbstractHandler>> e : EXACT_PATH_HANDLERS.entrySet()) {
        serve(e.getKey()).with(HandlerServlet.class);
        exactPathBinder.addBinding(e.getKey()).to(e.getValue());
      }
    }
    {
      MapBinder<String, AbstractHandler> prefixPathBinder =
          MapBinder.newMapBinder(binder(),
              String.class, AbstractHandler.class, PrefixPathHandlers.class);
      for (Map.Entry<String, Class<? extends AbstractHandler>> e
               : PREFIX_PATH_HANDLERS.entrySet()) {
        serve(e.getKey() + "/*").with(HandlerServlet.class);
        prefixPathBinder.addBinding(e.getKey()).to(e.getValue());
      }
    }

    bind(AppstatsFilter.class).in(Singleton.class);
    bind(AppstatsServlet.class).in(Singleton.class);
    serve("/admin/appstats*").with(AppstatsServlet.class);

    bind(MapReduceServlet.class).in(Singleton.class);
    serve("/admin/mapreduce/*").with(MapReduceServlet.class);

    for (String path : Arrays.asList(
            "/", "/client", "/inbox", "/noauth", "/wave", "/import")) {
      filter(path).through(InteractiveAuthFilter.class);
    }
    for (String path : Arrays.asList(
            "/connect", "/submitdelta", "/channel", "/history", "/contacts", "/photos")) {
      filter(path).through(RpcAuthFilter.class);
    }
  }


  // RequestScoped because it involves parsing and crypto, so we want it cached.
  @Provides @RequestScoped
  ObjectSession provideVerifiedSession(ObjectSessionHelper helper, HttpServletRequest req) {
    try {
      return helper.getVerifiedSession(req);
    } catch (InvalidSecurityTokenException e) {
      throw new BadRequestException(e);
    } catch (XsrfTokenExpiredException e) {
      throw new BadRequestException(e);
    }
  }

  // RequestScoped because we don't know how efficient it is, so we want it cached.
  @Provides @RequestScoped
  User provideAppengineUser(UserService userService) {
    User user = userService.getCurrentUser();
    if (user == null) {
      throw new ProvisionException("Not logged in");
    }
    return user;
  }

  @Provides
  StableUserId provideStableUserId(UserContext context) {
    return context.getUserId();
  }

  @Provides
  ParticipantId provideParticipantId(UserContext context) {
    return context.getParticipantId();
  }

  @Provides
  OAuthCredentials provideOAuthCredentials(UserContext context) {
    return context.getOAuthCredentials();
  }

  @Provides
  ContactsService provideContactService(
      @Flag(FlagName.OAUTH_CLIENT_ID) String clientId,
      final OAuthRequestHelper helper) {
    final HttpAuthToken authToken = new HttpAuthToken() {
        @Override public String getAuthorizationHeader(URL requestUrl, String requestMethod) {
          log.info("getAuthorizationHeader(" + requestUrl + ", " + requestMethod + ") = "
              + helper.getAuthorizationHeaderValue());
          return helper.getAuthorizationHeaderValue();
        }
      };
    GDataRequestFactory factory = new GoogleGDataRequest.Factory() {
        @Override protected GDataRequest createRequest(RequestType type, URL requestUrl,
            ContentType contentType) throws IOException, ServiceException {
          return new GoogleGDataRequest(type, requestUrl, contentType, authToken,
              headerMap, privateHeaderMap, connectionSource) {
            // Superclass method barfs with a NullPointerException because it
            // expects a WWW-Authenticate header that isn't there, so override
            // it.
            @Override protected void handleErrorResponse() throws ServiceException, IOException {
              if (httpConn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new SessionExpiredException("Got response code " + httpConn.getResponseCode()
                    + ", token has probably expired");
              }
              super.handleErrorResponse();
            }
          };
        }
      };
    factory.setAuthToken(authToken);
    ContactsService contacts = new ContactsService(clientId,
        factory,
        new AuthTokenFactory() {
          @Override public AuthToken getAuthToken() {
            log.info("getAuthToken()");
            return authToken;
          }
          @Override public void handleSessionExpiredException(SessionExpiredException expired) {
            // Let's not log the stack trace, the exception is usually harmless.
            log.log(Level.INFO, "Session expired: " + expired);
            try {
              helper.refreshToken();
            } catch (IOException e) {
              throw new RuntimeException("IOException refreshing token", e);
            }
          }
        });
    return contacts;
  }

  // RequestScoped because it involves parsing and string construction, so we want it cached.
  @Provides @RequestScoped @Named("wave url base")
  String provideWaveUrlBase(HttpServletRequest req) throws MalformedURLException {
    URL requestUrl = new URL(req.getRequestURL().toString());
    String portString = requestUrl.getPort() == -1 ? "" : ":" + requestUrl.getPort();
    String waveUrlBase = requestUrl.getProtocol() + "://" + requestUrl.getHost() + portString + "/";
    log.info("requestUrl=" + requestUrl + ", waveUrlBase=" + waveUrlBase);
    return waveUrlBase;
  }

}
