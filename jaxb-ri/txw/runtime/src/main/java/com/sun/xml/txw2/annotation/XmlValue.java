/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the invocation of the method will produce a text
 *
 * <p>
 * The method signature has to match the form {@code R foo(DT1,DT2,..)}
 *
 * <p>
 * R is either {@code void} or the type to which the interface that declares
 * this method is assignable. In the case of the latter, the method will return
 * {@code this} object, allowing you to chain the multiple method
 * invocations like {@link StringBuffer}.
 *
 * <p>
 * DTi must be datatype objects.
 *
 * <p>
 * When this method is called, whitespace-separated text data
 * is added from each of the datatype objects.
 *
 * @author Kohsuke Kawaguchi
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface XmlValue {
}
