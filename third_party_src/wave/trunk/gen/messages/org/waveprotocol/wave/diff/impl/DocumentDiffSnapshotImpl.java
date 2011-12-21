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

package org.waveprotocol.wave.diff.impl;

import org.waveprotocol.wave.federation.ProtocolDocumentOperation;
import org.waveprotocol.wave.federation.impl.ProtocolDocumentOperationImpl;
import org.waveprotocol.wave.diff.DocumentDiffSnapshot;
import org.waveprotocol.wave.diff.DocumentDiffSnapshotUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of DocumentDiffSnapshot.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
public class DocumentDiffSnapshotImpl implements DocumentDiffSnapshot {
  private String documentId;
  private ProtocolDocumentOperationImpl state;
  private ProtocolDocumentOperationImpl diff;
  private String author;
  private final List<String> contributor = new ArrayList<String>();
  private final List<String> addedContributor = new ArrayList<String>();
  private final List<String> removedContributor = new ArrayList<String>();
  private Double lastModifiedVersion;
  private Double lastModifiedTime;
  public DocumentDiffSnapshotImpl() {
  }

  public DocumentDiffSnapshotImpl(DocumentDiffSnapshot message) {
    copyFrom(message);
  }

  @Override
  public void copyFrom(DocumentDiffSnapshot message) {
    setDocumentId(message.getDocumentId());
    if (message.hasState()) {
      setState(message.getState());
    } else {
      clearState();
    }
    if (message.hasDiff()) {
      setDiff(message.getDiff());
    } else {
      clearDiff();
    }
    setAuthor(message.getAuthor());
    clearContributor();
    for (String field : message.getContributor()) {
      addContributor(field);
    }
    clearAddedContributor();
    for (String field : message.getAddedContributor()) {
      addAddedContributor(field);
    }
    clearRemovedContributor();
    for (String field : message.getRemovedContributor()) {
      addRemovedContributor(field);
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
  public boolean hasState() {
    return state != null;
  }

  @Override
  public void clearState() {
    state = null;
  }

  @Override
  public ProtocolDocumentOperationImpl getState() {
    return new ProtocolDocumentOperationImpl(state);
  }

  @Override
  public void setState(ProtocolDocumentOperation message) {
    this.state = new ProtocolDocumentOperationImpl(message);
  }

  @Override
  public boolean hasDiff() {
    return diff != null;
  }

  @Override
  public void clearDiff() {
    diff = null;
  }

  @Override
  public ProtocolDocumentOperationImpl getDiff() {
    return new ProtocolDocumentOperationImpl(diff);
  }

  @Override
  public void setDiff(ProtocolDocumentOperation message) {
    this.diff = new ProtocolDocumentOperationImpl(message);
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
  public List<String> getAddedContributor() {
    return Collections.unmodifiableList(addedContributor);
  }

  @Override
  public void addAllAddedContributor(List<String> values) {
    this.addedContributor.addAll(values);
  }

  @Override
  public String getAddedContributor(int n) {
    return addedContributor.get(n);
  }

  @Override
  public void setAddedContributor(int n, String value) {
    this.addedContributor.set(n, value);
  }

  @Override
  public int getAddedContributorSize() {
    return addedContributor.size();
  }

  @Override
  public void addAddedContributor(String value) {
    this.addedContributor.add(value);
  }

  @Override
  public void clearAddedContributor() {
    addedContributor.clear();
  }

  @Override
  public List<String> getRemovedContributor() {
    return Collections.unmodifiableList(removedContributor);
  }

  @Override
  public void addAllRemovedContributor(List<String> values) {
    this.removedContributor.addAll(values);
  }

  @Override
  public String getRemovedContributor(int n) {
    return removedContributor.get(n);
  }

  @Override
  public void setRemovedContributor(int n, String value) {
    this.removedContributor.set(n, value);
  }

  @Override
  public int getRemovedContributorSize() {
    return removedContributor.size();
  }

  @Override
  public void addRemovedContributor(String value) {
    this.removedContributor.add(value);
  }

  @Override
  public void clearRemovedContributor() {
    removedContributor.clear();
  }

  @Override
  public double getLastModifiedVersion() {
    return lastModifiedVersion;
  }

  @Override
  public void setLastModifiedVersion(double value) {
    this.lastModifiedVersion = value;
  }

  @Override
  public double getLastModifiedTime() {
    return lastModifiedTime;
  }

  @Override
  public void setLastModifiedTime(double value) {
    this.lastModifiedTime = value;
  }

  /** Provided to subclasses to clear all fields, for example when deserializing. */
  protected void reset() {
    this.documentId = null;
    this.state = null;
    this.diff = null;
    this.author = null;
    this.contributor.clear();
    this.addedContributor.clear();
    this.removedContributor.clear();
    this.lastModifiedVersion = null;
    this.lastModifiedTime = null;
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof DocumentDiffSnapshotImpl) && isEqualTo(o);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof DocumentDiffSnapshot) {
      return DocumentDiffSnapshotUtil.isEqual(this, (DocumentDiffSnapshot) o);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return DocumentDiffSnapshotUtil.getHashCode(this);
  }

}