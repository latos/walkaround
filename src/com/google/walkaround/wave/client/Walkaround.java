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

package com.google.walkaround.wave.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.walkaround.proto.ConnectResponse;
import com.google.walkaround.proto.WalkaroundWaveletSnapshot;
import com.google.walkaround.proto.WaveletDiffSnapshot;
import com.google.walkaround.proto.ClientVars.LiveClientVars;
import com.google.walkaround.proto.ClientVars.StaticClientVars;
import com.google.walkaround.proto.ClientVars.UdwLoadData;
import com.google.walkaround.proto.jso.ClientVarsJsoImpl;
import com.google.walkaround.proto.jso.DeltaJsoImpl;
import com.google.walkaround.slob.client.GenericOperationChannel.ReceiveOpChannel;
import com.google.walkaround.slob.client.GenericOperationChannel.SendOpService;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.util.client.log.ErrorReportingLogHandler;
import com.google.walkaround.util.client.log.LogPanel;
import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.util.shared.RandomBase64Generator;
import com.google.walkaround.util.shared.RandomBase64Generator.RandomProvider;
import com.google.walkaround.wave.client.MyFullDomRenderer.DocRefRenderer;
import com.google.walkaround.wave.client.attachment.DownloadThumbnailAction;
import com.google.walkaround.wave.client.attachment.UploadToolbarAction;
import com.google.walkaround.wave.client.attachment.WalkaroundAttachmentManager;
import com.google.walkaround.wave.client.profile.ContactsManager;
import com.google.walkaround.wave.client.rpc.AjaxRpc;
import com.google.walkaround.wave.client.rpc.AttachmentInfoService;
import com.google.walkaround.wave.client.rpc.ChannelConnectService;
import com.google.walkaround.wave.client.rpc.LoadWaveService;
import com.google.walkaround.wave.client.rpc.SubmitDeltaService;
import com.google.walkaround.wave.client.rpc.WaveletMap;
import com.google.walkaround.wave.client.rpc.LoadWaveService.ConnectCallback;
import com.google.walkaround.wave.client.rpc.WaveletMap.WaveletEntry;
import com.google.walkaround.wave.shared.IdHack;
import com.google.walkaround.wave.shared.Versions;
import com.google.walkaround.wave.shared.WaveSerializer;

