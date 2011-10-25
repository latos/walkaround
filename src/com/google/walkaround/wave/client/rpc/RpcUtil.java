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

import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.wave.client.JsUtil;
import com.google.walkaround.wave.shared.SharedConstants;

import org.waveprotocol.wave.client.common.util.JsoView;

/**
 * Rpc utilities.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 */
public final class RpcUtil {
  private RpcUtil(){}

  public static JsoView evalPrefixed(String data) throws MessageException {
    if (!data.startsWith(SharedConstants.XSSI_PREFIX)) {
      throw new MessageException("Data did not start with XSSI prefix: " + data);
    }
    return JsUtil.eval(data.substring(SharedConstants.XSSI_PREFIX.length()));
  }
}
