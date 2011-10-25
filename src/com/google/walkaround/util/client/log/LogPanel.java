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

package com.google.walkaround.util.client.log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.walkaround.util.client.log.Logs.Level;

import org.waveprotocol.wave.client.widget.common.ImplPanel;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.ReadableStringMap.ProcV;
import org.waveprotocol.wave.model.util.StringMap;

import java.util.Date;
import java.util.EnumMap;

/**
 * Handler for pretty printing log events in the DOM. Can be turned on and off.
 * For debugging convenience, when it's turned on, it renders old entries that
 * were logged before it was active.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author hearnden@google.com (David Hearnden)
 */
public final class LogPanel extends Composite implements Logs.Handler {

  /**
   * Focuses an element in a schedule-finally command. When it eventually runs, it
   * only focuses the most recently chosen element.
   */
  private static final class Focuser implements ScheduledCommand {
    private Element toFocus;

    Focuser() {
    }

    void focusLater(Element e) {
      if (toFocus == null) {
        Scheduler.get().scheduleFinally(this);
      }
      toFocus = e;
    }

    @Override
    public final void execute() {
      toFocus.scrollIntoView();
      toFocus = null;
    }
  }

  //
  // Standard 40 lines of GWT per-component boilerplate. This is required in
  // order to stop GWT from injecting its own style element (at the wrong
  // time, and without revealing access to it) for the CssResource.
  //
  interface Resources extends ClientBundle {
    @Source("LogPanel.css")
    Css css();
  }

  interface Css extends CssResource {
    // Strict mode forces declaration of every class, even those used only by
    // the UiBinder template.
    String self();

    String entries();

    String header();

    String button();

    String control();

    String enabled();

    String disabled();

    String level();

    String stream();

    String debug();

    String info();

    String warning();

    String severe();

    String item();
  }

  @UiField(provided = true)
  static final Css css = GWT.<Resources>create(Resources.class).css();

  interface Binder extends UiBinder<ImplPanel, LogPanel> {
  }

  private static final Binder BINDER = GWT.create(Binder.class);

  //
  // Interesting code starts below.
  //

  /**
   * A button that controls filtering of some dimension of log entries.
   *
   * Each filter button synthesizes an associated CSS rule of the form:
   * <dl>
   * <dd>.hideFoo .foo { display: none; }</dd>
   * </dl>
   * for some dimension foo (e.g., a log level, or a stream).
   */
  static class FilterButton extends Label implements ClickHandler {
    /** Element to which filtering classes are added/removed. */
    private final Element itemContainer;
    /** CSS class to toggle on the container ({@code .hideFoo} in example). */
    private final String containerClass;
    /** CSS class of items affected by this rule (@code .foo} in example). */
    private final String itemClass;
    /** True when this filter is not hiding entries. */
    private boolean allow = true;

    FilterButton(DoubleClickHandler superSelect,
        Element itemContainer,
        String containerClass,
        String itemClass,
        String label,
        String buttonClass) {
      super(label);
      this.itemContainer = itemContainer;
      this.itemClass = itemClass;
      this.containerClass = containerClass;
      setStyleName(LogPanel.css.button() + " " + LogPanel.css.enabled() + " " + buttonClass);
      addClickHandler(this);
      if (superSelect != null) {
        addDoubleClickHandler(superSelect);
      }
    }

    @Override
    public void onClick(ClickEvent event) {
      setAllow(!allow);
    }

    void setAllow(boolean allow) {
      if (this.allow != allow) {
        if (allow) {
          getElement().replaceClassName(LogPanel.css.disabled(), LogPanel.css.enabled());
          itemContainer.removeClassName(containerClass);
        } else {
          getElement().replaceClassName(LogPanel.css.enabled(), LogPanel.css.disabled());
          itemContainer.addClassName(containerClass);
        }
        this.allow = allow;
      }
    }

    /** @return the class to apply to items controlled by this filter. */
    public String getItemClass() {
      return itemClass;
    }

    /** @return the CSS rule that makes this filter work. */
    public String getCssRule() {
      return "." + containerClass + " ." + itemClass + " {display:none;}";
    }
  }

