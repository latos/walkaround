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

package com.google.walkaround.wave.server.gxp;

import org.waveprotocol.wave.model.id.WaveId;

import java.util.List;

/**
 * A wave instance to import from.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public interface SourceInstance {

  interface Factory {
    List<? extends SourceInstance> getInstances();
    /**
     * @throws RuntimeException (or a subtype) if {@code serialized} is not a
     * serialized {@code SourceInstance}.
     */
    SourceInstance parseUnchecked(String serialized);
  }

  String serialize();

  /** URL for Active Robot API calls. */
  String getApiUrl();
  /** Short name for display. */
  String getShortName();
  /** Long name for display. */
  String getLongName();
  /** Returns a link to a wave on this instance. */
  String getWaveLink(WaveId waveId);
  /**
   * Returns the URL of an attachment on this instance given the attachment path from the attachment
   * data document.
   */
  String getFullAttachmentUrl(String attachmentPath);

}
