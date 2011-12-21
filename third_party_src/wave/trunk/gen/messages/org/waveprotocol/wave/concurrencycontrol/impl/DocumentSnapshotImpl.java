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

package org.waveprotocol.wave.concurrencycontrol.impl;

import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.impl.ProtocolDocumentOperationImpl;
import org.waveprotocol.wave.concurrencycontrol.DocumentSnapshot;
import org.waveprotocol.wave.concurrencycontrol.DocumentSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of DocumentSnapshot.
 *
 * Generated from org/waveprotocol/wave/concurrencycontrol/clientserver.proto. Do not edit.
 */
public class DocumentSnapshotImpl implements DocumentSnapshot {
  private String documentId;
  private ProtocolDocumentOperationImpl documentOperation;
  private String author;
  private final List<String> contributor = new ArrayList<String>();
  private Long lastModifiedVersion;
  private Long lastModifiedTime;
  public DocumentSnapshotImpl() {
  }

  public DocumentSnapshotImpl(DocumentSnapshot message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(DocumentSnapshot message) {
    setDocumentId(message.getDocumentId());
    setDocumentOperation(message.getDocumentOperation());
    setAuthor(message.getAuthor());
    clearContributor();
    for (String field : message.getContributor()) {
      addContributor(field);
    }
    setLastModifiedVersion(message.getLastModifiedVersion());
    setLastModifiedTime(message.getLastModifiedTime());
  }

  @Override
  public String getDocumentId() {
    return documentId;
  }

  @Override
  public void setDocumentId(String value) {
    this.documentId = value;
  }

  @Override
  public ProtocolDocumentOperationImpl getDocumentOperation() {
    return new ProtocolDocumentOperationImpl(documentOperation);
  }

  @Override
  public void setDocumentOperation(ProtocolDocumentOperation message) {
    this.documentOperation = new ProtocolDocumentOperationImpl(message);
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public void setAuthor(String value) {
    this.author = value;
  }

  @Override
  public List<String> getContributor() {
    return Collections.unmodifiableList(contributor);
  }

  @Override
  public void addAllContributor(List<String> values) {
    this.contributor.addAll(values);
  }

  @Override
  public String getContributor(int n) {
    return contributor.get(n);
  }

  @Override
  public void setContributor(int n, String value) {
    this.contributor.set(n, value);
  }

  @Override
  public int getContributorSize() {
    return contributor.size();
  }

  @Override
  public void addContributor(String value) {
    this.contributor.add(value);
  }

  @Override
  public void clearContributor() {
    contributor.clear();
  }

  @Override
  public long getLastModifiedVersion() {
    return lastModifiedVersion;
  }

  @Override
  public void setLastModifiedVersion(long value) {
    this.lastModifiedVersion = value;
  }

  @Override
  public long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @Override
  public void setLastModifiedTime(long value) {
    this.lastModifiedTime = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.documentId = null;
    this.documentOperation = null;
    this.author = null;
    this.contributor.clear();
    this.lastModifiedVersion = null;
    this.lastModifiedTime = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof DocumentSnapshotImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof DocumentSnapshot) {
      return DocumentSnapshotUtil.isEqual(this, (DocumentSnapshot) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return DocumentSnapshotUtil.getHashCode(this);
  }

}