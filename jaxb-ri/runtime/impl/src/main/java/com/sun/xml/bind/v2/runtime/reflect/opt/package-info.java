/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Hosts optimized
 * {@link com.sun.xml.bind.v2.runtime.reflect.Accessor},
 * {@link com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor}, and {@link com.sun.xml.bind.v2.runtime.Transducer}.
 *
 * <h2>How it works</h2>
 * <p>
 * Most of the classes in this package are "templates." At run-time, A template class file is slightly modified to match
 * the target Java Bean, then it will be loaded into the VM.
 */
package com.sun.xml.bind.v2.runtime.reflect.opt;
