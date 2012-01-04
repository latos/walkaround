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

package com.google.walkaround.wave.shared;

import com.google.walkaround.proto.ProtocolDocumentOperation;
import com.google.walkaround.proto.ProtocolWaveletOperation;
import com.google.walkaround.proto.ProtocolWaveletOperation.MutateDocument;
import com.google.walkaround.wave.shared.MessageWrapperDocOp.DocOpMessageProvider;

import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.automaton.DocOpAutomaton.ViolationCollector;
import org.waveprotocol.wave.model.document.operation.impl.DocOpUtil;
import org.waveprotocol.wave.model.document.operation.impl.DocOpValidator;
import org.waveprotocol.wave.model.operation.wave.AddParticipant;
import org.waveprotocol.wave.model.operation.wave.BlipContentOperation;
import org.waveprotocol.wave.model.operation.wave.NoOp;
import org.waveprotocol.wave.model.operation.wave.RemoveParticipant;
import org.waveprotocol.wave.model.operation.wave.WaveletBlipOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperation;
import org.waveprotocol.wave.model.operation.wave.WaveletOperationContext;
import org.waveprotocol.wave.model.wave.InvalidParticipantAddress;
import org.waveprotocol.wave.model.wave.ParticipantId;

/**
 * A factory for creating operations from messages.
 *
 * @author Alexandre Mah
 */
public final class OperationFactory {

  /** Private default constructor to enforce noninstantiability. */
  private OperationFactory() {}

  private static final WaveletOperationContext DUMMY_CONTEXT = new WaveletOperationContext(
      null, 0L, 0L);

  public static class InvalidInputException extends Exception {
    private static final long serialVersionUID = 626293536856959309L;

    public InvalidInputException(String message) {
      super(message);
    }
    public InvalidInputException(Throwable cause) {
      super(cause);
    }
    public InvalidInputException(String message, Throwable cause) {
      super(message, cause);
    }
  }


  /**
   * Creates a WaveletOperation with a dummy context.
   *
   * @param message The message that should be contained within the
   *        WaveletOperation.
   * @return The newly-created WaveletOperation.
   */
  public static WaveletOperation createContextFreeWaveletOperation(
      ProtocolWaveletOperation message) throws InvalidInputException {
    return createWaveletOperation(DUMMY_CONTEXT, message);
  }

  /**
   * Deserializes a wavelet-operation message.
   *
   * @param context  operation context for the operation
   * @param message  operation message
   * @param checkWellFormed If we should check for wellformness of deserialised DocOp
   * @return an operation described by {@code message}
   */
  public static WaveletOperation createWaveletOperation(WaveletOperationContext context,
      ProtocolWaveletOperation message, boolean checkWellFormed)
      throws InvalidInputException {

    try {
      if (message.hasNoOp() && message.getNoOp()) {
        return new NoOp(context);
      } else if (message.getAddParticipant() != null && !message.getAddParticipant().isEmpty()) {
        return new AddParticipant(context, ParticipantId.of(message.getAddParticipant()));
      } else if (message.getRemoveParticipant() != null
          && !message.getRemoveParticipant().isEmpty()) {
        return new RemoveParticipant(context, ParticipantId.of(message.getRemoveParticipant()));
      } else if (message.getMutateDocument() != null) {
        return createBlipOperation(context, message, checkWellFormed);
      }
      throw new IllegalArgumentException("Unsupported operation: " + message);
    } catch (InvalidParticipantAddress e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Deserializes a wavelet-operation message. Checsk for wellformness of deserialised DocOp
   *
   * @param context  operation context for the operation
   * @param message  operation message
   * @return an operation described by {@code message}
   */
  public static WaveletOperation createWaveletOperation(
      WaveletOperationContext context, ProtocolWaveletOperation message)
      throws InvalidInputException {
    return createWaveletOperation(context, message, true);
  }

  /**
   * Creates a wavelet-blip operation from a message.
   *
   * @param context
   * @param message
   * @param checkWellFormed If we should check for wellformness of deserialised DocOp
   * @return a wavelet-blip operation described by {@code message}, or {@code null} if
   *   {@code message} does not describe a blip operation.
   */
  private static WaveletBlipOperation createBlipOperation(WaveletOperationContext context,
      ProtocolWaveletOperation message, boolean checkWellFormed)
      throws InvalidInputException {

    if (message.getMutateDocument() != null) {
      MutateDocument opMessage = message.getMutateDocument();
      return new WaveletBlipOperation(opMessage.getDocumentId(), new BlipContentOperation(
          context, createDocumentOperation(opMessage.getDocumentOperation(), checkWellFormed)));
    }
    throw new InvalidInputException("Failed to create blip operation for message " + message);
  }

  /**
   * Create an implementation of DocumentOperation.
   *
   * @param message The message representing the document operation.
   * @param checkWellFormed If we should check for wellformness of deserialised DocOp
   * @return The created operation.
   */
  public static DocOp createDocumentOperation(
      final ProtocolDocumentOperation message, boolean checkWellFormed)
      throws InvalidInputException {
    return createDocumentOperation(new DocOpMessageProvider() {
      @Override
      public ProtocolDocumentOperation getContent() {
        return message;
      }
    }, checkWellFormed);
  }

  /**
   * Create an implementation of DocumentOperation.
   *
   * @param input The provider of the message to wrap.
   * @param checkWellFormed If we should check for wellformness of deserialised DocOp
   * @return The created operation.
   */
  private static DocOp createDocumentOperation(
      final DocOpMessageProvider input, boolean checkWellFormed) throws InvalidInputException {

    DocOp value = new MessageWrapperDocOp(input, checkWellFormed);

    if (checkWellFormed) {
      try {
        if (!DocOpValidator.isWellFormed(null, value)) {
          // Check again, collecting violations this time.
          ViolationCollector v = new ViolationCollector();
          DocOpValidator.isWellFormed(v, value);
          throw new InvalidInputException("Attempt to build ill-formed operation ("
              + v + "): " + value);
        }
      } catch (MessageWrapperDocOp.DelayedInvalidInputException e) {
        throw new InvalidInputException("Caught DelayedInvalidInputException while validating: "
            // Append e's message to our own message here.  It's not
            // enough to have e's message somewhere in the cause chain
            // because some places only log the getMessage() of our
            // exception.
            + e + ", " + input, e);
      }
    }

    return value;
  }

  /**
   * Create an implementation of DocumentOperation.
   * Checks for wellformness of deserialised DocOp.
   *
   * @param message The message representing the document operation.
   * @return The created operation.
   */
  public static DocOp createDocumentOperation(final ProtocolDocumentOperation message)
      throws InvalidInputException {
    return createDocumentOperation(message, true);
  }

  /**
   * Creates a document initialization from an operation message.
   *
   * @param message The message representing the document operation.
   * @param checkWellFormed If we should check for well-formedness of deserialised DocOp
   * @return The created operation.
   */
  public static DocInitialization createDocumentInitialization(
      ProtocolDocumentOperation message, boolean checkWellFormed) throws InvalidInputException {
    return DocOpUtil.asInitialization(createDocumentOperation(message, checkWellFormed));
  }

  /**
   * Creates a document initialization from an operation message.
   * Checks for wellformness of deserialised DocOp.
   *
   * @param message The message representing the document operation.
   * @return The created operation.
   */
  public static DocInitialization createDocumentInitialization(ProtocolDocumentOperation message)
      throws InvalidInputException {
    return createDocumentInitialization(message, true);
  }
}
