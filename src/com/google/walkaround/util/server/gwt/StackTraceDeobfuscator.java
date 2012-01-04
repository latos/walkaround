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

package com.google.walkaround.util.server.gwt;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapted from {@link com.google.gwt.logging.server.StackTraceDeobfuscator}
 * with some changes.
 *
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
@Singleton
public class StackTraceDeobfuscator {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(StackTraceDeobfuscator.class.getName());

  /** Retrieves symbol maps. */
  public interface SymbolMapsDirectory {
    InputStream getSymbolMapInputStream(String permutationStrongName) throws IOException;
  }

  // From JsniRef class, which is in gwt-dev and so can't be accessed here
  // TODO: once there is a place for shared code, move this to there.
  private static final Pattern JSNI_REF_PATTERN =
      Pattern.compile("@?([^:]+)::([^(]+)(\\((.*)\\))?");

  private SymbolMapsDirectory symbolMapsDirectory;

  // See commit ebb4736368b6d371a1bf5005541d96b88dcac504 for my failed attempt
  // at using CacheBuilder.  TODO(ohler): Figure out the right solution to this.
  @SuppressWarnings("deprecation")
  private final Map<String, Map<String, String>> symbolMaps =
    new MapMaker()
        .maximumSize(100)
        .makeComputingMap(
            new Function<String, Map<String, String>>() {
              @Override public Map<String, String> apply(String strongName) {
                try {
                  // Can't use ImmutableMap.Builder since we may have duplicate keys like $clinit.
                  Map<String, String> out = new HashMap<String, String>();
                  BufferedReader bin = new BufferedReader(
                      new InputStreamReader(
                          symbolMapsDirectory.getSymbolMapInputStream(strongName)));
                  try {
                    String line;
                    while ((line = bin.readLine()) != null) {
                      if (line.charAt(0) == '#') {
                        continue;
                      }
                      int idx = line.indexOf(',');
                      out.put(line.substring(0, idx), line.substring(idx + 1));
                    }
                  } finally {
                    bin.close();
                  }
                  return ImmutableMap.copyOf(out);
                } catch (IOException e) {
                  log.log(Level.WARNING, "Symbol map not found: " + strongName, e);
                  // use empty symbol map to avoid repeated loads
                  return ImmutableMap.of();
                }
              }
            });

  /**
   * Constructor, which takes a <code>symbolMaps</code> directory as its
   * argument. Symbol maps can be generated using the <code>-extra</code> GWT
   * compiler argument.
   */
  @Inject
  public StackTraceDeobfuscator(SymbolMapsDirectory symbolMapsDirectory) {
    setSymbolMapsDirectory(symbolMapsDirectory);
  }

  /**
   * Convenience method which resymbolizes an entire stack trace to extent
   * possible.
   *
   * @param st the stack trace to resymbolize
   * @param strongName the GWT permutation strong name
   * @return a best effort resymbolized stack trace
   */
  public StackTraceElement[] deobfuscateStackTrace(
      String[] st, String strongName) {
    StackTraceElement[] newSt = new StackTraceElement[st.length];
    for (int i = 0; i < st.length; i++) {
      newSt[i] = resymbolize(st[i], strongName);
    }
    return newSt;
  }

  /**
   * Best effort resymbolization of a a single stack trace element.
   *
   * @param ste the stack trace element to resymbolize
   * @param strongName the GWT permutation strong name
   * @return the best effort resymbolized stack trace element
   */
  public StackTraceElement resymbolize(String ste,
      String strongName) {
    Map<String, String> map = symbolMaps.get(strongName);
    String symbolData = map == null ? null : map.get(ste);

    if (symbolData != null) {
      // jsniIdent, className, memberName, sourceUri, sourceLine
      String[] parts =
          Lists.newArrayList(Splitter.on(",").split(symbolData)).toArray(new String[0]);
      if (parts.length == 5) {
        String[] ref = parse(
            parts[0].substring(0, parts[0].lastIndexOf(')') + 1));

        String declaringClass = parts[1];
        String methodName = parts[2];

        // parts[3] contains the source file URI or "Unknown"
        String filename = "Unknown".equals(parts[3]) ? null
            : parts[3].substring(parts[3].lastIndexOf('/') + 1);

        int lineNumber = 0; //ste.getLineNumber();
        /*
         * When lineNumber is zero, either because compiler.stackMode is not
         * emulated or compiler.emulatedStack.recordLineNumbers is false, use
         * the method declaration line number from the symbol map.
         */
        if (lineNumber == 0) {
          lineNumber = Integer.parseInt(parts[4]);
        }

        return new StackTraceElement(
            declaringClass, methodName, filename, lineNumber);
      }
    }
    return new StackTraceElement(
        "UNKNOWN_CLASS", ste, "UNKNOWN_FILE", 0);
  }

  public void setSymbolMapsDirectory(SymbolMapsDirectory symbolMapsDirectory) {
    // permutations are unique, no need to clear the symbolMaps hash map
    this.symbolMapsDirectory = symbolMapsDirectory;
  }

  /**
   * Extracts the declaring class and method name from a JSNI ref, or null if
   * the information cannot be extracted.
   *
   * @see com.google.gwt.dev.util.JsniRef
   * @param refString symbol map reference string
   * @return a string array contains the declaring class and method name, or
   *         null when the regex match fails
   */
  private String[] parse(String refString) {
    Matcher matcher = JSNI_REF_PATTERN.matcher(refString);
    if (!matcher.matches()) {
      return null;
    }
    String className = matcher.group(1);
    String memberName = matcher.group(2);
    String[] toReturn = new String[] {className, memberName};
    return toReturn;
  }
}
