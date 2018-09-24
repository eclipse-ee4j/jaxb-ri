/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jaxb.osgi_test.jxc;

import jaxb.osgi_test.JaxbOsgiTest;
import org.osgi.framework.Bundle;

/**
 * Testing jaxb-jxc osgi jar
 *
 * @author yaroska
 */
public class JaxbJxcOsgiTest extends JaxbOsgiTest {
    private static final String BUNDLE = "com.sun.xml.bind.jaxb-jxc";

    private Bundle bundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bundle = getBundle(BUNDLE);
    }

    public void testBundle() {
        checkBundle(bundle);
    }

    public void testSchemaGen() {
        checkClassInBundle("com.sun.tools.jxc.SchemaGenerator", bundle);
    }

    public void testJxc() {
        checkClassInBundle("com.sun.tools.jxc.api.JXC", bundle);
    }
}
