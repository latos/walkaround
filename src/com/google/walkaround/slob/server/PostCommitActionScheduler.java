// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.walkaround.slob.server;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import com.google.walkaround.slob.server.handler.PostCommitTaskHandler;
import com.google.walkaround.slob.shared.SlobId;
import com.google.walkaround.slob.shared.SlobModel.ReadableSlob;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.appengine.MemcacheTable;

import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Schedules task queue tasks that invoke {@link PostCommitAction} in a
 * throttled manner.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class PostCommitActionScheduler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(PostCommitActionScheduler.class.getName());

  private final String MEMCACHE_TAG_PREFIX = "PostCommitActionPending-";

  private final Set<PostCommitAction> actions;
  private final Queue postCommitActionQueue;
  private final int postCommitActionIntervalMillis;
  private final MemcacheTable<SlobId, Boolean> postCommitActionPending;
  private final String rootEntityKind;
  private final String taskUrl;
  private final Random random;
  // We treat the cache-clearing InternalPostCommitAction specially since we
  // want it to always run first; otherwise, it could be preempted by
  // user-defined PostCommitActions that always crash.
  private final SlobStoreImpl.InternalPostCommitAction internalPostCommit;

  @Inject
  public PostCommitActionScheduler(Set<PostCommitAction> actions,
      @PostCommitActionQueue Queue postCommitActionQueue,
      @PostCommitActionIntervalMillis int postCommitActionIntervalMillis,
      MemcacheTable.Factory memcacheFactory,
      @SlobRootEntityKind String rootEntityKind,
      @PostCommitTaskUrl String taskUrl,
      Random random,
      SlobStoreImpl.InternalPostCommitAction internalPostCommit) {
    this.actions = actions;
    this.postCommitActionQueue = postCommitActionQueue;
    this.postCommitActionIntervalMillis = postCommitActionIntervalMillis;
    this.postCommitActionPending = memcacheFactory.create(MEMCACHE_TAG_PREFIX + rootEntityKind);
    this.rootEntityKind = rootEntityKind;
    this.taskUrl = taskUrl;
    this.random = random;
    this.internalPostCommit = internalPostCommit;
  }

  public void preCommit(CheckedTransaction tx, SlobId slobId)
      throws PermanentFailure, RetryableFailure {
    // Can't short-circuit if actions.isEmpty() because we always have internalPostCommit.
    if (postCommitActionPending.get(slobId) == Boolean.TRUE) {
      log.info("Post-commit actions pending on " + slobId
          + " (" + rootEntityKind + "), not scheduling task");
      return;
    }
    int delayMillis = postCommitActionIntervalMillis == 0 ? 0
        : Ints.saturatedCast(random.nextInt(postCommitActionIntervalMillis)
            + postCommitActionIntervalMillis / 2L);
    log.info("Scheduling post-commit actions on " + slobId
        + " (" + rootEntityKind + "), in " + delayMillis + "ms");
    tx.enqueueTask(postCommitActionQueue,
        TaskOptions.Builder.withUrl(taskUrl)
        .param(PostCommitTaskHandler.STORE_TYPE_PARAM, rootEntityKind)
        .param(PostCommitTaskHandler.SLOB_ID_PARAM, slobId.getId())
        .countdownMillis(delayMillis));
  }

  private Iterable<PostCommitAction> getActions() {
    return Iterables.concat(ImmutableList.of(internalPostCommit), actions);
  }

  public void postCommit(SlobId slobId, long resultingVersion, ReadableSlob resultingState) {
    // Since the memcache entry is overwritten by a task queue task, we only
    // need this expiration to protect from admins deleting tasks and similar
    // situations.  We use a duration much larger than
    // postCommitActionIntervalMillis since we do want to prevent further tasks
    // from being scheduled as long as our task is still expected to run (it
    // might have been delayed by the task queue).
    postCommitActionPending.put(slobId, true,
        Expiration.byDeltaMillis(Ints.saturatedCast(100L * postCommitActionIntervalMillis)));
    for (PostCommitAction action : getActions()) {
      log.info("Running immediate post-commit action " + action
          + " on " + slobId + " (" + rootEntityKind + ")");
      action.unreliableImmediatePostCommit(slobId, resultingVersion, resultingState);
    }
  }

  public void taskInvoked(SlobId slobId) {
    postCommitActionPending.delete(slobId);
    for (PostCommitAction action : getActions()) {
      log.info("Running reliable post-commit action " + action
          + " on " + slobId + " (" + rootEntityKind + ")");
      action.reliableDelayedPostCommit(slobId);
    }
  }

}