import org.waveprotocol.wave.client.StageOne;
import org.waveprotocol.wave.client.StageThree;
import org.waveprotocol.wave.client.StageTwo;
import org.waveprotocol.wave.client.StageZero;
import org.waveprotocol.wave.client.Stages;
import org.waveprotocol.wave.client.account.Profile;
import org.waveprotocol.wave.client.account.ProfileManager;
import org.waveprotocol.wave.client.common.util.AsyncHolder;
import org.waveprotocol.wave.client.common.util.JsoView;
import org.waveprotocol.wave.client.concurrencycontrol.MuxConnector;
import org.waveprotocol.wave.client.concurrencycontrol.StaticChannelBinder;
import org.waveprotocol.wave.client.concurrencycontrol.WaveletOperationalizer;
import org.waveprotocol.wave.client.doodad.DoodadInstallers.ConversationInstaller;
import org.waveprotocol.wave.client.doodad.attachment.ImageThumbnail;
import org.waveprotocol.wave.client.editor.content.Registries;
import org.waveprotocol.wave.client.render.ReductionBasedRenderer;
import org.waveprotocol.wave.client.render.RenderingRules;
import org.waveprotocol.wave.client.scheduler.SchedulerInstance;
import org.waveprotocol.wave.client.scheduler.Scheduler.Task;
import org.waveprotocol.wave.client.uibuilder.UiBuilder;
import org.waveprotocol.wave.client.wave.LazyContentDocument;
import org.waveprotocol.wave.client.wave.RegistriesHolder;
import org.waveprotocol.wave.client.wave.SimpleDiffDoc;
import org.waveprotocol.wave.client.wave.WaveDocuments;
import org.waveprotocol.wave.client.wavepanel.render.HtmlDomRenderer;
import org.waveprotocol.wave.client.wavepanel.render.DocumentRegistries.Builder;
import org.waveprotocol.wave.client.wavepanel.view.BlipView;
import org.waveprotocol.wave.client.wavepanel.view.ParticipantView;
import org.waveprotocol.wave.client.wavepanel.view.dom.FullStructure;
import org.waveprotocol.wave.client.wavepanel.view.dom.ModelAsViewProvider;
import org.waveprotocol.wave.client.wavepanel.view.dom.ParticipantAvatarDomImpl;
import org.waveprotocol.wave.client.wavepanel.view.dom.UpgradeableDomAsViewProvider;
import org.waveprotocol.wave.client.wavepanel.view.dom.full.BlipQueueRenderer;
import org.waveprotocol.wave.client.wavepanel.view.dom.full.DomRenderer;
import org.waveprotocol.wave.client.wavepanel.view.dom.full.ParticipantAvatarViewBuilder;
import org.waveprotocol.wave.client.wavepanel.view.impl.ParticipantViewImpl;
import org.waveprotocol.wave.client.widget.popup.AlignedPopupPositioner;
import org.waveprotocol.wave.client.widget.profile.ProfilePopupView;
import org.waveprotocol.wave.client.widget.profile.ProfilePopupWidget;
import org.waveprotocol.wave.concurrencycontrol.channel.WaveViewService;
import org.waveprotocol.wave.model.conversation.Conversation;
import org.waveprotocol.wave.model.conversation.ConversationBlip;
import org.waveprotocol.wave.model.conversation.ConversationThread;
import org.waveprotocol.wave.model.document.indexed.IndexedDocumentImpl;
import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.id.IdGenerator;
import org.waveprotocol.wave.model.id.ModernIdSerialiser;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.schema.SchemaProvider;
import org.waveprotocol.wave.model.schema.conversation.ConversationSchemas;
import org.waveprotocol.wave.model.testing.RandomProviderImpl;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.IdentityMap;
import org.waveprotocol.wave.model.util.StringMap;
import org.waveprotocol.wave.model.wave.InvalidParticipantAddress;
import org.waveprotocol.wave.model.wave.ParticipantId;
import org.waveprotocol.wave.model.wave.Wavelet;
import org.waveprotocol.wave.model.wave.data.DocumentFactory;
import org.waveprotocol.wave.model.wave.data.ObservableWaveletData;
import org.waveprotocol.wave.model.wave.data.WaveViewData;
import org.waveprotocol.wave.model.wave.data.impl.ObservablePluggableMutableDocument;
import org.waveprotocol.wave.model.wave.data.impl.WaveViewDataImpl;
import org.waveprotocol.wave.model.wave.data.impl.WaveletDataImpl;

import javax.annotation.Nullable;

