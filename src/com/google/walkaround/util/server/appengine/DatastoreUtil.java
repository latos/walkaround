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
import com.google.common.base.Preconditions;

import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class DatastoreUtil {

  /**
   * Thrown by methods that parse entities if properties are invalid or missing.
   */
  public static class InvalidPropertyException extends RuntimeException {
    private static final long serialVersionUID = 211006788308217422L;

    public InvalidPropertyException(Entity entity, String propertyName) {
      this(entity, propertyName, null, null);
    }

    public InvalidPropertyException(Entity entity, String propertyName, String message) {
      this(entity, propertyName, message, null);
    }

    public InvalidPropertyException(Entity entity, String propertyName, Throwable cause) {
      this(entity, propertyName, null, cause);
    }

    public InvalidPropertyException(Entity entity, String propertyName,
        String message, Throwable cause) {
      super(message == null
          ? ("Invalid property " + propertyName + " in entity " + entity)
          : ("Invalid property " + propertyName + " in entity " + entity + ": " + message),
          cause);
    }
  }

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(DatastoreUtil.class.getName());

  @Nullable public static <T> T getOptionalProperty(
      Entity e, String propertyName, Class<T> propertyType)
      throws InvalidPropertyException {
    Preconditions.checkNotNull(e, "Null entity");
    Preconditions.checkNotNull(propertyName, "Null propertyName");
    Preconditions.checkNotNull(propertyType, "Null propertyType");
    Object value = e.getProperty(propertyName);
    if (value == null) {
      return null;
    }
    if (!propertyType.isInstance(value)) {
      throw new InvalidPropertyException(e, propertyName, "Expected property type "
          + propertyType + ", found " + value.getClass() + ": " + value);
    }
    return propertyType.cast(value);
  }

  public static <T> T getExistingProperty(Entity e, String propertyName, Class<T> propertyType)
      throws InvalidPropertyException {
    T value = getOptionalProperty(e, propertyName, propertyType);
    if (value == null) {
      throw new InvalidPropertyException(e, propertyName, "Property is required but null");
    }
    return value;
  }

  /** For Blob and Text, this doesn't really make the property indexed.
   * TODO(ohler): disallow Blob and Text? */
  public static void setNonNullIndexedProperty(Entity e, String propertyName, Object value) {
    Preconditions.checkNotNull(e, "Null entity");
    Preconditions.checkNotNull(propertyName, "Null propertyName");
    Preconditions.checkNotNull(value, "Null value");
    e.setProperty(propertyName, value);
  }

  public static void setNonNullUnindexedProperty(Entity e, String propertyName, Object value) {
    Preconditions.checkNotNull(e, "Null entity");
    Preconditions.checkNotNull(propertyName, "Null propertyName");
    Preconditions.checkNotNull(value, "Null value");
    e.setUnindexedProperty(propertyName, value);
  }

  /** For Blob and Text, this doesn't really make the property indexed.
   * TODO(ohler): disallow Blob and Text? */
  public static void setOrRemoveIndexedProperty(
      Entity e, String propertyName, @Nullable Object value) {
    Preconditions.checkNotNull(e, "Null entity");
    Preconditions.checkNotNull(propertyName, "Null propertyName");
    if (value == null) {
      e.removeProperty(propertyName);
    } else {
      e.setProperty(propertyName, value);
    }
  }

  public static void setOrRemoveUnindexedProperty(
      Entity e, String propertyName, @Nullable Object value) {
    Preconditions.checkNotNull(e, "Null entity");
    Preconditions.checkNotNull(propertyName, "Null propertyName");
    if (value == null) {
      e.removeProperty(propertyName);
    } else {
      e.setUnindexedProperty(propertyName, value);
    }
  }

}