  /** Maximum number of DOM entries. Recycling occurs after this. */
  private static final int MAX_ENTRIES = 3000;

  /**
   * Prefixes for stream and level filtering CSS classes. When a filter class is
   * applied to the entries element, they hide entries that match the filter.
   */
  private static final String STREAM_FILTER_PREFIX = "w-hs-";
  private static final String LEVEL_FILTER_PREFIX = "w-hl-";

  /** Pretty labels for printing levels. Designed for fixed-width output. */
  private static final EnumMap<Level, String> LABELS = new EnumMap<Level, String>(Level.class);
  static {
    // All labels have equal size.
    LABELS.put(Level.INFO, "INFO  ");
    LABELS.put(Level.DEBUG, "DEBUG ");
    LABELS.put(Level.WARNING, "WARN  ");
    LABELS.put(Level.SEVERE, "SEVERE");
    assert LABELS.size() == Level.values().length;
  }

  /** CSS classes for log levels. */
  private static final EnumMap<Level, String> LEVEL_CLASSES =
      new EnumMap<Level, String>(Level.class);
  static {
    LEVEL_CLASSES.put(Level.DEBUG, css.debug());
    LEVEL_CLASSES.put(Level.INFO, css.info());
    LEVEL_CLASSES.put(Level.WARNING, css.warning());
    LEVEL_CLASSES.put(Level.SEVERE, css.severe());
    assert LEVEL_CLASSES.size() == Level.values().length;
  }

  @UiField
  ImplPanel self;
  @UiField
  Element play;
  @UiField
  Element clear;
  @UiField
  Element levelsContainer;
  @UiField
  Element streamsContainer;
  @UiField
  Element entries;

  /** Log being handled. */
  private final Logs log;
  /** Buttons that disable particular levels. */
  private final EnumMap<Level, FilterButton> levelFilters =
      new EnumMap<Level, FilterButton>(Level.class);
  /** Buttons that disable particular streams. */
  private final StringMap<FilterButton> streamFilters = CollectionUtils.createStringMap();
  /** Style element holding filter rules. */
  private final StyleElement style = Document.get().createStyleElement();
  /** Thing that brings the most recent log entry into view. */
  private final Focuser focuser = new Focuser();

  /** True if this panel is creating DOM for logs. Does not mean visible. */
  private boolean active;
  /** Index into the monotonic log history of the most recently printed log. */
  private int upTo;
  /** Number of entries in the DOM, constrained by {@link #MAX_ENTRIES}. */
  private int numEntries;

  private LogPanel(Logs log) {
    this.log = log;
    initWidget(BINDER.createAndBindUi(this));

    // Add filter buttons. Global filter, then log-level filters.
    StringBuilder rules = new StringBuilder();
    rules.append(css.getText());

    for (final Level level : Level.values()) {
      String itemClass = LEVEL_CLASSES.get(level);
      String containerClass = LEVEL_FILTER_PREFIX + itemClass;
      FilterButton levelFilter = new FilterButton(new DoubleClickHandler() {
        @Override
        public void onDoubleClick(DoubleClickEvent event) {
          enableOnlyMoreImportantLevels(level);
        }
      }, entries, containerClass, itemClass, level.name(), css.level());
      levelFilters.put(level, levelFilter);
      self.add(levelFilter, levelsContainer);
      rules.append(levelFilter.getCssRule());
    }

    // Inject style.
    style.setInnerText(rules.toString());
    Document.get().getBody().appendChild(style);

    setActive(true);
  }

  /**
   * Creates a log panel.
   *
   * @param log log to observe
   */
  public static LogPanel create(Logs log) {
    LogPanel panel = new LogPanel(log);
    panel.setActive(true);
    return panel;
  }

  /**
   * Creates a log panel, bringing attention to the severe logs of a particular
   * stream.
   */
  public static LogPanel createOnStream(Logs log, final String stream) {
    LogPanel panel = create(log);
    panel.enableOnlyMoreImportantLevels(Level.SEVERE);
    panel.enableOnlyOneStream(stream);
    return panel;
  }

