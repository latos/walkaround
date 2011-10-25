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

package com.google.walkaround.util.client.log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.walkaround.util.client.log.Logs.Handler;
import com.google.walkaround.util.client.log.Logs.Level;

/**
 * A log handler that reports fatal logs to the server, and tees off warning
 * logs to its subclass.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class ErrorReportingLogHandler implements Handler {
  private boolean firstReport = true;

  private final String errorReportUrl;

  public ErrorReportingLogHandler(String errorReportUrl) {
    this.errorReportUrl = errorReportUrl;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

  @Override
  public void receiveEntry(
      int num, double timestamp, String stream, Level level, Object[] objects, Throwable t) {
    if (level.compareTo(Level.SEVERE) <= 0) {
      onSevere(stream);
      report(timestamp, stream, level, objects, t);
    }
  }

  /**
   * Notifies a subclass of a severe log event on a stream.
   */
  protected void onSevere(String stream) {
  }

  void report(double timestamp, String streamName, Level level, Object[] objects, Throwable t) {
    // TODO(danilatos): Use a JsoView to construct and then serialize, rather
    // than string builder, unless it's too slow (might even be faster).

    StringBuilder sb = new StringBuilder();
    sb.append("{\"strongName\" : ");
    sb.append(escape(GWT.getPermutationStrongName()));

    sb.append(",\"timestamp\" : ");
    sb.append("" + (long) timestamp);

    sb.append(",\"stream\" : ");
    sb.append(escape(streamName));

    sb.append(",\"level\" : ");
    sb.append(escape(level.name()));

    // Stacktrace info of the first exception found, if any
    if (t != null) {
      sb.append(",\"exception\" : ");
      buildExceptionJson(sb, t);
    }

    sb.append(",\"objects\" : [");
    boolean needsComma = false;
    for (Object o : objects) {
      if (needsComma) {
        sb.append(",");
      } else {
        needsComma = true;
      }
      sb.append(escape(o != null ? o.toString() : "(null)"));
    }

    sb.append("]}");
    String jsonData = sb.toString();

    XMLHttpRequest xhr = XMLHttpRequest.create();
    xhr.open("POST", errorReportUrl + "?firstReport=" + firstReport);
    firstReport = false;
    xhr.send(jsonData);
  }

  public void buildExceptionJson(StringBuilder sb, Throwable t) {
    sb.append("{\"name\" : ");
    sb.append(escape(t.getClass().getName()));

    sb.append(",\"message\" : ");
    sb.append(escape(t.getMessage()));

    sb.append(",\"stackTrace\" : [");
    boolean needsComma = false;
    for (StackTraceElement e : t.getStackTrace()) {
      if (needsComma) {
        sb.append(",");
      } else {
        needsComma = true;
      }

      sb.append(escape(e.getMethodName()));
    }
    sb.append("]");

    if (t.getCause() != null) {
      sb.append(",\"cause\" : ");
      buildExceptionJson(sb, t.getCause());
    }

    sb.append("}");
  }

  String escape(String unescaped) {
    // + "" to avoid null
    return JsonUtils.escapeValue(unescaped != null ? unescaped : "(null)");
  }
}
