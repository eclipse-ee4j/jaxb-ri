/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Hosts optimized
 * {@link org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor},
 * {@link org.glassfish.jaxb.runtime.v2.runtime.reflect.TransducedAccessor}, and {@link org.glassfish.jaxb.runtime.v2.runtime.Transducer}.
 *
 * <h2>How it works</h2>
 * <p>
 * Most of the classes in this package are "templates." At run-time, A template class file is slightly modified to match
 * the target Java Bean, then it will be loaded into the VM.
 */
package org.glassfish.jaxb.runtime.v2.runtime.reflect.opt;
