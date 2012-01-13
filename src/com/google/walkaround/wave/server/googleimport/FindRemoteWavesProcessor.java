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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import com.google.walkaround.proto.FindRemoteWavesTask;
import com.google.walkaround.proto.ImportTaskPayload;
import com.google.walkaround.proto.RobotSearchDigest;
import com.google.walkaround.proto.gson.FindRemoteWavesTaskGsonImpl;
import com.google.walkaround.proto.gson.ImportTaskPayloadGsonImpl;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.shared.Assert;
import com.google.walkaround.wave.server.auth.StableUserId;
import com.google.walkaround.wave.server.gxp.SourceInstance;

import org.joda.time.LocalDate;
import org.waveprotocol.wave.model.id.IdUtil;
import org.waveprotocol.wave.model.id.WaveId;
import org.waveprotocol.wave.model.id.WaveletId;
import org.waveprotocol.wave.model.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Processes a {@link FindRemoteWavesTask}.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class FindRemoteWavesProcessor {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(FindRemoteWavesProcessor.class.getName());

  private final RobotApi.Factory robotApiFactory;
  private final SourceInstance.Factory sourceInstanceFactory;
  private final StableUserId userId;
  private final PerUserTable perUserTable;
  private final CheckedDatastore datastore;
  private final Random random;

  @Inject
  public FindRemoteWavesProcessor(RobotApi.Factory robotApiFactory,
      SourceInstance.Factory sourceInstanceFactory,
      StableUserId userId,
      PerUserTable perUserTable,
      CheckedDatastore datastore,
      Random random) {
    this.robotApiFactory = robotApiFactory;
    this.sourceInstanceFactory = sourceInstanceFactory;
    this.userId = userId;
    this.perUserTable = perUserTable;
    this.datastore = datastore;
    this.random = random;
  }

  // This used to be 300 but has been raised.  Some of the comments elsewhere in
  // the code probably still assume 300.
  private static final int MAX_RESULTS = 10000;

  private String getQueryDateRestriction(String facet, long dateDays) {
    LocalDate date = DaysSinceEpoch.toLocalDate(dateDays);
    return String.format("%s:%04d/%02d/%02d", facet,
        date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
  }

  private List<RobotSearchDigest> searchBetween(SourceInstance instance,
      long onOrAfterDays, long beforeDays) throws IOException {
    RobotApi api = robotApiFactory.create(instance.getApiUrl());
    String query = getQueryDateRestriction("after", onOrAfterDays)
        // The "before" search operator is inclusive (i.e., it means before the
        // end of the day); beforeDays is exclusive.
        + " " + getQueryDateRestriction("before", beforeDays - 1);
    return api.search(query, 0, MAX_RESULTS);
  }

  private long randomBetween(long min, long limit) {
    return min + random.nextInt(Ints.checkedCast(limit - min));
  }

  private List<Pair<Long, Long>> splitInterval(long onOrAfterDays, long beforeDays) {
    Preconditions.checkArgument(onOrAfterDays < beforeDays - 1,
        "Interval invalid or too small to split further: %s, %s", onOrAfterDays, beforeDays);
    // Split into roughly 5 intervals (if possible) because we want a high
    // branching factor (300*5^n reaches 1000, 10000 etc. quite a bit faster
    // than 300*2^n) and the maximum number of tasks GAE lets us add in one
    // transaction is 5.
    //
    // TreeSet for iteration order.
    Set<Long> splitPoints = Sets.newTreeSet();
    for (int i = 0; i < 4; i++) {
      // NOTE(ohler): Randomized strategy because it's simple to implement (the
      // cases where beforeDays - onOrAfterDays < 5 would require some thought
      // otherwise) and to make it unlikely that repeated runs send the same
      // queries to the googlewave.com servers, which seem to have a bug where
      // the result list is sometimes truncated for a query that has been issued
      // previously with a lower maxResults limit (perhaps some incorrect
      // caching).  Randomization means that re-running the "find waves" step
      // several times might have a greater chance to discover all waves.  But
      // I'm not positive whether this helps since I don't understand the bug.
      //
      // Other options include:
      //
      // * Instead of this interval splitting, start with "folder:3" or
      //   "before:2013/01/01" (for all waves), then do "before:<date of oldest
      //   wave returned by previous search>" until no more waves are returned.
      //   However, this relies on the assumption that truncated result lists
      //   are always truncated in such a way that only old waves are missing,
      //   not new waves.  We'd have to verify this.  Also, it's completely
      //   sequential rather than parallelizable.
      //
      // * Follow up every search for "after:A before:B" with another a search
      //   for "after:A before:<date of oldest wave returned by previous
      //   search>".  This could be a good combination of the two but relies on
      //   the same assumption and adds quite a bit more code.
      //
      // * When the user triggers the "find remote waves" task, enqueue N of
      //   them rather than just one, to cover the search space N times with
      //   different random interval splits to improve the likelihood that we
      //   find everything.  Could be good as well but adds code.
      //
      // * Add random negative search terms like -dgzimhmcoblhqfjciezc to the
      //   query that are unlikely to restrict the result set but make the query
      //   unique to avoid the poisoned caches.  Could also do many different
      //   such searches and merge the result sets.  (Can't assert that they are
      //   the same since waves may have been modified and fallen out of the
      //   date range.)  Probably worth implementing.
      //
      // * Fix the bug in googlewave.com or demonstrate that it's not
      //   reproducible.  Unlikely to happen since it's harder than any of these
      //   workarounds.
      splitPoints.add(randomBetween(onOrAfterDays + 1, beforeDays));
    }
    splitPoints.add(beforeDays);
    ImmutableList.Builder<Pair<Long, Long>> out = ImmutableList.builder();
    long left = onOrAfterDays;
    for (long right : splitPoints) {
      Assert.check(left < right, "left=%s, right=%s", left, right);
      out.add(Pair.of(left, right));
      left = right;
    }
    return out.build();
  }

  private void enqueueTasks(SourceInstance instance, List<Pair<Long, Long>> intervals)
      throws PermanentFailure {
    log.info("intervals: " + intervals);
    ImmutableList.Builder<ImportTaskPayload> accu = ImmutableList.builder();
    for (Pair<Long, Long> interval : intervals) {
      FindRemoteWavesTask task = new FindRemoteWavesTaskGsonImpl();
      task.setInstance(instance.serialize());
      task.setOnOrAfterDays(interval.getFirst());
      task.setBeforeDays(interval.getSecond());
      ImportTaskPayload payload = new ImportTaskPayloadGsonImpl();
      payload.setFindWavesTask(task);
      accu.add(payload);
    }
    final List<ImportTaskPayload> payloads = accu.build();
    new RetryHelper().run(
        new RetryHelper.VoidBody() {
          @Override public void run() throws RetryableFailure, PermanentFailure {
            CheckedTransaction tx = datastore.beginTransaction();
            try {
              for (ImportTaskPayload payload : payloads) {
                perUserTable.addTask(tx, userId, payload);
              }
              tx.commit();
            } finally {
              tx.close();
            }
          }
        });
    log.info("Successfully scheduled " + intervals.size() + " tasks");
  }

  public void enqueueRandomTasksForInterval(SourceInstance instance, long
      onOrAfterDays, long beforeDays) throws PermanentFailure {
    if (onOrAfterDays == beforeDays - 1) {
      enqueueTasks(instance, ImmutableList.of(Pair.of(onOrAfterDays, beforeDays)));
    } else {
      enqueueTasks(instance, splitInterval(onOrAfterDays, beforeDays));
    }
  }

  // Transaction limit is 500 entities but let's stay well below that.
  private static final int MAX_WAVELETS_PER_TRANSACTION = 300;

  private void storeResults(List<RemoteConvWavelet> results) throws PermanentFailure {
    for (final List<RemoteConvWavelet> partition
        : Iterables.partition(results, MAX_WAVELETS_PER_TRANSACTION)) {
      new RetryHelper().run(
          new RetryHelper.VoidBody() {
            @Override public void run() throws RetryableFailure, PermanentFailure {
              CheckedTransaction tx = datastore.beginTransaction();
              try {
                if (perUserTable.addRemoteWavelets(tx, userId, partition)) {
                  tx.commit();
                }
              } finally {
                tx.close();
              }
            }
          });
    }
    log.info("Successfully added " + results.size() + " remote waves");
  }

  private List<RemoteConvWavelet> expandPrivateReplies(SourceInstance instance,
      List<RobotSearchDigest> digests) throws IOException {
    RobotApi api = robotApiFactory.create(instance.getApiUrl());
    ImmutableList.Builder<RemoteConvWavelet> wavelets = ImmutableList.builder();
    for (RobotSearchDigest digest : digests) {
      WaveId waveId = WaveId.deserialise(digest.getWaveId());
      // The robot API only allows access to waves with ids that start with "w".
      if (!waveId.getId().startsWith(IdUtil.WAVE_PREFIX + "+")) {
        log.info("Wave " + waveId + " not accessible through Robot API, skipping");
      } else {
        log.info("Getting wave view for " + waveId);
        List<WaveletId> waveletIds = api.getWaveView(waveId);
        log.info("Wave view for " + waveId + ": " + waveletIds);
        for (WaveletId waveletId : waveletIds) {
          if (IdUtil.isConversationalId(waveletId)) {
            wavelets.add(new RemoteConvWavelet(instance, digest, waveletId, null, null));
          } else {
            log.info("Skipping non-conv wavelet " + waveletId);
          }
        }
      }
    }
    return wavelets.build();
  }

  public void findWaves(FindRemoteWavesTask task) throws IOException, PermanentFailure {
    SourceInstance instance = sourceInstanceFactory.parseUnchecked(task.getInstance());
    long onOrAfterDays = task.getOnOrAfterDays();
    long beforeDays = task.getBeforeDays();
    List<RobotSearchDigest> results = searchBetween(instance, onOrAfterDays, beforeDays);
    if (results.isEmpty()) {
      return;
    }
    storeResults(expandPrivateReplies(instance, results));
    if (results.size() >= MAX_RESULTS) {
      // Result list is most likely truncated, repeat with smaller intervals.
      log.info("Got " + results.size() + " results between "  + onOrAfterDays + " and " + beforeDays
          + ", splitting");
      if (beforeDays - onOrAfterDays <= 1) {
        throw new RuntimeException("Can't split further; too many results (" + results.size()
            + ") between " + onOrAfterDays + " and " + beforeDays);
      }
      enqueueRandomTasksForInterval(instance, onOrAfterDays, beforeDays);
    }
  }

}
