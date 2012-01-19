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

package com.google.walkaround.wave.server.gxp;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gxp.html.HtmlClosure;
import com.google.gxp.html.HtmlClosures;

/**
 * Information {@link InboxFragment} needs about a wave.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class InboxDisplayRecord {

  private final String creator;
  private final String title;
  private final String snippetHtml;
  private final String lastModified;
  private final String link;

  public InboxDisplayRecord(String creator,
      String title,
      String snippetHtml,
      String lastModified,
      String link) {
    this.creator = checkNotNull(creator, "Null creator");
    this.title = checkNotNull(title, "Null title");
    this.snippetHtml = checkNotNull(snippetHtml, "Null snippet");
    this.lastModified = checkNotNull(lastModified, "Null lastModified");
    this.link = checkNotNull(link, "Null link");
  }

  public String getCreator() {
    return creator;
  }

  public String getTitle() {
    return title;
  }

  public boolean hasSnippet() {
    return !snippetHtml.isEmpty();
  }

  public HtmlClosure getSnippetHtml() {
    return HtmlClosures.fromHtml(snippetHtml);
  }

  public String getLastModified() {
    return lastModified;
  }

  public String getLink() {
    return link;
  }

  @Override public String toString() {
    return "InboxDisplayRecord("
        + creator + ", "
        + title + ", "
        + snippetHtml + ", "
        + lastModified + ", "
        + link
        + ")";
  }

}
