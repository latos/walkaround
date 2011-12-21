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

package org.waveprotocol.box.common.comms;

import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.ProtocolDocumentOperationBuilder;
import org.waveprotocol.box.common.comms.DocumentSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for DocumentSnapshots.
 *
 * Generated from org/waveprotocol/box/common/comms/waveclient-rpc.proto. Do not edit.
 */
public final class DocumentSnapshotBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    DocumentSnapshot create();
  }

  private String documentId;
  private ProtocolDocumentOperation documentOperation;
  private String author;
  private final List<String> contributor = new ArrayList<String>();
  private Long lastModifiedVersion;
  private Long lastModifiedTime;
  public DocumentSnapshotBuilder() {
  }

  public DocumentSnapshotBuilder setDocumentId(String value) {
    this.documentId = value;
    return this;
  }

  public DocumentSnapshotBuilder setDocumentOperation(ProtocolDocumentOperation message) {
    this.documentOperation = message;
    return this;
  }

  public DocumentSnapshotBuilder setAuthor(String value) {
    this.author = value;
    return this;
  }

  public DocumentSnapshotBuilder addAllContributor(List<String> values) {
    this.contributor.addAll(values);
    return this;
  }

  public DocumentSnapshotBuilder setContributor(int n, String value) {
    this.contributor.set(n, value);
    return this;
  }

  public DocumentSnapshotBuilder addContributor(String value) {
    this.contributor.add(value);
    return this;
  }

  public DocumentSnapshotBuilder clearContributor() {
    contributor.clear();
    return this;
  }

  public DocumentSnapshotBuilder setLastModifiedVersion(long value) {
    this.lastModifiedVersion = value;
    return this;
  }

  public DocumentSnapshotBuilder setLastModifiedTime(long value) {
    this.lastModifiedTime = value;
    return this;
  }

  /** Builds a {@link DocumentSnapshot} using this builder and a factory. */
  public DocumentSnapshot build(Factory factory) {
    DocumentSnapshot message = factory.create();
    message.setDocumentId(documentId);
    message.setDocumentOperation(documentOperation);
    message.setAuthor(author);
    message.clearContributor();
    message.addAllContributor(contributor);
    message.setLastModifiedVersion(lastModifiedVersion);
    message.setLastModifiedTime(lastModifiedTime);
    return message;
  }

}