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

package com.google.walkaround.util.server.auth;

/**
 * Thrown when a security token is invalid.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class InvalidSecurityTokenException extends Exception {

  public InvalidSecurityTokenException() {
  }

  public InvalidSecurityTokenException(String message) {
    super(message);
  }

  public InvalidSecurityTokenException(Throwable cause) {
    super(cause);
  }

  public InvalidSecurityTokenException(String message, Throwable cause) {
    super(message, cause);
  }

}
