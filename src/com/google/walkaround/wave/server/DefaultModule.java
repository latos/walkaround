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

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.walkaround.util.server.MonitoringVars;
import com.google.walkaround.wave.server.gxp.SourceInstance;
import com.google.walkaround.wave.server.servlet.ServerExceptionFilter.UserTrustStatus;

import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.waveref.WaveRef;
import org.waveprotocol.wave.util.escapers.jvm.JavaWaverefEncoder;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class DefaultModule extends AbstractModule {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(DefaultModule.class.getName());

  private enum SourceInstanceImpl implements SourceInstance {
    GOOGLEWAVE {
      @Override public String getApiUrl() {
        return "https://www-opensocial.googleusercontent.com/api/rpc";
      }
      @Override public String getShortName() { return "googlewave.com"; }
      @Override public String getLongName() { return "googlewave.com"; }
      @Override public String getWaveLink(WaveId waveId) {
        return "https://wave.google.com/wave/waveref/"
            + JavaWaverefEncoder.encodeToUriPathSegment(WaveRef.of(waveId));
      }
    },
    WAVESANDBOX {
      @Override public String getApiUrl() {
        return "https://www-opensocial-sandbox.googleusercontent.com/api/rpc";
      }
      @Override public String getShortName() { return "wavesandbox.com"; }
      @Override public String getLongName() { return "wavesandbox.com developer sandbox"; }
      @Override public String getWaveLink(WaveId waveId) {
        // TODO(ohler): test this
        return "https://www.wavesandbox.com/wave/waveref/"
            + JavaWaverefEncoder.encodeToUriPathSegment(WaveRef.of(waveId));
      }
    };

    @Override public String serialize() {
      return name();
    }
  }


  @Override
  protected void configure() {
    bind(MonitoringVars.class).toInstance(MonitoringVars.NULL_IMPL);
    bind(SourceInstance.Factory.class).toInstance(
        new SourceInstance.Factory() {
          @Override public List<? extends SourceInstance> getInstances() {
            return ImmutableList.copyOf(SourceInstanceImpl.values());
          }
          @Override public SourceInstance parseUnchecked(String s) {
            return SourceInstanceImpl.valueOf(s);
          }
        });
  }

  @Provides
  UserTrustStatus provideUserTrustStatus(SystemProperty.Environment.Value systemEnvironment) {
    if (systemEnvironment == SystemProperty.Environment.Value.Development) {
      // Local SDK.
      return UserTrustStatus.TRUSTED;
    }
    return UserTrustStatus.UNTRUSTED;
  }

}
