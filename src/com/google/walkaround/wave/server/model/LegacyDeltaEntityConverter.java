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

package com.google.walkaround.wave.server.model;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.inject.Inject;
import com.google.walkaround.proto.gson.DeltaGsonImpl;
import com.google.walkaround.slob.server.GsonProto;
import com.google.walkaround.slob.server.MutationLog.DefaultDeltaEntityConverter;
import com.google.walkaround.slob.server.MutationLog.DeltaEntityConverter;
import com.google.walkaround.slob.shared.ChangeData;
import com.google.walkaround.slob.shared.ClientId;
import com.google.walkaround.slob.shared.MessageException;
import com.google.walkaround.util.server.appengine.DatastoreUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Converts entities in a legacy format that have a single property {@code Data}
 * whose value is a JSON map with the keys {@code sid}, {@code author},
 * {@code time}, and {@code op}.  The value in the {@code author} field is
 * additionally wrapped in another JSON map with a single key {@code email}.
 *
 * Example:
 * <code>{"sid":"abc","author":{"email":"foo@example.com"},"time":1312167950103,"op":{...}}</code>
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class LegacyDeltaEntityConverter implements DeltaEntityConverter {

  private static final String DATA_PROPERTY = "Data";
  private static final ServerMessageSerializer SERIALIZER = new ServerMessageSerializer();

  private final DefaultDeltaEntityConverter next;

  @Inject LegacyDeltaEntityConverter(DefaultDeltaEntityConverter next) {
    this.next = next;
  }

  @Override public ChangeData<String> convert(Entity entity) {
    if (!entity.hasProperty(DATA_PROPERTY)) {
      return next.convert(entity);
    } else {
      String json = DatastoreUtil.getExistingProperty(entity, DATA_PROPERTY, Text.class).getValue();
      try {
        JSONObject obj = new JSONObject(json);
        DeltaGsonImpl delta = new DeltaGsonImpl();
        delta.setAuthor(obj.getJSONObject("author").getString("email"));
        delta.setTimestampMillis(obj.getLong("time"));
        delta.setOperation(SERIALIZER.deserializeOp(obj.getJSONObject("op").toString()));
        return new ChangeData<String>(new ClientId(obj.getString("sid")),
            GsonProto.toJson(delta));
      } catch (MessageException e) {
        throw new RuntimeException("MessageException converting " + entity, e);
      } catch (JSONException e) {
        throw new RuntimeException("JSONException converting " + entity, e);
      }
    }
  }

}
