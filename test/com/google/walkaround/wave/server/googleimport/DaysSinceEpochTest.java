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

package com.google.walkaround.wave.server.googleimport;

import com.google.common.collect.ImmutableList;

import junit.framework.TestCase;

import org.joda.time.LocalDate;
import org.waveprotocol.wave.model.util.Pair;

import java.util.List;

/**
 * @author ohler@google.com (Christian Ohler)
 */
public class DaysSinceEpochTest extends TestCase {

  // Test that the functions (restricted to the date interval we care about)
  // convert back and forth, and that iterating over days covers the same
  // sequence as incrementing a LocalDate.
  public void testStuff() throws Exception {
    long min = DaysSinceEpoch.fromLocalDate(new LocalDate(2008, 1, 1));
    long max = DaysSinceEpoch.fromLocalDate(new LocalDate(2013, 1, 1));
    assertTrue(min < max);
    LocalDate expected = new LocalDate(2008, 1, 1);
    for (long i = min; i <= max; i++) {
      LocalDate date = DaysSinceEpoch.toLocalDate(i);
      assertEquals(expected, date);
      assertEquals(i, DaysSinceEpoch.fromLocalDate(date));
      expected = expected.plusDays(1);
      assertFalse(date.equals(expected));
    }
  }

  private Pair<Long, LocalDate> entry(long days, int y, int m, int d) {
    return Pair.of(days, new LocalDate(y, m, d));
  }

  // Compares a few values with data calculated by hand.
  public void testIndividualValues() throws Exception {
    List<Pair<Long, LocalDate>> testData = ImmutableList.of(
        entry(13879, 2008, 1, 1),
        entry(13880, 2008, 1, 2),
        entry(13881, 2008, 1, 3),
        // Check dates around leap second.
        entry(14244, 2008, 12, 31),
        entry(14245, 2009, 1, 1),

        entry(15706, 2013, 1, 1));
    for (Pair<Long, LocalDate> entry : testData) {
      long a = entry.getFirst();
      LocalDate b = entry.getSecond();
      assertEquals(a, DaysSinceEpoch.fromLocalDate(b));
      assertEquals(b, DaysSinceEpoch.toLocalDate(a));
    }
  }

}
