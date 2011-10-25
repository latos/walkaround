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
import com.google.walkaround.slob.client.GenericOperationChannel;
import com.google.walkaround.slob.client.GenericOperationChannel.ReceiveOpChannel;
import com.google.walkaround.slob.client.GenericOperationChannel.SendOpService;
import com.google.walkaround.slob.client.TransformQueue.Transformer;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;

import org.waveprotocol.wave.client.scheduler.SchedulerInstance;
import org.waveprotocol.wave.concurrencycontrol.channel.OperationChannel;
import org.waveprotocol.wave.concurrencycontrol.client.MergingSequence;
import org.waveprotocol.wave.model.operation.OperationPair;
import org.waveprotocol.wave.model.operation.TransformException;
import org.waveprotocol.wave.model.operation.wave.Transform;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.version.HashedVersion;

import java.util.List;
import java.util.Queue;

/**
 * Adapts GenericOperationChannel to implement wave's OperationChannel.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class WalkaroundOperationChannel implements OperationChannel {

  private static int lastChannelId;

  // NOTE(danilatos): Wavelet id would also work fine.
  private final String channelId = "" + (++lastChannelId);

  public interface SavedStateListener {
    void unsaved(String channelId);
    void saved(String channelId);
    void turbulenced();
  }

  /**
   * Wave requires {code VersionUpdateOp}s in the operation stream from the
   * channel wherever a client acknowledgment occurred. We create a buffer that
   * contains these ops for each ack, and null for each remote wavelet
   * operation. We can't eagerly fill the buffer with the remote operations
   * because those operations may need to be transformed further against
   * subsequent client ops. So we should only e.g. {@link #peek()} at the very
   * last minute.
   */
  private final Queue<WaveletOperation> versionUpdatesBuffer = CollectionUtils.createQueue();

  public static final boolean HACK_NO_INCOMING = false;

  private final Transformer<WaveletOperation> transformer = new Transformer<WaveletOperation>() {
    @Override
    public OperationPair<WaveletOperation> transform(
        WaveletOperation clientOp, WaveletOperation serverOp) {
      try {
        return Transform.transform(clientOp, serverOp);
      } catch (TransformException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public List<WaveletOperation> compact(List<WaveletOperation> clientOps) {
      MergingSequence m = new MergingSequence(clientOps);
      m.optimise();
      return m;
    }
  };

  private final GenericOperationChannel.Listener<WaveletOperation> channelListener =
      new GenericOperationChannel.Listener<WaveletOperation>() {
        @Override
        public void onError(Throwable e) {
          logger.log(Level.WARNING, e);
          savedStateListener.turbulenced();
        }

        @Override
        public void onRemoteOp(WaveletOperation serverHistoryOp) {
          versionUpdatesBuffer.add(null);
          listener.onOperationReceived();
        }

        @Override
        public void onAck(WaveletOperation serverHistoryOp, boolean isClean) {
          if (isClean) {
            savedStateListener.saved(channelId);
          }

          versionUpdatesBuffer.add(serverHistoryOp.createVersionUpdateOp(1, null));
          listener.onOperationReceived();
        }
      };

  private final Log logger;
  private final GenericOperationChannel<WaveletOperation> channel;
  private final String sessionId;
  private final SavedStateListener savedStateListener;
  private final int startVersion;

  Listener listener;

  public WalkaroundOperationChannel(Log logger,
      SendOpService<WaveletOperation> sendService,
      ReceiveOpChannel<WaveletOperation> receiveChannel,
      int startVersion, String sessionId, SavedStateListener savedStateListener) {
    Preconditions.checkNotNull(sendService, "Null sendService");
    Preconditions.checkNotNull(receiveChannel, "Null channel");
    Preconditions.checkNotNull(sessionId, "Null sessionId");
    Preconditions.checkArgument(startVersion >= 0, "Invalid startVersion, %s", startVersion);
    this.logger = logger;
    this.startVersion = startVersion;
    this.sessionId = sessionId;
    this.savedStateListener = savedStateListener;

    this.channel = new GenericOperationChannel<WaveletOperation>(
        SchedulerInstance.getMediumPriorityTimer(), transformer,
        receiveChannel, sendService, channelListener, logger);
  }

  @Override
  public String getDebugString() {
    return "Blah blah not implemented";
  }

  @Override
  public List<HashedVersion> getReconnectVersions() {
    throw new AssertionError("Not implemented");
  }

  @Override
  public WaveletOperation peek() {
    if (versionUpdatesBuffer.isEmpty()) {
      return null;
    }
    return versionUpdatesBuffer.peek() != null
        ? versionUpdatesBuffer.peek()
        : channel.peek();
  }

  @Override
  public WaveletOperation receive() {
    if (versionUpdatesBuffer.isEmpty()) {
      return null;
    }
    WaveletOperation maybeOp = versionUpdatesBuffer.remove();
    return maybeOp != null ? maybeOp : channel.receive();
  }

  @Override
  public void send(WaveletOperation... operations) {
    savedStateListener.unsaved(channelId);
    for (WaveletOperation op : operations) {
      channel.send(op);
    }
  }

  @Override
  public void setListener(Listener listener) {
    Preconditions.checkNotNull(listener, "null listener");
    Preconditions.checkState(this.listener == null, "Already connected");
    this.listener = listener;
    channel.connect(startVersion, sessionId);
  }
}
