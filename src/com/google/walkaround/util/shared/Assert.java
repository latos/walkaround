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

package com.google.walkaround.util.shared;

import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nullable;

/**
 * Similar to {@link com.google.common.base.Preconditions} but for assertions.
 * We use this instead of {@code assert} because ensuring whether {@code assert}
 * is enabled in a given environment can be difficult.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class Assert {

  private Assert() {}

  /**
   * Ensures the truth of an expression.
   *
   * @param expression a boolean expression
   * @throws AssertionError if {@code expression} is false
   */
  public static void check(boolean expression) {
    if (!expression) {
      throw new AssertionError();
    }
  }

  /**
   * Ensures the truth of an expression.
   *
   * @param expression a boolean expression
   * @param errorMessage the exception message to use if the check fails; will
   *     be converted to a string using {@link String#valueOf(Object)}
   * @throws AssertionError if {@code expression} is false
   */
  public static void check(boolean expression, @Nullable Object errorMessage) {
    if (!expression) {
      throw new AssertionError(String.valueOf(errorMessage));
    }
  }

  /**
   * Ensures the truth of an expression.
   *
   * @param expression a boolean expression
   * @param errorMessageTemplate a template for the exception message should the
   *     check fail. The message is formed by replacing each {@code %s}
   *     placeholder in the template with an argument. These are matched by
   *     position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
   *     Unmatched arguments will be appended to the formatted message in square
   *     braces. Unmatched placeholders will be left as-is.
   * @param errorMessageArgs the arguments to be substituted into the message
   *     template. Arguments are converted to strings using
   *     {@link String#valueOf(Object)}.
   * @throws IllegalArgumentException if {@code expression} is false
   * @throws NullPointerException if the check fails and either {@code
   *     errorMessageTemplate} or {@code errorMessageArgs} is null (don't let
   *     this happen)
   */
  public static void check(boolean expression,
      @Nullable String errorMessageTemplate,
      @Nullable Object... errorMessageArgs) {
    if (!expression) {
      throw new AssertionError(
          format(errorMessageTemplate, errorMessageArgs));
    }
  }

  /**
   * Substitutes each {@code %s} in {@code template} with an argument. These
   * are matched by position - the first {@code %s} gets {@code args[0]}, etc.
   * If there are more arguments than placeholders, the unmatched arguments will
   * be appended to the end of the formatted message in square braces.
   *
   * @param template a non-null string containing 0 or more {@code %s}
   *     placeholders.
   * @param args the arguments to be substituted into the message
   *     template. Arguments are converted to strings using
   *     {@link String#valueOf(Object)}. Arguments can be null.
   */
  @VisibleForTesting static String format(String template,
      @Nullable Object... args) {
    template = String.valueOf(template); // null -> "null"

    // start substituting the arguments into the '%s' placeholders
    StringBuilder builder = new StringBuilder(
        template.length() + 16 * args.length);
    int templateStart = 0;
    int i = 0;
    while (i < args.length) {
      int placeholderStart = template.indexOf("%s", templateStart);
      if (placeholderStart == -1) {
        break;
      }
      builder.append(template.substring(templateStart, placeholderStart));
      builder.append(args[i++]);
      templateStart = placeholderStart + 2;
    }
    builder.append(template.substring(templateStart));

    // if we run out of placeholders, append the extra args in square braces
    if (i < args.length) {
      builder.append(" [");
      builder.append(args[i++]);
      while (i < args.length) {
        builder.append(", ");
        builder.append(args[i++]);
      }
      builder.append(']');
    }

    return builder.toString();
  }

}
