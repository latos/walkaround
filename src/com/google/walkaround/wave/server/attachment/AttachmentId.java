/*
 * Copyright 2012 Google Inc. All Rights Reserved.
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

package com.google.walkaround.wave.server.attachment;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.io.PrintWriter;

import javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * Attachment ID.  This is distinct from an App Engine BlobKey since code
 * outside the server shouldn't reason about BlobKeys.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class AttachmentId implements Serializable {

  private static final long serialVersionUID = 112261689375404053L;

  private final String id;

  public AttachmentId(String id) {
    this.id = checkNotNull(id, "Null id");
  }

  public String getId() {
    return id;
  }

  @Override public String toString() {
    return getClass().getSimpleName() + "(" + id + ")";
  }

  @Override public final boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof AttachmentId)) { return false; }
    AttachmentId other = (AttachmentId) o;
    return Objects.equal(id, other.id);
  }

  @Override public final int hashCode() {
    return Objects.hashCode(id);
  }

}
