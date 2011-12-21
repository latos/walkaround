/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.waveprotocol.box.server.rpc.jso;

import static org.waveprotocol.wave.communication.gwt.JsonHelper.*;
import com.google.gwt.core.client.*;
import org.waveprotocol.box.server.rpc.RpcFinished;
import org.waveprotocol.box.server.rpc.RpcFinishedUtil;
import org.waveprotocol.wave.communication.Blob;
import org.waveprotocol.wave.communication.ProtoEnums;
import org.waveprotocol.wave.communication.gwt.*;
import org.waveprotocol.wave.communication.json.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Client implementation of RpcFinished backed by a GWT JavaScriptObject.
 *
 * Generated from org/waveprotocol/box/server/rpc/rpc.proto. Do not edit.
 */
// We have to use fully-qualified name of the GsonSerializable class here in order to make it
// visible in case of nested classes.
public final class RpcFinishedJsoImpl extends org.waveprotocol.wave.communication.gwt.JsonMessage
    implements RpcFinished {
  private static final String keyFailed = "1";
  private static final String keyErrorText = "2";
  protected RpcFinishedJsoImpl() {
  }

  public static RpcFinishedJsoImpl create() {
    RpcFinishedJsoImpl instance = (RpcFinishedJsoImpl) JsonMessage.createJsonMessage();
    // Force all lists to start with an empty list rather than no property for
    // the list. This is so that the native JS equality works, since (obviously)
    // {} != {"foo": []} while in the context of messages they should be.
    return instance;
  }

  @Override
  public void copyFrom(RpcFinished message) {
    super.copyFrom((RpcFinishedJsoImpl) message);
  }

  @Override
  public boolean getFailed() {
    return hasProperty(this, keyFailed) ? getPropertyAsBoolean(this, keyFailed) : false;
  }

  @Override
  public void setFailed(boolean value) {
    setPropertyAsBoolean(this, keyFailed, value);
  }

  @Override
  public boolean hasErrorText() {
    return hasProperty(this, keyErrorText);
  }

  @Override
  public void clearErrorText() {
    if (hasProperty(this, keyErrorText)) {
      deleteProperty(this, keyErrorText);
    }
  }

  @Override
  public String getErrorText() {
    return hasProperty(this, keyErrorText) ? getPropertyAsString(this, keyErrorText) : null;
  }

  @Override
  public void setErrorText(String value) {
    setPropertyAsString(this, keyErrorText, value);
  }

  @Override
  public boolean isEqualTo(Object o) {
    if (o instanceof RpcFinishedJsoImpl) {
      return nativeIsEqualTo(o);
    } else if (o instanceof RpcFinished) {
      return RpcFinishedUtil.isEqual(this, (RpcFinished) o);
    } else {
      return false;
    }
  }

}