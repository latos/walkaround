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

import com.google.common.base.Objects;

import org.waveprotocol.wave.model.id.WaveletName;

import javax.annotation.Nullable;

/**
 * Information {@link ImportOverviewFragment} needs about a wavelet.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public final class ImportWaveletDisplayRecord {

  private final SourceInstance instance;
  private final WaveletName waveletName;
  private final String linkToOriginal;
  private final String creator;
  private final String title;
  private final String lastModifiedDate;
  private final boolean privateImportInProgress;
  @Nullable private final String privateImportedLink;
  private final boolean sharedImportInProgress;
  @Nullable private final String sharedImportedLink;

  public ImportWaveletDisplayRecord(SourceInstance instance,
      WaveletName waveletName,
      String linkToOriginal,
      String creator,
      String title,
      String lastModifiedDate,
      boolean privateImportInProgress,
      @Nullable String privateImportedLink,
      boolean sharedImportInProgress,
      @Nullable String sharedImportedLink) {
    this.instance = checkNotNull(instance, "Null instance");
    this.waveletName = checkNotNull(waveletName, "Null waveletName");
    this.linkToOriginal = checkNotNull(linkToOriginal, "Null linkToOriginal");
    this.creator = checkNotNull(creator, "Null creator");
    this.title = checkNotNull(title, "Null title");
    this.lastModifiedDate = checkNotNull(lastModifiedDate, "Null lastModifiedDate");
    this.privateImportInProgress = privateImportInProgress;
    this.privateImportedLink = privateImportedLink;
    this.sharedImportInProgress = sharedImportInProgress;
    this.sharedImportedLink = sharedImportedLink;
  }

  public SourceInstance getInstance() {
    return instance;
  }

  public WaveletName getWaveletName() {
    return waveletName;
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

  public boolean isPrivateImportInProgress() {
    return privateImportInProgress;
  }

  @Nullable public String getPrivateImportedLink() {
    return privateImportedLink;
  }

  public boolean isSharedImportInProgress() {
    return sharedImportInProgress;
  }

  @Nullable public String getSharedImportedLink() {
    return sharedImportedLink;
  }

  @Override public String toString() {
    return getClass().getSimpleName() + "("
        + instance + ", "
        + waveletName + ", "
        + linkToOriginal + ", "
        + creator + ", "
        + title + ", "
        + lastModifiedDate + ", "
        + privateImportInProgress + ", "
        + privateImportedLink + ", "
        + sharedImportInProgress + ", "
        + sharedImportedLink
        + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof ImportWaveletDisplayRecord)) { return false; }
    ImportWaveletDisplayRecord other = (ImportWaveletDisplayRecord) o;
    return privateImportInProgress == other.privateImportInProgress
        && sharedImportInProgress == other.sharedImportInProgress
        && Objects.equal(instance, other.instance)
        && Objects.equal(waveletName, other.waveletName)
        && Objects.equal(linkToOriginal, other.linkToOriginal)
        && Objects.equal(creator, other.creator)
        && Objects.equal(title, other.title)
        && Objects.equal(lastModifiedDate, other.lastModifiedDate)
        && Objects.equal(privateImportedLink, other.privateImportedLink)
        && Objects.equal(sharedImportedLink, other.sharedImportedLink);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(instance, waveletName, linkToOriginal, creator, title, lastModifiedDate,
        privateImportInProgress, privateImportedLink, sharedImportInProgress, sharedImportedLink);
  }

}
