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

package com.google.walkaround.wave.server.admin;

import com.google.common.collect.Lists;
import com.google.gxp.base.GxpContext;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.walkaround.util.server.flags.FlagDeclaration;
import com.google.walkaround.wave.server.Flag;
import com.google.walkaround.wave.server.FlagConfiguration;
import com.google.walkaround.wave.server.FlagName;
import com.google.walkaround.wave.server.gxp.Admin;
import com.google.walkaround.wave.server.gxp.FlagDisplayRecord;
import com.google.walkaround.wave.server.gxp.FlagsFragment;
import com.google.walkaround.wave.server.util.AbstractHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class FlagsHandler extends AbstractHandler {

  @Inject @Named("raw flag data") String rawFlags;
  @Inject @FlagConfiguration Map<FlagDeclaration, Object> parsedFlags;
  @Inject @Flag(FlagName.ANALYTICS_ACCOUNT) String analyticsAccount;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    List<FlagDisplayRecord> records = Lists.newArrayListWithCapacity(parsedFlags.size());
    for (Map.Entry<FlagDeclaration, Object> e : parsedFlags.entrySet()) {
      records.add(new FlagDisplayRecord(e.getKey().getName(),
              "" + e.getKey().getType().getName(),
              "" + e.getValue()));
    }
    Collections.sort(records,
        new Comparator<FlagDisplayRecord>() {
          @Override public int compare(FlagDisplayRecord a, FlagDisplayRecord b) {
            return a.getName().compareTo(b.getName());
          }
        });

    resp.setContentType("text/html");
    Admin.write(resp.getWriter(), new GxpContext(req.getLocale()),
        analyticsAccount, FlagsFragment.getGxpClosure(records, rawFlags));
  }

}
