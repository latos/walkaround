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
 * Thrown by server code when it detects that the client does not have permission.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class PermissionDeniedException extends HttpException {
  private static final long serialVersionUID = 372093839240056814L;

  public PermissionDeniedException() {
  }

  public PermissionDeniedException(String publicMessage) {
    super(publicMessage);
  }

  public PermissionDeniedException(Throwable cause) {
    super(cause);
  }

  public PermissionDeniedException(String publicMessage, Throwable cause) {
    super(publicMessage, cause);
  }

  @Override public int getResponseCode() {
    return HttpServletResponse.SC_FORBIDDEN;
  }

  @Override public String getPublicMessage() {
    if (getMessage() == null) {
      return "Permission denied";
    } else {
      return "Permission denied: " + getMessage();
    }
  }

}
