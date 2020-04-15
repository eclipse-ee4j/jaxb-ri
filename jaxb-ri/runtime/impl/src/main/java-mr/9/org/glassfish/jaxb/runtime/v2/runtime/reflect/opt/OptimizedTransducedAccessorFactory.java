/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect.opt;

import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimePropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.TransducedAccessor;

import java.util.logging.Logger;

/**
 * Stub version of {@link org.glassfish.jaxb.runtime.v2.runtime.reflect.opt.OptimizedTransducedAccessorFactory} for java versions >= 9
 *
 * @author Daniel Kec
 */
public abstract class OptimizedTransducedAccessorFactory {
    private OptimizedTransducedAccessorFactory() {} // no instantiation please

    private static final Logger logger = Logger.getLogger(org.glassfish.jaxb.runtime.v2.runtime.reflect.opt.OptimizedTransducedAccessorFactory.class.getName());

    public static final TransducedAccessor get(RuntimePropertyInfo prop) {
        logger.finer("Optimization is not available since java 9");
        return null;
    }
}
