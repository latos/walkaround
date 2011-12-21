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

package org.waveprotocol.wave.model.document;

/**
 * Concrete vacuous interfaces for the N,E,T type
 * parameters to Document
 *
 * @see Document
 */
public final class Doc {

  // Do not add methods to these interfaces

  /** Node type */
  public interface N {}

  /** Element type */
  public interface E extends N {}

  /** Text node type */
  public interface T extends N {}

  private Doc() {}
}
