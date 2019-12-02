/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * <h2>Library for generating Java source code</h2>.
 *
 * <p>
 * CodeModel is a library that allows you to generate Java source
 * code in a type-safe fashion.
 *
 * <p>
 * With CodeModel, you build the java source code by first building AST,
 * then writing it out as text files that is Java source files.
 * The AST looks like this:
 *
 * <p>
 * You bulid this tree mostly from top-down. So, you first create
 * a new {@link JDefinedClass} from {@link JCodeModel}, then you
 * create a {@link JMethod} from {@link JDefinedClass}, and so on.
 *
 * <p>
 * This design brings the following benefits:
 *
 * <ul>
 *  <li>source code can be written in random order
 *  <li>generated source code nicely imports other classes
 *  <li>generated source code is lexically always correct
 *      (no unbalanced parenthesis, etc.)
 *  <li>code generation becomes relatively type-safe
 * </ul>
 *
 * The price you pay for that is
 * increased memory footprint and the generation speed.
 * See <a href="#performance">performance section</a> for
 * more discussions about the performance and possible improvements.
 *
 *
 * <h3>Using CodeModel</h3>
 * <p>
 * {@link com.sun.codemodel.JCodeModel} is the entry point to
 * the library. See its javadoc for more details about how to use
 * CodeModel.
 *
 *
 *
 * <h3>Performance</h3>
 * <p>
 * Generally speaking, CodeModel is expected to be used in
 * an environment where the resource constraint is not severe.
 * Therefore, we haven't spent much effort in trying to make
 * this library lean and mean.
 *
 * <p>
 * That said, we did some benchmark and performance analysis.
 * In case anyone is interested in making this library
 * better performance wise, here's the findings.
 *
 * <p>
 * {@link List}s {@link Map}s, and other collections take up
 * a lot of space. Allocating those things lazily is generally
 * a good idea.
 *
 * <p>
 * Compared to template-based code generator, the writing operation
 * is slow, as it needs to traverse each AST node. Consider
 * pre-encoding tokens (like 'public') to the target encoding,
 * and consider exploting the subtree equivalence.
 *
 */
package com.sun.codemodel;

import java.util.List;
import java.util.Map;
