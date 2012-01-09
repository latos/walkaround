// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.walkaround.slob.server;

import com.google.appengine.api.datastore.Key;
import com.google.walkaround.slob.server.MutationLog.MutationLogFactory;
import com.google.walkaround.slob.shared.SlobId;

/**
 * Offers access to various classes that operate on a slob type.
 *
 * @author ohler@google.com (Christian Ohler)
 */
public interface SlobFacilities {

  SlobStore getSlobStore();
  MutationLogFactory getMutationLogFactory();
  LocalMutationProcessor getLocalMutationProcessor();

  String getRootEntityKind();

  Key makeRootEntityKey(SlobId slobId);
  SlobId parseRootEntityKey(Key key);

  PostCommitActionScheduler getPostCommitActionScheduler();

}
