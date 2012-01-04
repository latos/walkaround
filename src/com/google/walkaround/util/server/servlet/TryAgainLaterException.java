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

import javax.servlet.http.HttpServletResponse;

/**
 * Thrown by server code when it can't serve the request right now but expects
 * to be able to serve the same request later.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class TryAgainLaterException extends HttpException {
  private static final long serialVersionUID = 423954225618154001L;

  public TryAgainLaterException() {
  }

  public TryAgainLaterException(String publicMessage) {
    super(publicMessage);
  }

  public TryAgainLaterException(Throwable cause) {
    super(cause);
  }

  public TryAgainLaterException(String publicMessage, Throwable cause) {
    super(publicMessage, cause);
  }

  @Override public int getResponseCode() {
    return HttpServletResponse.SC_SERVICE_UNAVAILABLE;
  }

  @Override public String getPublicMessage() {
    if (getMessage() == null) {
      return "Try again later";
    } else {
      return "Try again later: " + getMessage();
    }
  }

}
