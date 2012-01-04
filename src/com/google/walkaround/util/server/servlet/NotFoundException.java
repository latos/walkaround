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
 * Thrown by server code when a resource does not exist (or if the user does not
 * have access but we do not wish to reveal this information).
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class NotFoundException extends HttpException {
  private static final long serialVersionUID = 272829532791826633L;

  private static final String NOT_FOUND_MESSAGE = "We're sorry, this page either does not exist "
      + "or you do not have access to it.";

  public static NotFoundException withInternalMessage(String message) {
    return new NotFoundException(new Exception(message));
  }

  public NotFoundException() {
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }

  @Override public int getResponseCode() {
    return HttpServletResponse.SC_NOT_FOUND;
  }

  @Override public String getPublicMessage() {
    return NOT_FOUND_MESSAGE;
  }

}
