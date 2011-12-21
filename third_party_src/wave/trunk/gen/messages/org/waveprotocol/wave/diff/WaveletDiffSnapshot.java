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

import org.waveprotocol.wave.diff.DocumentDiffSnapshot;
import org.waveprotocol.wave.federation.ProtocolHashedVersion;
import org.waveprotocol.wave.communication.Blob;
import org.waveprotocol.wave.communication.ProtoEnums;
import java.util.List;

/**
 * Model interface for WaveletDiffSnapshot.
 *
 * Generated from org/waveprotocol/wave/diff/diff.proto. Do not edit.
 */
public interface WaveletDiffSnapshot {

  /** Does a deep copy from model. */
  void copyFrom(WaveletDiffSnapshot model);

  /**
   * Tests if this model is equal to another object.
   * "Equal" is recursively defined as:
   * <ul>
   * <li>both objects implement this interface,</li>
   * <li>all corresponding primitive fields of both objects have the same value, and</li>
   * <li>all corresponding nested-model fields of both objects are "equal".</li>
   * </ul>
   *
   * This is a coarser equivalence than provided by the equals() methods. Two
   * objects may not be equal() to each other, but may be isEqualTo() each other.
   */
  boolean isEqualTo(Object o);

  /** Returns waveletId, or null if hasn't been set. */
  String getWaveletId();

  /** Sets waveletId. */
  void setWaveletId(String waveletId);

  /** Returns participant, or null if hasn't been set. */
  List<String> getParticipant();

  /** Adds an element to participant. */
  void addParticipant(String value);

  /** Adds a list of elements to participant. */
  void addAllParticipant(List<String> participant);

  /** Returns the nth element of participant. */
  String getParticipant(int n);

  /** Sets the nth element of participant. */
  void setParticipant(int n, String value);

  /** Returns the length of participant. */
  int getParticipantSize();

  /** Clears participant. */
  void clearParticipant();

  /** Returns addedParticipant, or null if hasn't been set. */
  List<String> getAddedParticipant();

  /** Adds an element to addedParticipant. */
  void addAddedParticipant(String value);

  /** Adds a list of elements to addedParticipant. */
  void addAllAddedParticipant(List<String> addedParticipant);

  /** Returns the nth element of addedParticipant. */
  String getAddedParticipant(int n);

  /** Sets the nth element of addedParticipant. */
  void setAddedParticipant(int n, String value);

  /** Returns the length of addedParticipant. */
  int getAddedParticipantSize();

  /** Clears addedParticipant. */
  void clearAddedParticipant();

  /** Returns removedParticipant, or null if hasn't been set. */
  List<String> getRemovedParticipant();

  /** Adds an element to removedParticipant. */
  void addRemovedParticipant(String value);

  /** Adds a list of elements to removedParticipant. */
  void addAllRemovedParticipant(List<String> removedParticipant);

  /** Returns the nth element of removedParticipant. */
  String getRemovedParticipant(int n);

  /** Sets the nth element of removedParticipant. */
  void setRemovedParticipant(int n, String value);

  /** Returns the length of removedParticipant. */
  int getRemovedParticipantSize();

  /** Clears removedParticipant. */
  void clearRemovedParticipant();

  /** Returns document, or null if hasn't been set. */
  List<? extends DocumentDiffSnapshot> getDocument();

  /** Adds an element to document. */
  void addDocument(DocumentDiffSnapshot value);

  /** Adds a list of elements to document. */
  void addAllDocument(List<? extends DocumentDiffSnapshot> document);

  /** Returns the nth element of document. */
  DocumentDiffSnapshot getDocument(int n);

  /** Sets the nth element of document. */
  void setDocument(int n, DocumentDiffSnapshot value);

  /** Returns the length of document. */
  int getDocumentSize();

  /** Clears document. */
  void clearDocument();

  /** Returns version, or null if hasn't been set. */
  ProtocolHashedVersion getVersion();

  /** Sets version. */
  void setVersion(ProtocolHashedVersion version);

  /** Returns lastModifiedTime, or null if hasn't been set. */
  double getLastModifiedTime();

  /** Sets lastModifiedTime. */
  void setLastModifiedTime(double lastModifiedTime);

  /** Returns creator, or null if hasn't been set. */
  String getCreator();

  /** Sets creator. */
  void setCreator(String creator);

  /** Returns creationTime, or null if hasn't been set. */
  double getCreationTime();

  /** Sets creationTime. */
  void setCreationTime(double creationTime);
}