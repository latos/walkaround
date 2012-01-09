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

package com.google.walkaround.util.server.writebatch;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;

/**
 * Messages used in {@link BatchingUpdateProcessor}.
 *
 * @author ohler@google.com (Christian Ohler)
 */
// Package-private because only BatchingUpdateProcessor needs access.
class Messages {

  private Messages() {}

  /* (ohler-java-generate-union-type "<R>"
       '(Message nil
         ((YourTurnMessage nil nil)
          (ResultMessage ((R result nil)) nil)
          (PermanentFailureMessage ((PermanentFailure failure nil)) nil)))) */

  public enum MessageType {
    YOUR_TURN,
    RESULT,
    PERMANENT_FAILURE;
  }

  public static abstract class Message<R> {
    // Package-private so that no subclasses can be added outside this package.
    Message() {
    }

    public abstract MessageType getType();

    public boolean isYourTurnMessage() {
      return false;
    }

    public boolean isResultMessage() {
      return false;
    }

    public boolean isPermanentFailureMessage() {
      return false;
    }

    public YourTurnMessage<R> asYourTurnMessage() {
      throw new ClassCastException("Attempt to call asYourTurnMessage() on " + this);
    }

    public ResultMessage<R> asResultMessage() {
      throw new ClassCastException("Attempt to call asResultMessage() on " + this);
    }

    public PermanentFailureMessage<R> asPermanentFailureMessage() {
      throw new ClassCastException("Attempt to call asPermanentFailureMessage() on " + this);
    }
  }

  public static class YourTurnMessage<R> extends Message<R> {
    // Package-private so that no subclasses can be added outside this package.
    YourTurnMessage() {
    }

    @Override public String toString() {
      return "YourTurnMessage()";
    }

    @Override public boolean equals(Object o) {
      return o != null && o.getClass() == YourTurnMessage.class;
    }

    @Override public int hashCode() {
      return Objects.hashCode(YourTurnMessage.class);
    }

    @Override public MessageType getType() {
      return MessageType.YOUR_TURN;
    }

    @Override public boolean isYourTurnMessage() {
      return true;
    }

    @Override public YourTurnMessage<R> asYourTurnMessage() {
      return this;
    }

    public static <R> YourTurnMessage<R> of() {
      return new YourTurnMessage<R>();
    }
  }

  public static class ResultMessage<R> extends Message<R> {
    private final R result;

    // Package-private so that no subclasses can be added outside this package.
    ResultMessage(R result) {
      Preconditions.checkNotNull(result, "Null result");
      this.result = result;
    }

    public R getResult() {
      return result;
    }

    @Override public String toString() {
      return "ResultMessage("
          + result
          + ")";
    }

    @Override public boolean equals(Object o) {
      if (o == null) return false;
      if (o == this) return true;
      if (!(o.getClass() == ResultMessage.class)) return false;
      ResultMessage<?> other = (ResultMessage<?>) o;
      return result.equals(other.result);
    }

    @Override public int hashCode() {
      return Objects.hashCode(ResultMessage.class, result);
    }

    @Override public MessageType getType() {
      return MessageType.RESULT;
    }

    @Override public boolean isResultMessage() {
      return true;
    }

    @Override public ResultMessage<R> asResultMessage() {
      return this;
    }

    public static <R> ResultMessage<R> of(R result) {
      return new ResultMessage<R>(result);
    }
  }

  public static class PermanentFailureMessage<R> extends Message<R> {
    private final PermanentFailure failure;

    // Package-private so that no subclasses can be added outside this package.
    PermanentFailureMessage(PermanentFailure failure) {
      Preconditions.checkNotNull(failure, "Null failure");
      this.failure = failure;
    }

    public PermanentFailure getFailure() {
      return failure;
    }

    @Override public String toString() {
      return "PermanentFailureMessage("
          + failure
          + ")";
    }

    @Override public boolean equals(Object o) {
      if (o == null) return false;
      if (o == this) return true;
      if (!(o.getClass() == PermanentFailureMessage.class)) return false;
      PermanentFailureMessage<?> other = (PermanentFailureMessage<?>) o;
      return failure.equals(other.failure);
    }

    @Override public int hashCode() {
      return Objects.hashCode(PermanentFailureMessage.class, failure);
    }

    @Override public MessageType getType() {
      return MessageType.PERMANENT_FAILURE;
    }

    @Override public boolean isPermanentFailureMessage() {
      return true;
    }

    @Override public PermanentFailureMessage<R> asPermanentFailureMessage() {
      return this;
    }

    public static <R> PermanentFailureMessage<R> of(PermanentFailure failure) {
      return new PermanentFailureMessage<R>(failure);
    }
  }

}
