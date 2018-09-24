/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jaxb.osgi_test.core;

import jaxb.osgi_test.JaxbOsgiTest;
import org.osgi.framework.Bundle;

/**
 * Testing jaxb-core osgi jar
 *
 * @author yaroska
 */
public class JaxbCoreOsgiTest extends JaxbOsgiTest {

    private static final String BUNDLE = "com.sun.xml.bind.jaxb-core";

    private Bundle bundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bundle = getBundle(BUNDLE);
    }

    public void testBundle() {
        checkBundle(bundle);
    }

    public void testClassFactory() {
        checkClassInBundle("com.sun.xml.bind.v2.ClassFactory", bundle);
    }

    public void testBuilder() {
        checkClassInBundle("com.sun.istack.Builder", bundle);
    }

    public void testTxwDocument() {
        checkClassInBundle("com.sun.xml.txw2.Document", bundle);
    }
}
