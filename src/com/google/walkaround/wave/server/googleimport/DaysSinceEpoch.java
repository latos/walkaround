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

import com.google.common.base.Preconditions;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

/**
 * Utilities for working with days since Unix epoch.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class DaysSinceEpoch {

  public static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

  private DaysSinceEpoch() {}

  public static long fromYMD(int y, int m, int d) {
    // Go through a LocalDate in the unverified hope that it will do
    // something smart about out-of-range arguments.
    return fromLocalDate(new LocalDate(y, m, d));
  }

  public static long fromLocalDate(LocalDate d) {
    return fromDateMidnight(d.toDateMidnight(DateTimeZone.UTC));
  }

  // This one is private since there is no guarantee that the DateMidnight is in
  // UTC time zone.
  private static long fromDateMidnight(DateMidnight d) {
    return fromMillis(d.getMillis());
  }

  public static long fromMillis(long millis) {
    // Note that Unix time ignores leap seconds.
    Preconditions.checkArgument(millis % MILLIS_PER_DAY == 0,
        "Not a multiple of %s: %s", MILLIS_PER_DAY, millis);
    return millis / MILLIS_PER_DAY;
  }

  public static DateMidnight toDateMidnightUTC(long daysSinceEpoch) {
    return new DateMidnight(daysSinceEpoch * MILLIS_PER_DAY, DateTimeZone.UTC);
  }

  public static LocalDate toLocalDate(long daysSinceEpoch) {
    return toDateMidnightUTC(daysSinceEpoch).toLocalDate();
  }

}