/**
 * Walkaround configuration for Undercurrent client
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class Walkaround implements EntryPoint {
// TODO(danilatos): Enable this once Undercurrent does not crash. Or, delete if undercurrent
// ends up doing it by default:
//
//  static {
//    CollectionUtils.setDefaultCollectionFactory(new JsoCollectionFactory());
//  }

  static final Log logger = Logs.create("client");

  private static final String WAVEPANEL_PLACEHOLDER = "initialHtml";
  // TODO(danilatos): flag
  private static final int VERSION_CHECK_INTERVAL_MS = 15 * 1000;

  private static final String OOPHM_SUFFIX = "&gwt.codesvr=127.0.0.1:9997";

  private final ClientVarsJsoImpl clientVars = nativeGetVars().cast();
  private final WaveSerializer serializer = new WaveSerializer(new ClientMessageSerializer());

  private final WaveletMap wavelets = new WaveletMap();
  private SlobId convObjectId;
  private SlobId udwObjectId;

  private IdGenerator idGenerator;

  private static native JsoView nativeGetVars() /*-{
    return $wnd.__vars;
  }-*/;

  private static LogPanel domLogger;

  private static boolean loaded;

  private WaveletEntry makeEntry(SlobId objectId,
      @Nullable ConnectResponse connectResponse, WaveletDataImpl wavelet) {
    if (connectResponse != null) {
      Preconditions.checkArgument(
          objectId.getId().equals(connectResponse.getSignedSession().getSession().getObjectId()),
          "Mismatched object ids: %s, %s", objectId, connectResponse);
    }
    return new WaveletEntry(objectId,
        connectResponse == null ? null : connectResponse.getSignedSessionString(),
        connectResponse == null ? null : connectResponse.getSignedSession().getSession(),
        connectResponse == null ? null : connectResponse.getChannelToken(),
        wavelet);
  }

  private WaveletEntry parseConvWaveletData(
      @Nullable ConnectResponse connectResponse, WaveletDiffSnapshot diffSnapshot,
      DocumentFactory<?> docFactory, StringMap<DocOp> diffMap) {
    WaveSerializer waveSerializer = new WaveSerializer(new ClientMessageSerializer(), docFactory);
    WaveletDataImpl wavelet;
    try {
      StringMap<DocOp> diffOps = waveSerializer.deserializeDocumentsDiffs(diffSnapshot);
      diffMap.putAll(diffOps);
      wavelet = waveSerializer.createWaveletData(
          IdHack.convWaveletNameFromConvObjectId(convObjectId), diffSnapshot);
    } catch (MessageException e) {
      throw new RuntimeException(e);
    }
    return makeEntry(convObjectId, connectResponse, wavelet);
  }

  private WaveletEntry parseUdwData(
      @Nullable ConnectResponse connectResponse, WalkaroundWaveletSnapshot snapshot,
      DocumentFactory<?> docFactory) {
    WaveSerializer serializer = new WaveSerializer(new ClientMessageSerializer(), docFactory);
    WaveletDataImpl wavelet;
    try {
      wavelet = serializer.createWaveletData(
          IdHack.udwWaveletNameFromConvObjectIdAndUdwObjectId(convObjectId, udwObjectId),
          snapshot);
    } catch (MessageException e) {
      throw new RuntimeException(e);
    }
    return makeEntry(udwObjectId, connectResponse, wavelet);
  }

  /**
   * Runs the harness script.
   */
  @Override
  public void onModuleLoad() {
    if (loaded) {
      return;
    }
    loaded = true;

    setupLogging();
    setupShell();

    // Bail early if the server sent us an error message
    if (clientVars.hasErrorVars()) {
      Element placeHolder = Document.get().getElementById(WAVEPANEL_PLACEHOLDER);
      String errorMessage = "Error: " + clientVars.getErrorVars().getErrorMessage();
      if (placeHolder != null) {
        placeHolder.setInnerText(errorMessage);
      } else {
        Window.alert(errorMessage);
      }
      return;
    }
    logger.log(Level.INFO, "Init");

    final SavedStateIndicator indicator = new SavedStateIndicator(
        Document.get().getElementById("savedStateContainer"));
    final AjaxRpc rpc = new AjaxRpc("", indicator);

    final boolean isLive;
    final boolean useUdw;
    @Nullable final UdwLoadData udwData;
    final int randomSeed;
    final String userIdString;
    final boolean haveOAuthToken;
    final String convObjectIdString;
    final WaveletDiffSnapshot convSnapshot;
    @Nullable final ConnectResponse convConnectResponse;
    if (clientVars.hasStaticClientVars()) {
      isLive = false;
      StaticClientVars vars = clientVars.getStaticClientVars();
      randomSeed = vars.getRandomSeed();
      userIdString = vars.getUserEmail();
      haveOAuthToken = vars.getHaveOauthToken();
      convObjectIdString = vars.getConvObjectId();
      convSnapshot = vars.getConvSnapshot();
      convConnectResponse = null;
      useUdw = false;
      udwData = null;
    } else {
      isLive = true;
      LiveClientVars vars = clientVars.getLiveClientVars();
      randomSeed = vars.getRandomSeed();
      userIdString = vars.getUserEmail();
      haveOAuthToken = vars.getHaveOauthToken();
      convObjectIdString =
          vars.getConvConnectResponse().getSignedSession().getSession().getObjectId();
      convSnapshot = vars.getConvSnapshot();
      convConnectResponse = vars.getConvConnectResponse();
      if (!vars.hasUdw()) {
        useUdw = false;
        udwData = null;
      } else {
        useUdw = true;
        udwData = vars.getUdw();
        udwObjectId = new SlobId(vars.getUdw().getConnectResponse()
            .getSignedSession().getSession().getObjectId());
      }
      VersionChecker versionChecker = new VersionChecker(rpc, vars.getClientVersion());
      // NOTE(danilatos): Use the highest priority timer, since we can't afford to
      // let it be starved due to some bug with another non-terminating
      // high-priority task. This task runs infrequently and is very minimal so
      // the risk of impacting the UI is low.
      SchedulerInstance.getHighPriorityTimer().scheduleRepeating(versionChecker,
          VERSION_CHECK_INTERVAL_MS, VERSION_CHECK_INTERVAL_MS);
    }
    final RandomProviderImpl random =
        // TODO(ohler): Get a stronger RandomProvider.
        RandomProviderImpl.ofSeed(randomSeed);
    final RandomBase64Generator random64 = new RandomBase64Generator(new RandomProvider() {
      @Override public int nextInt(int upperBound) {
        return random.nextInt(upperBound);
      }});
    final ParticipantId userId;
    try {
      userId = ParticipantId.of(userIdString);
    } catch (InvalidParticipantAddress e1) {
      Window.alert("Invalid user id received from server: " + userIdString);
      return;
    }
    convObjectId = new SlobId(convObjectIdString);

    idGenerator = new IdHack.MinimalIdGenerator(
        IdHack.convWaveletIdFromObjectId(convObjectId),
        useUdw ? IdHack.udwWaveletIdFromObjectId(udwObjectId)
        // Some code depends on this not to return null, so let's return
        // something.
        : IdHack.DISABLED_UDW_ID,
        random64);

    // TODO(ohler): Make the server's response to the contacts RPC indicate
    // whether an OAuth token is needed, and enable the button dynamically when
    // appropriate, rather than statically.
    UIObject.setVisible(Document.get().getElementById("enableAvatarsButton"),
        !haveOAuthToken);
    @Nullable final LoadWaveService loadWaveService = isLive ? new LoadWaveService(rpc) : null;
    @Nullable final ChannelConnectService channelService =
        isLive ? new ChannelConnectService(rpc) : null;

    new Stages() {
      @Override
      protected AsyncHolder<StageZero> createStageZeroLoader() {
        return new StageZero.DefaultProvider() {
          @Override
          protected UncaughtExceptionHandler createUncaughtExceptionHandler() {
            return WalkaroundUncaughtExceptionHandler.INSTANCE;
          }
        };
      }

      @Override
      protected AsyncHolder<StageOne> createStageOneLoader(StageZero zero) {
        return new StageOne.DefaultProvider(zero) {
          protected final ParticipantViewImpl.Helper<ParticipantAvatarDomImpl> participantHelper =
              new ParticipantViewImpl.Helper<ParticipantAvatarDomImpl>() {
                @Override public void remove(ParticipantAvatarDomImpl impl) {
                  impl.remove();
                }
                @Override public ProfilePopupView showParticipation(ParticipantAvatarDomImpl impl) {
                  return new ProfilePopupWidget(impl.getElement(),
                      AlignedPopupPositioner.BELOW_RIGHT);
                }
              };

          @Override protected UpgradeableDomAsViewProvider createViewProvider() {
            return new FullStructure(createCssProvider()) {
              @Override public ParticipantView asParticipant(Element e) {
                return e == null ? null : new ParticipantViewImpl<ParticipantAvatarDomImpl>(
                    participantHelper, ParticipantAvatarDomImpl.of(e));
              }
            };
          }
        };
      }

      @Override
      protected AsyncHolder<StageTwo> createStageTwoLoader(final StageOne one) {
        return new StageTwo.DefaultProvider(one) {
          WaveViewData waveData;
          StringMap<DocOp> diffMap = CollectionUtils.createStringMap();

          @Override protected DomRenderer createRenderer() {
            final BlipQueueRenderer pager = getBlipQueue();
            DocRefRenderer docRenderer = new DocRefRenderer() {
                @Override
                public UiBuilder render(
                    ConversationBlip blip, IdentityMap<ConversationThread, UiBuilder> replies) {
                  // Documents are rendered blank, and filled in later when
                  // they get paged in.
                  pager.add(blip);
                  return DocRefRenderer.EMPTY.render(blip, replies);
                }
              };
            RenderingRules<UiBuilder> rules = new MyFullDomRenderer(
                getBlipDetailer(), docRenderer, getProfileManager(),
                getViewIdMapper(), createViewFactories(), getThreadReadStateMonitor()) {
              @Override
              public UiBuilder render(Conversation conversation, ParticipantId participant) {
                // Same as super class, but using avatars instead of names.
                Profile profile = getProfileManager().getProfile(participant);
                String id = getViewIdMapper().participantOf(conversation, participant);
                ParticipantAvatarViewBuilder participantUi =
                    ParticipantAvatarViewBuilder.create(id);
                participantUi.setAvatar(profile.getImageUrl());
                participantUi.setName(profile.getFullName());
                return participantUi;
              }
            };
            return new HtmlDomRenderer(ReductionBasedRenderer.of(rules, getConversations()));
          }

          @Override
          protected ProfileManager createProfileManager() {
            return ContactsManager.create(rpc);
          }

          @Override
          protected void create(final Accessor<StageTwo> whenReady) {
            super.create(whenReady);
          }

          @Override
          protected IdGenerator createIdGenerator() {
            return idGenerator;
          }

          @Override
          protected void fetchWave(final Accessor<WaveViewData> whenReady) {
            wavelets.updateData(
                parseConvWaveletData(
                    convConnectResponse, convSnapshot,
                    getDocumentRegistry(), diffMap));
            if (useUdw) {
              wavelets.updateData(
                  parseUdwData(
                      udwData.getConnectResponse(),
                      udwData.getSnapshot(),
                      getDocumentRegistry()));
            }
            Document.get().getElementById(WAVEPANEL_PLACEHOLDER).setInnerText("");
            waveData = createWaveViewData();
            whenReady.use(waveData);
          }

          @Override
          protected WaveDocuments<LazyContentDocument> createDocumentRegistry() {
            IndexedDocumentImpl.performValidation = false;

            DocumentFactory<?> dataDocFactory =
                ObservablePluggableMutableDocument.createFactory(createSchemas());
            DocumentFactory<LazyContentDocument> blipDocFactory =
                new DocumentFactory<LazyContentDocument>() {
                  private final Registries registries = RegistriesHolder.get();

                  @Override
                  public LazyContentDocument create(
                      WaveletId waveletId, String docId, DocInitialization content) {
                    SimpleDiffDoc diff = SimpleDiffDoc.create(content, diffMap.get(docId));
                    return LazyContentDocument.create(registries, diff);
                  }
                };

            return WaveDocuments.create(blipDocFactory, dataDocFactory);
          }

          @Override
          protected ParticipantId createSignedInUser() {
            return userId;
          }

          @Override
          protected String createSessionId() {
            // TODO(ohler): Write a note here about what this is and how it
            // interacts with walkaround's session management.
            return random64.next(6);
          }

          @Override
          protected MuxConnector createConnector() {
            return new MuxConnector() {
              private void connectWavelet(StaticChannelBinder binder,
                  ObservableWaveletData wavelet) {
                WaveletId waveletId = wavelet.getWaveletId();
                SlobId objectId = IdHack.objectIdFromWaveletId(waveletId);
                WaveletEntry data = wavelets.get(objectId);
                Assert.check(data != null, "Unknown wavelet: %s", waveletId);
                if (data.getChannelToken() == null) {
                  // TODO(danilatos): Handle with a nicer message, and maybe try to
                  // reconnect later.
                  Window.alert("Could not open a live connection to this wave. "
                      + "It will be read-only, changes will not be saved!");
                  return;
                }

                String debugSuffix;
                if (objectId.equals(convObjectId)) {
                  debugSuffix = "-c";
                } else if (objectId.equals(udwObjectId)) {
                  debugSuffix = "-u";
                } else {
                  debugSuffix = "-xxx";
                }

                ReceiveOpChannel<WaveletOperation> storeChannel =
                    new GaeReceiveOpChannel<WaveletOperation>(
                        objectId, data.getSignedSessionString(), data.getChannelToken(),
                        channelService, Logs.create("gaeroc" + debugSuffix)) {
                      @Override
                      protected WaveletOperation parse(ChangeData<JavaScriptObject> message)
                          throws MessageException {
                        return serializer.deserializeDelta(
                            message.getPayload().<DeltaJsoImpl>cast());
                      }
                    };

                WalkaroundOperationChannel channel = new WalkaroundOperationChannel(
                    Logs.create("channel" + debugSuffix),
                    createSubmitService(objectId),
                    storeChannel, Versions.truncate(wavelet.getVersion()),
                    data.getSession().getClientId(),
                    indicator);
                String id = ModernIdSerialiser.INSTANCE.serialiseWaveletId(waveletId);
                binder.bind(id, channel);
              }

              @Override
              public void connect(Command onOpened) {
                if (isLive) {
                  WaveletOperationalizer operationalizer = getWavelets();
                  StaticChannelBinder binder = new StaticChannelBinder(
                      operationalizer, getDocumentRegistry());
                  for (ObservableWaveletData wavelet : operationalizer.getWavelets()) {
                    if (useUdw || !IdHack.DISABLED_UDW_ID.equals(wavelet.getWaveletId())) {
                      connectWavelet(binder, wavelet);
                    }
                  }
                  // HACK(ohler): I haven't tried to understand what the semantics of the callback
                  // are; perhaps we should invoke it even if the wave is static.  (It seems to be
                  // null though.)
                  if (onOpened != null) {
                    onOpened.execute();
                  }
                }
              }

              @Override
              public void close() {
                throw new AssertionError("close not implemented");
              }
            };
          }

          private SubmitDeltaService createSubmitService(final SlobId objectId) {
            return new SubmitDeltaService(rpc, wavelets, objectId) {
              @Override
              public void requestRevision(final SendOpService.Callback callback) {
                loadWaveService.fetchWaveRevision(
                    wavelets.get(objectId).getSignedSessionString(),
                    new ConnectCallback() {
                      @Override public void onData(ConnectResponse data) {
                        // TODO(danilatos): Update session id etc in the operation channel
                        // in order for (something equivalent to) this to work.
                        // But don't have submit delta service keep a reference to this map.
                        // ALSO TODO: channelToken could be null, if a channel could not be opened.
                        // In this case the object should be opened as readonly.
                        //wavelets.updateData(
                        //  new WaveletEntry(id, channelToken, sid, xsrfToken, revision, null));
                        callback.onSuccess(Versions.truncate(data.getObjectVersion()));
                      }

                      @Override public void onConnectionError(Throwable e) {
                        callback.onConnectionError(e);
                      }
                    });
              }
            };
          }

          @Override
          protected WaveViewService createWaveViewService() {
            // unecessary
            throw new UnsupportedOperationException();
          }

          @Override
          protected SchemaProvider createSchemas() {
            return new ConversationSchemas();
          }

          @Override
          protected Builder installDoodads(Builder doodads) {
            doodads = super.installDoodads(doodads);
            doodads.use(new ConversationInstaller() {
              @Override
              public void install(Wavelet w, Conversation c, Registries r) {
                WalkaroundAttachmentManager mgr = new WalkaroundAttachmentManager(
                    new AttachmentInfoService(rpc), logger);
                ImageThumbnail.register(r.getElementHandlerRegistry(), mgr,
                    new DownloadThumbnailAction());
              }
            });
            return doodads;
          }

          @Override
          protected void installFeatures() {
            super.installFeatures();
            ReadStateSynchronizer.create(FakeReadStateService.create(), getReadMonitor());
          }
        };
      }

      @Override
      protected AsyncHolder<StageThree> createStageThreeLoader(final StageTwo two) {
        return new StageThree.DefaultProvider(two) {
          @Override
          protected void install() {
            // Inhibit if not live; super.install() seems to enable editing in Undercurrent.
            // Haven't studied this carefully though.
            if (isLive) {
              super.install();
            }
          }

          @Override
          protected void create(final Accessor<StageThree> whenReady) {
            // Prepend an init wave flow onto the stage continuation.
            super.create(new Accessor<StageThree>() {
              @Override
              public void use(StageThree three) {
                if (isLive) {
                  maybeNewWaveSetup(two, three);
                  UploadToolbarAction.install(three.getEditSession(), three.getEditToolbar());
                  // HACK(ohler): I haven't tried to understand what the semantics of the callback
                  // are; perhaps we should invoke it even if the wave is static.
                  whenReady.use(three);
                }
              }
            });
          }
        };
      }

      private void maybeNewWaveSetup(StageTwo two, StageThree three) {
        ModelAsViewProvider views = two.getModelAsViewProvider();
        Conversation rootConv = two.getConversations().getRoot();

        if (looksLikeANewWave(rootConv)) {
          BlipView blipUi = views.getBlipView(rootConv.getRootThread().getFirstBlip());

          // Needed because startEditing must have an editor already rendered.
          two.getBlipQueue().flush();
          three.getEditActions().startEditing(blipUi);
        }
      }

      private boolean looksLikeANewWave(Conversation rootConv) {
        return Iterables.size(rootConv.getRootThread().getBlips()) == 1
            && rootConv.getRootThread().getFirstBlip().getContent().size() == 4;
      }
    }.load(null);
  }

  private WaveViewDataImpl createWaveViewData() {
    final WaveViewDataImpl waveData = WaveViewDataImpl.create(
        IdHack.waveIdFromConvObjectId(convObjectId));
    wavelets.each(new WaveletMap.Proc() {
      @Override public void wavelet(WaveletEntry data) {
        WaveletDataImpl wavelet = data.getWaveletState();
        waveData.addWavelet(wavelet);
      }
    });
    return waveData;
  }

  private void setupShell() {
    DebugMenu menu = new DebugMenu();

    boolean shouldShowDebug =
        Window.Location.getParameter("debug") != null;
    menu.setVisible(shouldShowDebug);

    menu.addItem("OOPHM", navigateTaskOophm(Window.Location.getHref(), false));
    menu.addItem("Show log", new Task() {
        @Override public void execute() {
          Walkaround.ensureDomLog();
        }
      });
    menu.addItem("log level 'debug'", navigateTask(
        Window.Location.getHref() + "&ll=debug", false));
    menu.addItem("Frame: " + Window.Location.getHref(),
        navigateTask(Window.Location.getHref(), true));
    menu.addItem("Throw Exception", new Task() {
        @Override public void execute() {
          throw new RuntimeException("TEST EXCEPTION", a());
        }

        private Exception a() {
          return b();
        }

        private Exception b() {
          return new Exception("Nested test exception cause");
        }
      });

    menu.install();
  }

  private void setupLogging() {
    Logs.get().addHandler(new ErrorReportingLogHandler("/gwterr") {
      @Override protected void onSevere(String stream) {
        alertDomLog(stream);
      }
    });
  }

  /**
   * If ths DOM log has not been created yet, creates it and draws attention to
   * a stream.
   */
  private static void alertDomLog(final String attentionStream) {
    if (domLogger == null) {
      domLogger = LogPanel.createOnStream(Logs.get(), attentionStream);
      attachLogPanel();
    }
  }

  /**
   * If ths DOM log has not been created yet, creates it.
   */
  private static void ensureDomLog() {
    if (domLogger == null) {
      domLogger = LogPanel.create(Logs.get());
      attachLogPanel();
    }
  }

  private static Task navigateTaskOophm(String url, boolean newPage) {
    if (!url.endsWith(OOPHM_SUFFIX)) {
      url += OOPHM_SUFFIX;
    }
    return navigateTask(url, newPage);
  }

  private static Task navigateTask(final String url, final boolean newPage) {
    return new Task() {
      @Override public void execute() {
        if (newPage) {
          Window.open(url, "_blank", null);
        } else {
          Window.Location.assign(url);
        }
      }
    };
  }

  /** Reveals the log div, and executes a task when done. */
  // The async API for this method is intended to support two things: a cool
  // spew animation, and also the potential to runAsync the whole LogPanel code.
  private static void attachLogPanel() {
    Logs.get().addHandler(domLogger);
    final Panel logHolder = RootPanel.get("logHolder");
    logHolder.setVisible(true);

    // Need one layout and paint cycle after revealing it to start animation.
    // Use high priority to avoid potential starvation by other tasks if a
    // problem is occurring.
    SchedulerInstance.getHighPriorityTimer().scheduleDelayed(new Task() {
      @Override
      public void execute() {
        logHolder.add(domLogger);
        Style waveStyle = Document.get().getElementById(WAVEPANEL_PLACEHOLDER).getStyle();
        Style logStyle = logHolder.getElement().getStyle();
        logStyle.setHeight(250, Unit.PX);
        waveStyle.setBottom(250, Unit.PX);
      }
    }, 50);
  }

}
