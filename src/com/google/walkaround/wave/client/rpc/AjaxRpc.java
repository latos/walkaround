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

package com.google.walkaround.wave.client.rpc;

import com.google.gwt.core.client.Duration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;

import org.waveprotocol.wave.client.common.util.UserAgent;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.IntMap;
import org.waveprotocol.wave.model.util.ReadableStringMap;
import org.waveprotocol.wave.model.util.StringMap;

/**
 * Ajax HTTP-Request based implementation of the Rpc interface.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class AjaxRpc implements Rpc {
  private enum Result {
    OK,
    PERMANENT_FAILURE,
    RETRYABLE_FAILURE
  }

  private final class Handle implements RpcHandle {
    private final int id;

    Handle(int id) {
      this.id = id;
    }

    public int getId() {
      return id;
    }

    @Override public boolean equals(Object other) {
      if (other instanceof Handle) {
        return id == ((Handle) other).id;
      }
      return false;
    }

    @Override public int hashCode() {
      return id * 43;
    }

    @Override
    public void drop() {
      dropRequest(this);
    }

    @Override
    public boolean isPending() {
      return handles.containsKey(id);
    }
  }

  static void dropRequest(Handle handle) {
    handles.remove(handle.getId());
  }

  private static final int MAX_CONSECUTIVE_FAILURES = 20;

  /** Root prefix to prepend to service urls */
  private final String rpcRoot;
  private final ConnectionStateListener listener;

  private ConnectionState connectionState = ConnectionState.CONNECTED;
  private int consecutiveFailures = 0;

  /** Log stream to use */
  private static final Log log = Logs.create("rpc");

  /** Incrementing id counter */
  private static int nextRequestId;

  private static IntMap<Handle> handles = CollectionUtils.createIntMap();

  /**
   * Drops all pending AJAX requests
   */
  public static void dropAll() {
    handles.clear();
  }

  /**
   * @param rpcRoot root prefix to prepend to service urls
   */
  public AjaxRpc(final String rpcRoot, ConnectionStateListener listener) {
    this.rpcRoot = rpcRoot;
    this.listener = listener;
  }

  @Override
  public RpcHandle makeRequest(Method method, String serviceName, ReadableStringMap<String> params,
      final Rpc.RpcCallback rpcCallback) {

    final int requestId = nextRequestId;
    nextRequestId++;

    // See the javadoc for HARD_RELOAD.
    if (connectionState == ConnectionState.HARD_RELOAD) {
      return new Handle(requestId);
    }

    final String requestData;
    final RequestBuilder.Method httpMethod;

    StringBuilder urlBuilder = new StringBuilder(rpcRoot + "/" + serviceName + "?");
    // NOTE(danilatos): For some reason, IE6 seems not to perform some requests
    // it's already made, resulting in... no data. Inserting some
    // unique value into the request seems to fix that.
    if (UserAgent.isIE()) {
      urlBuilder.append("_no_cache=" + requestId + "" + Duration.currentTimeMillis() + "&");
    }

    if (method == Method.GET) {
      httpMethod = RequestBuilder.GET;
      addParams(urlBuilder, params);
      requestData = "";
    } else {
      httpMethod = RequestBuilder.POST;
      requestData = addParams(new StringBuilder(), params).toString();
    }

    final String url = urlBuilder.toString();

    RequestBuilder r = new RequestBuilder(httpMethod, url);
    if (method == Method.POST) {
      r.setHeader("Content-Type", "application/x-www-form-urlencoded");
      r.setHeader("X-Same-Domain", "true");
    }

    log.log(Level.INFO, "RPC Request, id=", requestId, " method=", httpMethod,
        " urlSize=", url.length(), " bodySize=", requestData.length());

    class RpcRequestCallback implements RequestCallback {
      @Override public void onResponseReceived(Request request, Response response) {
        RpcHandle handle = handles.get(requestId);
        if (handle == null) {
          // It's been dropped
          log.log(Level.INFO, "RPC SuccessDrop, id=", requestId);
          return;
        }

        // Clear it now, before callbacks
        removeHandle();

        int statusCode = response.getStatusCode();
        String data = response.getText();

        Result result;
        if (statusCode < 100) {
          result = Result.RETRYABLE_FAILURE;
          maybeSetConnectionState(ConnectionState.OFFLINE);
        } else if (statusCode == 200) {
          result = Result.OK;
          maybeSetConnectionState(ConnectionState.CONNECTED);
          consecutiveFailures = 0;
        } else if (statusCode >= 500) {
          result = Result.RETRYABLE_FAILURE;
          consecutiveFailures++;
          if (consecutiveFailures > MAX_CONSECUTIVE_FAILURES) {
            maybeSetConnectionState(ConnectionState.OFFLINE);
          } else {
            maybeSetConnectionState(ConnectionState.CONNECTED);
          }
        } else {
          result = Result.PERMANENT_FAILURE;
          maybeSetConnectionState(ConnectionState.SOFT_RELOAD);
        }

        switch (result) {
          case OK:
            log.log(Level.INFO, "RPC Success, id=", requestId);
            try {
              rpcCallback.onSuccess(data);
            } catch (MessageException e) {
              // Semi-HACK(danilatos): Treat parse errors as login problems
              // due to loading a login or authorization page. (It's unlikely
              // we'd otherwise get a parse error from a 200 OK result).
              // The simpler solution of detecting redirects is not possible
              // with XmlHttpRequest, the web is unfortunately broken.

              // TODO(danilatos) Possible alternatives:
              // either change our server side to not require
              // login through web.xml but to check if UserService says currentUser==null (or
              // whatever it does if not logged in) and return a well-defined "not logged in"
              // response instead, or to prefix all responses from the server with a fixed string
              // (like we do with "OK" elsewhere) and assume not logged in if that prefix is
              // missing.  We could strip off that prefix here and make it transparent to the
              // callbacks.

              maybeSetConnectionState(ConnectionState.LOGGED_OUT);

              error(new Exception("RPC failed due to message exception, treating as auth failure"
                  + ", status code: " + statusCode + ", data: " + data));
            }
            break;
          case RETRYABLE_FAILURE:
            error(new Exception("RPC failed, status code: " + statusCode + ", data: " + data));
            break;
          case PERMANENT_FAILURE:
            fatal(new Exception("RPC bad request, status code: " + statusCode + ", data: " + data));
            break;
          default:
            throw new AssertionError("Unknown result " + result);
        }
      }

      @Override public void onError(Request request, Throwable exception) {
        if (!handles.containsKey(requestId)) {
          log.log(Level.INFO, "RPC FailureDrop, id=", requestId, exception.getMessage());
          return;
        }
        removeHandle();
        error(exception);
      }

      private void fatal(Throwable e) {
        log.log(Level.WARNING, "RPC Bad Request, id=", requestId, e.getMessage(),
            "Request url:" + url, e);
        rpcCallback.onFatalError(e);
      }

      private void error(Throwable e) {
        log.log(Level.WARNING, "RPC Failure, id=", requestId, e.getMessage(),
            "Request url:" + url, e);
        rpcCallback.onConnectionError(e);
      }

      private void removeHandle() {
        handles.remove(requestId);
      }
    }

    RpcRequestCallback innerCallback = new RpcRequestCallback();

    try {
      Request gwtReq = r.sendRequest(requestData, innerCallback);
      Handle handle = new Handle(requestId);
      handles.put(handle.getId(), handle);
      return handle;
    } catch (RequestException e) {
      // TODO(danilatos) Decide if this should be a badRequest.
      innerCallback.error(e);
      return null;
    }
  }


  /**
   * {@inheritDoc}
   *
   * If a change occurs, notifies the listener.
   */
  @Override
  public void maybeSetConnectionState(ConnectionState newState) {
    if (newState != connectionState && connectionState.canTransitionTo(newState)) {
      connectionState = newState;
      listener.connectionStateChanged(connectionState);
    }
  }

  private StringBuilder addParams(final StringBuilder b, ReadableStringMap<String> params) {
    params.each(new StringMap.ProcV<String>() {
      @Override public void apply(String key, String value) {
        if (value != null) {
          b.append(
              URL.encodeQueryString(key) + "=" +
              URL.encodeQueryString(value) + "&");
        }
      }
    });
    return b;
  }
}
