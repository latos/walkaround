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

import org.waveprotocol.wave.client.doodad.attachment.ImageThumbnail;
import org.waveprotocol.wave.client.doodad.attachment.render.ImageThumbnailWrapper;
import org.waveprotocol.wave.client.editor.EditorContext;
import org.waveprotocol.wave.client.editor.content.CMutableDocument;
import org.waveprotocol.wave.client.editor.content.ContentNode;
import org.waveprotocol.wave.client.editor.content.FocusedContentRange;
import org.waveprotocol.wave.client.wavepanel.impl.edit.EditSession;
import org.waveprotocol.wave.client.wavepanel.impl.toolbar.EditToolbar;
import org.waveprotocol.wave.model.document.util.Point;
import org.waveprotocol.wave.model.document.util.XmlStringBuilder;

/**
 * A toolbar feature that pops up an upload dialog, and inserts an
 * image-thumbnail doodad for the attachment that it uploads.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class UploadToolbarAction implements UploadFormPopup.Listener {

  /** Session from which to pull insertion location for the doodad. */
  private final EditSession edit;

  /** Thumbnail doodad.  Created once upload starts, filled out when upload ends. */
  private ImageThumbnailWrapper thumbnail;

  UploadToolbarAction(EditSession edit) {
    this.edit = edit;
  }

  /**
   * Installs this feature.
   */
  public static void install(final EditSession edit, EditToolbar toolbar) {
    toolbar.addClickButton(Resources.css.icon(), new EditToolbar.ClickHandler() {
      @Override
      public void onClicked(EditorContext context) {
        UploadFormPopup prompt = new UploadFormPopup();
        prompt.setListener(new UploadToolbarAction(edit));
        prompt.show();
      }
    });
  }

  @Override
  public void onUploadStarted(String filename) {
    if (!edit.isEditing()) {
      // A concurrent editor may have deleted the context blip while this user
      // was filling out the upload form.  That was probably the cause of why
      // the edit session has terminated.
      // It may make sense to create a new blip somewhere and resume an edit
      // session, just in order to put the thumbnail somewhere, but that logic
      // would need to avoid making a bad choice of where that new blip goes
      // (e.g., in an unrelated thread, or in a different private reply, etc).
      // The simpler approach is to do nothing, and let the user upload the
      // file again if it still makes sense (the blip in which they intended it
      // to go has been deleted, so they may not want to upload it anymore).
      return;
    }
    EditorContext context = edit.getEditor();
    CMutableDocument doc = context.getDocument();
    FocusedContentRange selection = context.getSelectionHelper().getSelectionPoints();
    Point<ContentNode> point;
    if (selection != null) {
      point = selection.getFocus();
    } else {
      // Focus was probably lost.  Bring it back.
      context.focus(false);
      selection = context.getSelectionHelper().getSelectionPoints();
      if (selection != null) {
        point = selection.getFocus();
      } else {
        // Still no selection.  Oh well, put it at the end.
        point = doc.locate(doc.size() - 1);
      }
    }
    XmlStringBuilder content = ImageThumbnail.constructXml(null, filename);
    thumbnail = ImageThumbnailWrapper.of(doc.insertXml(point, content));
  }

  /** Inserts an attachment thumbnail at the current edit selection. */
  @Override
  public void onUploadFinished(String blobId) {
    if (thumbnail.getElement().isContentAttached()) {
      thumbnail.setAttachmentId(blobId);
    }
  }

  @Override
  public void onUploadFailure() {
    if (thumbnail != null && thumbnail.getElement().isContentAttached()) {
      thumbnail.setCaptionText("Upload failed");
    }
  }
}
