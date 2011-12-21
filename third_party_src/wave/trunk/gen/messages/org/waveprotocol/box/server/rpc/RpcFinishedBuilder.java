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

package org.waveprotocol.box.server.rpc;

import org.waveprotocol.box.server.rpc.RpcFinishedUtil;
import org.waveprotocol.wave.communication.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder for RpcFinisheds.
 *
 * Generated from org/waveprotocol/box/server/rpc/rpc.proto. Do not edit.
 */
public final class RpcFinishedBuilder {

  /** Factory to pass to {@link #build()}. */
  public interface Factory {
    RpcFinished create();
  }

  private Boolean failed;
  private String errorText;
  public RpcFinishedBuilder() {
  }

  public RpcFinishedBuilder setFailed(boolean value) {
    this.failed = value;
    return this;
  }

  public RpcFinishedBuilder clearErrorText() {
    errorText = null;
    return this;
  }

  public RpcFinishedBuilder setErrorText(String value) {
    this.errorText = value;
    return this;
  }

  /** Builds a {@link RpcFinished} using this builder and a factory. */
  public RpcFinished build(Factory factory) {
    RpcFinished message = factory.create();
    message.setFailed(failed);
    message.setErrorText(errorText);
    return message;
  }

}