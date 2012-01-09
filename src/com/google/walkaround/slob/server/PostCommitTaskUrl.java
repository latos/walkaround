// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.walkaround.slob.server;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Guice annotation for a String that contains the URL served by
 * {@link PostCommitActionTaskHandler}.
 *
 * @author ohler@google.com (Christian Ohler)
 */
@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface PostCommitTaskUrl {}
