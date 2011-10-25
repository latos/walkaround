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

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.walkaround.util.server.MonitoringVars;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Collects some basic stats about which endpoints are requested and what
 * responses we serve.  These stats overlap with what the admin console
 * displays, but the admin console's display is not integrated with our
 * monitoring.
 *
 * @author ohler@google.com (Christian Ohler)
 */
@Singleton
public class RequestStatsFilter implements Filter {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(RequestStatsFilter.class.getName());

  private final MonitoringVars monitoring;

  @Inject public RequestStatsFilter(MonitoringVars monitoring) {
    this.monitoring = monitoring;
  }

  @Override public void init(FilterConfig filterConfig) {}

  @Override public void destroy() {}

  // TODO(ohler): Upgrade our Java servlet implementation and eliminate this --
  // HttpServletResponse comes with a getStatus() method in more recent
  // versions.
  private static class ResponseWrapper extends HttpServletResponseWrapper {
    private int status;

    public ResponseWrapper(HttpServletResponse wrapped) {
      super(wrapped);
    }

    @Override public void sendError(int sc) throws IOException {
      status = sc;
      super.sendError(sc);
    }

    @Override public void sendError(int sc, String msg) throws IOException {
      status = sc;
      super.sendError(sc, msg);
    }


    @Override public void setStatus(int sc) {
      status = sc;
      super.setStatus(sc);
    }
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
      throws IOException {
    HttpServletRequest request = (HttpServletRequest) req;
    String methodAndUri = request.getMethod() + "-" + request.getRequestURI();
    monitoring.incrementCounter("servlet-request-" + methodAndUri);
    ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) resp);
    try {
      filterChain.doFilter(req, responseWrapper);
      monitoring.incrementCounter("servlet-response-"
          + methodAndUri + "-" + responseWrapper.status);
    } catch (Throwable t) {
      monitoring.incrementCounter("servlet-uncaught-throwable-" + methodAndUri);
      Throwables.propagateIfPossible(t, IOException.class);
      throw Throwables.propagate(t);
    }
  }

}
