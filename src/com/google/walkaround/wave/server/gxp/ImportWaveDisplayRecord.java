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

import static com.google.common.base.Preconditions.checkNotNull;

import org.waveprotocol.wave.model.id.WaveId;

import javax.annotation.Nullable;

/**
 * Information {@link ImportOverviewFragment} needs about a wave.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public final class ImportWaveDisplayRecord {

  private final SourceInstance instance;
  private final WaveId waveId;
  private final String linkToOriginal;
  private final String creator;
  private final String title;
  private final String lastModifiedDate;
  private final String status;
  private final boolean importable;
  @Nullable private final String linkToImported;

  public ImportWaveDisplayRecord(SourceInstance instance,
      WaveId waveId,
      String linkToOriginal,
      String creator,
      String title,
      String lastModifiedDate,
      String status,
      boolean importable,
      @Nullable String linkToImported) {
    this.instance = checkNotNull(instance, "Null instance");
    this.waveId = checkNotNull(waveId, "Null waveId");
    this.linkToOriginal = checkNotNull(linkToOriginal, "Null linkToOriginal");
    this.creator = checkNotNull(creator, "Null creator");
    this.title = checkNotNull(title, "Null title");
    this.lastModifiedDate = checkNotNull(lastModifiedDate, "Null lastModifiedDate");
    this.status = checkNotNull(status, "Null status");
    this.importable = importable;
    this.linkToImported = linkToImported;
  }

  public SourceInstance getInstance() {
    return instance;
  }

  public WaveId getWaveId() {
    return waveId;
  }

  public String getLinkToOriginal() {
    return linkToOriginal;
  }

  public String getCreator() {
    return creator;
  }

  public String getTitle() {
    return title;
  }

  public String getLastModifiedDate() {
    return lastModifiedDate;
  }

  public String getStatus() {
    return status;
  }

  public boolean isImportable() {
    return importable;
  }

  @Nullable public String getLinkToImported() {
    return linkToImported;
  }

  @Override public String toString() {
    return "ImportWaveDisplayRecord("
        + instance + ", "
        + waveId + ", "
        + linkToOriginal + ", "
        + creator + ", "
        + title + ", "
        + lastModifiedDate + ", "
        + status + ", "
        + importable + ", "
        + linkToImported
        + ")";
  }

}
