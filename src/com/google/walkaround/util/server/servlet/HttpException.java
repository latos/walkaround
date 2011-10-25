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

package com.google.walkaround.util.server.servlet;

/**
 * An exception that will be turned into a certain HTTP response code and message.
 *
 * Note that ServerExceptionFilter will log HttpExceptions at level INFO.  When
 * catching a more severe exception, make sure you log it with appropriate
 * severity before rethrowing it as an HttpException.
 *
 * @author ohler@google.com (Christian Ohler)
 */
// TODO(ohler): Determine if it's worthwhile to make this a checked exception.
public abstract class HttpException extends RuntimeException {

  public HttpException() {
  }

  public HttpException(String message) {
    super(message);
  }

  public HttpException(Throwable cause) {
    super(cause);
  }

  public HttpException(String message, Throwable cause) {
    super(message, cause);
  }

  public abstract int getResponseCode();
  /**
   * A response message that does not reveal any information that we want to
   * keep secret.
   */
  public abstract String getPublicMessage();

}
