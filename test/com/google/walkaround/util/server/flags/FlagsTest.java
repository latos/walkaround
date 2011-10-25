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

package com.google.walkaround.util.server.flags;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;

import junit.framework.TestCase;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Map;

/**
 * @author danilatos@google.com (Daniel Danilatos)
 * @author ohler@google.com (Christian Ohler)
 */
public class FlagsTest extends TestCase {

  private static void failContains(String message, Object expected, Object actual) {
    failWithMessage(message, "expected: <" + expected + "> in: <" + actual
        + '>');
  }

  private static void failWithMessage(String userMessage, String ourMessage) {
    fail((userMessage == null)
        ? ourMessage
        : userMessage + ' ' + ourMessage);
  }

  static void assertContains(CharSequence expectedSubseq, String actual) {
    assertContains(null, expectedSubseq, actual);
  }

  static void assertContains(String message, CharSequence expectedSubseq, String actual) {
    if (!actual.contains(expectedSubseq)) {
      failContains(message, expectedSubseq, actual);
    }
  }

  enum TestEnum {
    A_VALUE,
    ANOTHER_VAL,
    MORE_STUFF;
  }

  enum TestFlag implements FlagDeclaration {
    STR(String.class),
    INT(Integer.class),
    DBL(Double.class),
    BOOL(Boolean.class),
    ENUM(TestEnum.class),
    ENUM2(TestEnum.class);

    private final String name;
    private final Class<?> type;

    private TestFlag(Class<?> type) {
      this.name = name().toLowerCase();
      this.type = type;
    }

    @Override public Annotation getAnnotation() {
      return new TestFlagImpl(this);
    }

    @Override public String getName() {
      return name;
    }

    @Override public Class<?> getType() {
      return type;
    }
  }

  @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
  private @interface FlagAnnotation {
    TestFlag value();
  }

  // Stolen from com.google.inject.name.NamedImpl.
  private static class TestFlagImpl implements FlagAnnotation {
    final TestFlag value;

    private TestFlagImpl(TestFlag value) {
      Preconditions.checkNotNull(value, "Null value");
      this.value = value;
    }

    @Override public TestFlag value() {
      return value;
    }

    @Override public int hashCode() {
      // This is specified in java.lang.Annotation.
      return 127 * "value".hashCode() ^ value.hashCode();
    }

    @Override public boolean equals(Object o) {
      if (!(o instanceof FlagAnnotation)) {
        return false;
      }
      FlagAnnotation other = (FlagAnnotation) o;
      return value.equals(other.value());
    }

    @Override public String toString() {
      return "@FlagAnnotation(value=" + value + ")";
    }

    @Override public Class<? extends Annotation> annotationType() {
      return FlagAnnotation.class;
    }
  }

  public void testJsonParsing() throws Exception {
    Map<FlagDeclaration, Object> parsed = JsonFlags.parse(Arrays.asList(TestFlag.values()),
        "{str:'abc',int:5,dbl:4.3,bool:true,enum:'more_stuff',enum2:'AnOThEr_VaL'}");
    Map<FlagDeclaration, Object> expected = ImmutableMap.<FlagDeclaration, Object>builder()
        .put(TestFlag.STR, "abc")
        .put(TestFlag.INT, 5)
        .put(TestFlag.DBL, 4.3)
        .put(TestFlag.BOOL, true)
        .put(TestFlag.ENUM, TestEnum.MORE_STUFF)
        .put(TestFlag.ENUM2, TestEnum.ANOTHER_VAL)
        .build();
    assertEquals(expected, parsed);

    assertEquals(ImmutableMap.<FlagDeclaration, Object>of(TestFlag.INT, 10),
        JsonFlags.parse(Arrays.asList(TestFlag.INT), "{int:0xa}"));
  }

  public void testJsonParsingErrors() {
    try {
      JsonFlags.parse(Arrays.asList(TestFlag.STR), "{str:null}");
      fail("Cannot have a null string flag");
    } catch (FlagFormatException e) {
      assertContains("Null value for key str", e.getMessage());
    }
    try {
      JsonFlags.parse(Arrays.asList(TestFlag.DBL), "{dbl:null}");
      fail("Cannot have a null double flag");
    } catch (FlagFormatException e) {
      assertContains("Null value for key dbl", e.getMessage());
    }
    try {
      JsonFlags.parse(Arrays.asList(TestFlag.ENUM), "{enum:'abc'}");
      fail("Invalid enum value not caught");
    } catch (FlagFormatException e) {
      assertContains("Invalid flag enum value ABC for key enum;"
          + " valid values: [A_VALUE, ANOTHER_VAL, MORE_STUFF]", e.getMessage());
    }
    try {
      JsonFlags.parse(Arrays.asList(TestFlag.ENUM2), "{enum2:null}");
      fail("Cannot have a null enum flag");
    } catch (FlagFormatException e) {
      assertContains("Null value for key enum2", e.getMessage());
    }
    try {
      JsonFlags.parse(Arrays.asList(TestFlag.INT), "{int:null}");
      fail("Cannot have a null int flag");
    } catch (FlagFormatException e) {
      assertContains("Null value for key int", e.getMessage());
    }
    try {
      JsonFlags.parse(Arrays.asList(TestFlag.STR), "{int:5}");
      fail("Cannot have a default flag value");
    } catch (FlagFormatException e) {
      assertContains("Missing flag: str", e.getMessage());
    }
    try {
      JsonFlags.parse(Arrays.asList(TestFlag.INT), "{int:4.3}");
      fail("Invalid integer not caught");
    } catch (FlagFormatException e) {
      assertContains("Loss of precision for type int, key=int, value=4.3", e.getMessage());
    }
  }

  private Object getFlag(Injector injector, FlagDeclaration decl) {
    return injector.getInstance(Key.get(decl.getType(), decl.getAnnotation()));
  }

  public void testBindings() throws Exception {
    final Map<FlagDeclaration, Object> map = Maps.newHashMap();
    Injector injector = Guice.createInjector(new AbstractModule() {
        @Override public void configure() {
          JsonFlags.bind(binder(), Arrays.asList(TestFlag.values()),
              new Provider<Map<FlagDeclaration, Object>>() {
                @Override public Map<FlagDeclaration, Object> get() {
                  return map;
                }
              });
        }
      });
    map.put(TestFlag.STR, "abc");
    map.put(TestFlag.INT, 5);
    map.put(TestFlag.DBL, 4.3);
    map.put(TestFlag.BOOL, true);
    map.put(TestFlag.ENUM, TestEnum.MORE_STUFF);
    map.put(TestFlag.ENUM2, TestEnum.ANOTHER_VAL);
    assertEquals("abc", getFlag(injector, TestFlag.STR));
    assertEquals(5, getFlag(injector, TestFlag.INT));
    assertEquals(4.3, getFlag(injector, TestFlag.DBL));
    assertEquals(true, getFlag(injector, TestFlag.BOOL));
    assertEquals(TestEnum.MORE_STUFF, getFlag(injector, TestFlag.ENUM));
    assertEquals(TestEnum.ANOTHER_VAL, getFlag(injector, TestFlag.ENUM2));
    map.put(TestFlag.BOOL, false);
    assertEquals(false, getFlag(injector, TestFlag.BOOL));
    assertEquals(4.3, getFlag(injector, TestFlag.DBL));
    map.put(TestFlag.DBL, 4.1);
    assertEquals(4.1, getFlag(injector, TestFlag.DBL));
  }

}
