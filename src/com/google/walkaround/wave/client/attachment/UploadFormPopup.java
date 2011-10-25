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

package com.google.walkaround.wave.client.attachment;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.UIObject;
import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;

import org.waveprotocol.wave.client.common.util.DomHelper;
import org.waveprotocol.wave.client.common.util.DomHelper.HandlerReference;
import org.waveprotocol.wave.client.common.util.DomHelper.JavaScriptEventListener;
import org.waveprotocol.wave.client.widget.popup.CenterPopupPositioner;
import org.waveprotocol.wave.client.widget.popup.PopupChrome;
import org.waveprotocol.wave.client.widget.popup.PopupChromeFactory;
import org.waveprotocol.wave.client.widget.popup.PopupFactory;
import org.waveprotocol.wave.client.widget.popup.UniversalPopup;

/**
 * Manages the flow for uploading attachments.
 * <p>
 * First, a popup is shown with a form containing the file input. In parallel, a
 * hidden iframe is injected to the body. The form's target is set to that
 * iframe, so it can be submitted without reloading the main frame.
 * Additionally, that iframe is used to fetch an upload token, encoded as the
 * action URL for the form (an AppEngine requirement). An iframe is used for
 * fetching the token instead of an XHR only because the iframe is already
 * needed for the form submission.
 * <p>
 * On retrieval of the upload token, the popup form's action URL is set, and its
 * submit button is enabled.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class UploadFormPopup {

  /** Observer of the upload flow. */
  public interface Listener {
    /** Notifies this listener that the file is about to start uploading.
     * @param filename */
    void onUploadStarted(String filename);

    /** Notifies this listener that the file has been uploaded. */
    void onUploadFinished(String blobId);

    /** Notifies this listener that an error occured. */
    void onUploadFailure();
  }

  interface Binder extends UiBinder<FormPanel, UploadFormPopup> {
  }

  private static final Binder BINDER = GWT.create(Binder.class);
  @UiField(provided = true)
  static final Resources.Css css = Resources.css;

  private static final Log log = Logs.create("upload");
  /** Counter for ensuring unique names for the iframes. */
  private static int iframeId;

  // The popup.
  private final UniversalPopup popup;
  private final FormPanel form;
  @UiField
  FileUpload file;
  @UiField
  SubmitButton submit;

  private final IFrameElement iframe;
  private final HandlerReference onloadRegistration;

  /** Optional listener. */
  private Listener listener;

  /** States in the upload flow. */
  enum Stage {
    START, FETCHING_TOKEN, HAVE_TOKEN, UPLOADING_FILE, END
  }

  /** Flow state. Increases monotonically, never null. */
  private Stage stage = Stage.START;

  /**
   * Creates an upload popup.
   */
  public UploadFormPopup() {
    form = BINDER.createAndBindUi(this);

    PopupChrome chrome = PopupChromeFactory.createPopupChrome();
    chrome.enableTitleBar();
    popup = PopupFactory.createPopup(null, new CenterPopupPositioner(), chrome, false);
    popup.getTitleBar().setTitleText("Upload attachment");
    popup.add(form);

    iframe = Document.get().createIFrameElement();
    iframe.setName("_uploadform" + iframeId++);
    // HACK(danilatos): Prevent browser from caching due to whatever reason
    iframe.setSrc("/uploadform?nocache=" + Duration.currentTimeMillis());
    form.getElement().setAttribute("target", iframe.getName());

    onloadRegistration =
        DomHelper.registerEventHandler(iframe, "load", new JavaScriptEventListener() {
          @Override
          public void onJavaScriptEvent(String name, Event event) {
            onIframeLoad();
          }
        });
    UIObject.setVisible(iframe, false);
  }

  /** Sets the listener for upload status events. */
  public void setListener(Listener listener) {
    Preconditions.checkState(this.listener == null);
    Preconditions.checkArgument(listener != null);
    this.listener = listener;
  }

  /** Transitions into the end state, releasing resources. */
  private void destroy() {
    onloadRegistration.unregister();
    popup.hide();
    iframe.removeFromParent();
    stage = Stage.END;
    log.log(Level.INFO, "end");
  }

  /** Transitions into the end state due to a failure. */
  private void fail(String msg) {
    log.log(Level.WARNING, "Upload failed. ", msg);
    destroy();
    if (listener != null) {
      listener.onUploadFailure();
    }
  }

  /** Shows the popup, initiating the upload flow. */
  public void show() {
    Preconditions.checkState(stage == Stage.START);
    stage = Stage.FETCHING_TOKEN;
    popup.show();
    Document.get().getBody().appendChild(iframe);
    log.log(Level.INFO, "flow started");
  }

  private void onIframeLoad() {
    switch (stage) {
      case FETCHING_TOKEN:
        String action = getTokenedAction();
        if (action == null) {
          fail("could not get upload token.");
        } else {
          form.setAction(action);
          maybeEnableSubmit();
          stage = Stage.HAVE_TOKEN;
          log.log(Level.INFO, "token received: ", action);
        }
        break;
      case UPLOADING_FILE:
        String attachmentId = getAttachmentId();
        if (attachmentId == null) {
          fail("upload failed.");
        } else {
          destroy();
          log.log(Level.INFO, "success: ", attachmentId);
          if (listener != null) {
            listener.onUploadFinished(attachmentId);
          }
        }
        break;
      case END:
        // Some failure has already ended the flow. Ignore.
        break;
      default:
        throw new AssertionError();
    }
  }

  /**
   * Enables the submit button if this form has an upload token and a file is
   * selected. Otherwise, disables it.
   */
  private void maybeEnableSubmit() {
    submit.setEnabled(stage == Stage.HAVE_TOKEN && file.getFilename() != null);
  }

  //
  // Form events
  //

  @UiHandler("file")
  void handleFile(ChangeEvent e) {
    maybeEnableSubmit();
  }

  @UiHandler("cancel")
  void handleCancel(ClickEvent e) {
    destroy();
    stage = Stage.END;
    log.log(Level.INFO, "cancelled");
  }

  @UiHandler("submit")
  void handleSubmit(ClickEvent e) {
    Preconditions.checkState(stage == Stage.HAVE_TOKEN);
    // The browser's default action can not be relied upon to submit the form,
    // because this handler is removing the form from the DOM, so a manual
    // submit is required (before hiding the popup).
    e.stopPropagation();
    form.submit();
    popup.hide();
    stage = Stage.UPLOADING_FILE;
    log.log(Level.INFO, "posting file");
    if (listener != null) {
      listener.onUploadStarted(fixFilename(file.getFilename()));
    }
  }

  /**
   * Gets a property of the iframe's window. Setting such properties is how the
   * iframe communicates back with this window.
   */
  private static native String getFrameProperty(IFrameElement frame, String property)
  /*-{
    return frame.contentWindow[property];
  }-*/;

  private String getTokenedAction() {
    return getFrameProperty(iframe, "_formAction");
  }

  private String getAttachmentId() {
    return getFrameProperty(iframe, "_attachmentId");
  }

  /** Strips the C:\fakepath\ prefix inserted by some browsers. */
  private static String fixFilename(String filename) {
    // Some browsers (e.g., Chrome) report filenames "foo.png" with a fake path:
    // "C:\fakepath\foo.png". Other browsers (e.g., Firefox) report filenames
    // properly: "foo.png".
    String fakepath = "C:\\fakepath\\";
    return filename.startsWith(fakepath) ? filename.substring(fakepath.length()) : filename;
  }
}
