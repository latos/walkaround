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

import com.google.appengine.api.taskqueue.Queue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.walkaround.util.server.RetryHelper;
import com.google.walkaround.util.server.RetryHelper.PermanentFailure;
import com.google.walkaround.util.server.RetryHelper.RetryableFailure;
import com.google.walkaround.util.server.appengine.CheckedDatastore;
import com.google.walkaround.util.server.appengine.CheckedDatastore.CheckedTransaction;
import com.google.walkaround.util.server.servlet.AbstractHandler;
import com.google.walkaround.util.server.servlet.BadRequestException;
import com.google.walkaround.util.server.servlet.TryAgainLaterException;
import com.google.walkaround.wave.server.auth.AccountStore;
import com.google.walkaround.wave.server.auth.ServletAuthHelper;
import com.google.walkaround.wave.server.auth.StableUserId;
import com.google.walkaround.wave.server.auth.UserContext;

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles task queue callbacks for import tasks.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public class ImportTaskHandler extends AbstractHandler {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ImportTaskHandler.class.getName());

  public static final String USER_ID_HEADER = "X-Walkaround-Import-User-Id";
  public static final String TASK_ID_HEADER = "X-Walkaround-Import-Task-Id";

  @Inject AccountStore accountStore;
  @Inject UserContext userContext;
  @Inject ServletAuthHelper authHelper;
  @Inject PerUserTable perUserTable;
  @Inject CheckedDatastore datastore;
  @Inject @ImportTaskQueue Queue taskQueue;
  // Provider because we need to set up the UserContext before this can be instantiated.
  @Inject Provider<TaskDispatcher> taskDispatcher;

  private ImportTask readTask(StableUserId userId, long taskId) throws IOException {
    try {
      CheckedTransaction tx = datastore.beginTransaction();
      try {
        return perUserTable.getTask(tx, userId, taskId);
      } finally {
        tx.rollback();
      }
    } catch (RetryableFailure e) {
      // If we can't even read the task, let the task queue retry rather than
      // doing it here.  Seems better because it means we don't use waste a
      // thread for waiting.
      throw new IOException("PermanentFailure reading task: " + userId + ", " + taskId, e);
    } catch (PermanentFailure e) {
      throw new IOException("PermanentFailure reading task: " + userId + ", " + taskId, e);
    }
  }

  private void deleteTask(final StableUserId userId, final long taskId) throws PermanentFailure {
    log.info("deleteTask(" + userId + ", " + taskId + ")");
    new RetryHelper().run(
        new RetryHelper.VoidBody() {
          @Override public void run() throws RetryableFailure, PermanentFailure {
            CheckedTransaction tx = datastore.beginTransaction();
            try {
              perUserTable.deleteTask(tx, userId, taskId);
              tx.commit();
            } finally {
              tx.close();
            }
          }
        });
  }

  private void handleTask(StableUserId userId, long taskId) throws IOException {
    ImportTask taskToProcess = readTask(userId, taskId);
    if (taskToProcess == null) {
      log.info("Task is gone from datastore; either already completed or cancelled");
      return;
    }
    log.info("Task to process: " + taskToProcess);
    taskDispatcher.get().processTask(taskToProcess);
    // If the processing returns normally, the task has been completed.  (Other
    // tasks may have been scheduled.)
    try {
      deleteTask(userId, taskId);
    } catch (PermanentFailure e) {
      // Ugh, all the real work is done but then we failed to delete the task
      // entity.  We'll have to do the task all over again...
      throw new IOException("PermanentFailure deleting completed task: " + userId + " " + taskId,
          e);
    }
  }

  @Override
  public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException, ServletException {
    log.info("Received task: queue " + req.getHeader("X-AppEngine-QueueName")
        + ", task name " + req.getHeader("X-AppEngine-TaskName")
        + ", retry count " + req.getHeader("X-AppEngine-TaskRetryCount"));
    final StableUserId userId = new StableUserId(requireParameter(req, USER_ID_HEADER));
    final long taskId;
    try {
      taskId = Long.parseLong(requireParameter(req, TASK_ID_HEADER));
    } catch (NumberFormatException e) {
      throw new BadRequestException("Bad task id");
    }
    log.info("userId=" + userId + ", taskId=" + taskId);

    authHelper.serve(
        new ServletAuthHelper.ServletBody() {
          @Override public void run() throws IOException {
            handleTask(userId, taskId);
          }
        },
        new ServletAuthHelper.AccountLookup() {
          @Override @Nullable public AccountStore.Record getAccount()
              throws PermanentFailure, IOException {
            return accountStore.get(userId);
          }
        },
        new ServletAuthHelper.NeedNewOAuthTokenHandler() {
          @Override public void sendNeedTokenResponse() throws IOException {
            // TODO(ohler): When loading /import (and probably /inbox as well),
            // check not just whether we have an OAuth token, but also whether
            // it's still valid.
            throw new TryAgainLaterException(
                "Need OAuth token.  Let's hope the user re-enables interactively: "
                + userId);
          }
      });
  }

}
