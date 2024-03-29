/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * <p>
 * Compile-time representation of Java type system.
 *
 * <p>
 * These classes are used as TypeT and ClassDeclT of the model parameterization. This implementaion is designed to be
 * capable of representing pre-existing classes (such as java.lang.String) as well as the generated classes (represented
 * as JDefinedClass.)
 *
 * <h2>Handling of Primitive Types</h2>
 * <p>
 * Primitive types have two forms (int and Integer), and this complicates the binding process. For this reason, inside
 * the front end, we always use the boxed types. We'll use the unboxed form only in the back end when we know the field
 * doesn't need to represent the null value.
 */
package com.sun.tools.xjc.model.nav;
