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

import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;

import java.util.logging.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * Stub version of {@link org.glassfish.jaxb.runtime.v2.runtime.reflect.opt.OptimizedAccessorFactory} for java versions >= 9
 *
 * @author Daniel Kec
 */
public abstract class OptimizedAccessorFactory {
    private OptimizedAccessorFactory() {} // no instantiation please

    private static final Logger logger = Logger.getLogger(org.glassfish.jaxb.runtime.v2.runtime.reflect.opt.OptimizedAccessorFactory.class.getName());


    public static <B, V> Accessor<B, V> get(Method param1, Method param2) {
        return getStub();
    }

    public static <B, V> Accessor<B, V> get(Field param) {
        return getStub();
    }

    private static <B, V> Accessor<B, V> getStub(){
        logger.finer("Optimization is not available since java 9");
        return null;
    }

}
