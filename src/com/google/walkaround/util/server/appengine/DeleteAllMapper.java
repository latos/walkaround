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

package com.google.walkaround.util.server.appengine;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.AppEngineMapper;

import org.apache.hadoop.io.NullWritable;

import java.util.logging.Logger;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class DeleteAllMapper extends AppEngineMapper<Key, Entity, NullWritable, NullWritable> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(DeleteAllMapper.class.getName());

  // (loop repeat 20 do (insert (+ ?a (random 26))))
  private static final String CONFIRMATION_COOKIE = "DELETE_imakzeojnacjfsvbrxhu";

  @Override
  public void map(Key key, Entity value, Context context) {
    boolean really = CONFIRMATION_COOKIE.equals(
        context.getConfiguration().get("zz_confirmationCookie"));
    context.getCounter(getClass().getSimpleName(), "entities-seen").increment(1);
    if (really) {
      log.info("really deleting: " + key + ", " + value);
      getAppEngineContext(context).getMutationPool().delete(key);
      context.getCounter(getClass().getSimpleName(), "entities-really-deleted").increment(1);
    } else {
      log.info("not really deleting: " + key + ", " + value);
      context.getCounter(getClass().getSimpleName(), "entities-not-really-deleted").increment(1);
    }
    context.getCounter(getClass().getSimpleName(), "entities-processed").increment(1);
  }

}
