/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.waveprotocol.wave.diff;

import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder;
import org.waveprotocol.wave.diff.DocumentDiffSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for DocumentDiffSnapshots.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
public final class DocumentDiffSnapshotBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    DocumentDiffSnapshot create();
  }

  private String documentId;
  private ProtocolDocumentOperation state;
  private ProtocolDocumentOperation diff;
  private String author;
  private final List<String> contributor = new ArrayList<String>();
  private final List<String> addedContributor = new ArrayList<String>();
  private final List<String> removedContributor = new ArrayList<String>();
  private Double lastModifiedVersion;
  private Double lastModifiedTime;
  public DocumentDiffSnapshotBuilder() {
  }

  public DocumentDiffSnapshotBuilder setDocumentId(String value) {
    this.documentId = value;
    return this;
  }

  public DocumentDiffSnapshotBuilder clearState() {
    state = null;
    return this;
  }

  public DocumentDiffSnapshotBuilder setState(ProtocolDocumentOperation message) {
    this.state = message;
    return this;
  }

  public DocumentDiffSnapshotBuilder clearDiff() {
    diff = null;
    return this;
  }

  public DocumentDiffSnapshotBuilder setDiff(ProtocolDocumentOperation message) {
    this.diff = message;
    return this;
  }

  public DocumentDiffSnapshotBuilder setAuthor(String value) {
    this.author = value;
    return this;
  }

  public DocumentDiffSnapshotBuilder addAllContributor(List<String> values) {
    this.contributor.addAll(values);
    return this;
  }

  public DocumentDiffSnapshotBuilder setContributor(int n, String value) {
    this.contributor.set(n, value);
    return this;
  }

  public DocumentDiffSnapshotBuilder addContributor(String value) {
    this.contributor.add(value);
    return this;
  }

  public DocumentDiffSnapshotBuilder clearContributor() {
    contributor.clear();
    return this;
  }

  public DocumentDiffSnapshotBuilder addAllAddedContributor(List<String> values) {
    this.addedContributor.addAll(values);
    return this;
  }

  public DocumentDiffSnapshotBuilder setAddedContributor(int n, String value) {
    this.addedContributor.set(n, value);
    return this;
  }

  public DocumentDiffSnapshotBuilder addAddedContributor(String value) {
    this.addedContributor.add(value);
    return this;
  }

  public DocumentDiffSnapshotBuilder clearAddedContributor() {
    addedContributor.clear();
    return this;
  }

  public DocumentDiffSnapshotBuilder addAllRemovedContributor(List<String> values) {
    this.removedContributor.addAll(values);
    return this;
  }

  public DocumentDiffSnapshotBuilder setRemovedContributor(int n, String value) {
    this.removedContributor.set(n, value);
    return this;
  }

  public DocumentDiffSnapshotBuilder addRemovedContributor(String value) {
    this.removedContributor.add(value);
    return this;
  }

  public DocumentDiffSnapshotBuilder clearRemovedContributor() {
    removedContributor.clear();
    return this;
  }

  public DocumentDiffSnapshotBuilder setLastModifiedVersion(double value) {
    this.lastModifiedVersion = value;
    return this;
  }

  public DocumentDiffSnapshotBuilder setLastModifiedTime(double value) {
    this.lastModifiedTime = value;
    return this;
  }

  /** Builds a {@link DocumentDiffSnapshot} using this builder and a factory. */
  public DocumentDiffSnapshot build(Factory factory) {
    DocumentDiffSnapshot message = factory.create();
    message.setDocumentId(documentId);
    message.setState(state);
    message.setDiff(diff);
    message.setAuthor(author);
    message.clearContributor();
    message.addAllContributor(contributor);
    message.clearAddedContributor();
    message.addAllAddedContributor(addedContributor);
    message.clearRemovedContributor();
    message.addAllRemovedContributor(removedContributor);
    message.setLastModifiedVersion(lastModifiedVersion);
    message.setLastModifiedTime(lastModifiedTime);
    return message;
  }

}