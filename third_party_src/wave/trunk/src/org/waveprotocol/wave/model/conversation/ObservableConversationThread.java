/**
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.waveprotocol.wave.model.conversation;

import org.waveprotocol.wave.model.document.operation.DocInitialization;

/**
 * Extends {@link ConversationThread} to provide events, sourced from the
 * {@link #getConversation() conversation} object.
 *
 * @author anorth@google.com (Alex North)
 */
public interface ObservableConversationThread extends ConversationThread {

  // Covariant specialisations.

  @Override
  ObservableConversation getConversation();

  @Override
  Iterable<? extends ObservableConversationBlip> getBlips();

  @Override
  ObservableConversationBlip getParentBlip();

  @Override
  ObservableConversationBlip getFirstBlip();

  @Override
  ObservableConversationBlip appendBlip();

  @Override
  ObservableConversationBlip appendBlip(DocInitialization content);

  @Override
  ObservableConversationBlip insertBlip(ConversationBlip successor);
}
