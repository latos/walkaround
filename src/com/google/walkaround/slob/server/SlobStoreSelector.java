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

package com.google.walkaround.slob.server;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.walkaround.util.server.servlet.BadRequestException;

import java.util.Map;

/**
 * Maps "store type" strings (= root entity kind) to the corresponding SlobFacilities.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class SlobStoreSelector {

  private final Map<String, Provider<SlobFacilities>> facilities;

  @Inject
  public SlobStoreSelector(Map<String, Provider<SlobFacilities>> facilities) {
    this.facilities = facilities;
  }

  public SlobFacilities get(String storeType) {
    Provider<SlobFacilities> provider = facilities.get(storeType);
    if (provider != null) {
      return provider.get();
    }
    throw new BadRequestException("Unknown store type: " + storeType);
  }

}