  /** Activates/deactivates this handler. */
  private void setActive(boolean active) {
    boolean wasActive = this.active;
    this.active = active;
    if (!wasActive && active) {
      log.pushEntries(upTo, this);
    }
  }

  private void enableOnlyOneStream(final String selected) {
    streamFilters.each(new ProcV<FilterButton>() {
      @Override
      public void apply(String stream, FilterButton filter) {
        filter.setAllow(stream.equals(selected));
      }
    });
  }

  private void enableOnlyMoreImportantLevels(Level limit) {
    for (Level level : Level.values()) {
      levelFilters.get(level).setAllow(level.compareTo(limit) <= 0);
    }
  }

  /**
   * Gets the filter for a stream, creating one if one does not already exist.
   */
  private FilterButton getStreamFilter(final String stream) {
    FilterButton filter = streamFilters.get(stream);
    if (filter == null) {
      // Just in case a user can cause a stream to be called "'><script
      // src='evil.js'></script><pre '", we only use safe values for CSS
      // classes. There's no need for the class names to be human readable.
      int streamId = streamFilters.countEntries();
      String itemClass = "w-s-" + streamId;
      String containerClass = STREAM_FILTER_PREFIX + streamId;
      filter = new FilterButton(new DoubleClickHandler() {
        @Override
        public void onDoubleClick(DoubleClickEvent event) {
          enableOnlyOneStream(stream);
        }
      }, entries, containerClass, itemClass, stream, css.stream());
      self.add(filter, streamsContainer);
      streamFilters.put(stream, filter);
      // Replace stylesheet to make the new filter work.
      style.setInnerText(style.getInnerText() + filter.getCssRule());
    }
    return filter;
  }

  @UiHandler("self")
  void handleClick(ClickEvent e) {
    Element target = e.getNativeEvent().getEventTarget().cast();
    if (play.equals(target)) {
      if (active) {
        setActive(false);
        play.setInnerText("Resume");
      } else {
        setActive(true);
        play.setInnerText("Pause");
      }
    } else if (clear.equals(target)) {
      entries.setInnerHTML("");
    }
  }

  //
  // LogHandler.
  //

  @Override
  public boolean canReceive() {
    return active;
  }

  @Override
  public void receiveEntry(int num,
      double timestamp,
      String stream,
      Level level,
      Object[] objects,
      Throwable ex) {
    StringBuilder b = new StringBuilder();
    b.append(DateTimeFormat.getFormat("HH:mm:ss.SSS").format(new Date((long) timestamp)) + " "
        + LABELS.get(level) + " (" + stream + "): ");
    for (Object o : objects) {
      if (o instanceof Throwable) {
        printStackTrace((Throwable) o, b);
      } else {
        b.append(o != null ? o.toString() : "(null)");
      }
    }

    Element pre;
    if (numEntries < MAX_ENTRIES) {
      pre = Document.get().createPreElement();
      numEntries++;
    } else {
      pre = entries.getFirstChildElement();
    }
    String streamClass = getStreamFilter(stream).getItemClass();
    String levelClass = LEVEL_CLASSES.get(level);
    pre.setClassName(css.item() + " " + streamClass + " " + levelClass);
    pre.setInnerText(b.toString());
    entries.appendChild(pre);
    focuser.focusLater(pre);

    upTo = num + 1;
  }

  /**
   * Prints a stack trace as text. The output is intended to be placed in a
   * &lt;pre&gt; element, so no HTML markup is used.
   */
  private static void printStackTrace(Throwable t, StringBuilder out) {
    while (t != null) {
      out.append(t.getClass().getName() + ": " + t.getLocalizedMessage() + "\n");
      StackTraceElement[] elts = t.getStackTrace();
      for (int i = 0; i < elts.length; i++) {
        out.append("    at " + elts[i].getClassName() + "." + elts[i].getMethodName() + "("
            + elts[i].getFileName() + ":" + elts[i].getLineNumber() + ")\n");
      }
      t = t.getCause();
      if (t != null) {
        out.append("Caused by: ");
      }
    }
  }
}
