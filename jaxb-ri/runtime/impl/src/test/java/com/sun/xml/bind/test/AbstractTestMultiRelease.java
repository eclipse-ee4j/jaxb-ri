/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.sun.xml.bind.test;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Tools for testing multi-release jar, makes possible to skip tests marked for java version above current one.
 *
 * @author Daniel Kec
 * @see SinceJava9
 */
public abstract class AbstractTestMultiRelease {

    @Rule
    public TestWatcher javaVersionWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            if (description.getAnnotation(SinceJava9.class) != null) {
                if (!isAboveOrEqualJava9()) {
                    Assume.assumeTrue("Skipping, test is applicable since java 9 and higher.", false);
                }
            }
            super.starting(description);
        }
    };

    protected boolean isAboveOrEqualJava9() {
        try {
            //Runtime#version is available since java 9
            Runtime.class.getMethod("version");
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }
}
