/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.test.AbstractTestMultiRelease;
import com.sun.xml.bind.test.SinceJava9;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Test multi release for optimization feature,
 * Feature needs to be disabled since java 9.
 *
 * @author Daniel Kec
 */
public class OptimizationTestMultiRelease extends AbstractTestMultiRelease {

    @SuppressWarnings("WeakerAccess")
    public static String TEST_FIELD = "TEST_FIELD";

    @Test
    @SinceJava9
    public void testStubbedAccessorInjector() {
        //Stubbed version doesn't mind null param
        Assert.assertNull("Injector should always return null since Java 9",
                AccessorInjector.prepare(null, null, null, null));
    }

    @Test
    @SinceJava9
    public void testStubbedInjector() {
        //Stubbed version doesn't mind null param
        Assert.assertNull("Injector should always return null since Java 9",
                Injector.find(null, null));
    }

    @Test
    @SinceJava9
    public void testStubbedOptimizedAccessorFactory() throws NoSuchFieldException {
        Field field = this.getClass().getDeclaredField(TEST_FIELD);
        Assert.assertNull("OptimizedAccessorFactory should always return null since Java 9", OptimizedAccessorFactory.get(field));
    }

    @Test
    @SinceJava9
    @SuppressWarnings("ConstantConditions")
    public void testStubbedOptimizedTransducedAccessorFactory() throws NoSuchFieldException {
        final String errMessage = "OptimizedTransducedAccessorFactory should always return null since Java 9";
        try {
            //Stubbed version doesn't mind null param
            TransducedAccessor accessor = OptimizedTransducedAccessorFactory.get((RuntimePropertyInfo) null);
            Assert.assertNull(errMessage, accessor);
        } catch (NullPointerException npe) {
            Assert.fail(errMessage);
        }
    }

}
