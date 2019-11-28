/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

import java.util.logging.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.util.logging.Logger;

/**
 * Stub version of {@link OptimizedAccessorFactory} for java versions >= 9
 *
 * @author Daniel Kec
 */
public abstract class OptimizedAccessorFactory {
    private OptimizedAccessorFactory() {} // no instantiation please

    private static final Logger logger = Logger.getLogger(OptimizedAccessorFactory.class.getName());


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
