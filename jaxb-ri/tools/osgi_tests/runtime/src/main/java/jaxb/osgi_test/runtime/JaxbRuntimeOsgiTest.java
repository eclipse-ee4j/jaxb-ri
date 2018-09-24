/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jaxb.osgi_test.runtime;

import jaxb.osgi_test.JaxbOsgiTest;
import org.osgi.framework.Bundle;

/**
 * Testing jaxb-runtime osgi jar
 *
 * @author yaroska
 */
public class JaxbRuntimeOsgiTest extends JaxbOsgiTest {
    private static final String BUNDLE = "com.sun.xml.bind.jaxb-impl";

    private Bundle bundle;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bundle = getBundle(BUNDLE);
    }

    public void testBundle() {
        checkBundle(bundle);
    }

    public void testAccessorFactory() {
        checkClassInBundle("com.sun.xml.bind.AccessorFactory", bundle);
    }

    public void testJaxbRiContext() {
        checkClassInBundle("com.sun.xml.bind.api.JAXBRIContext", bundle);
    }

    public void testNamespacePrefixMapper() {
        checkClassInBundle("com.sun.xml.bind.marshaller.NamespacePrefixMapper", bundle);
    }

    public void testPatcher() {
        checkClassInBundle("com.sun.xml.bind.unmarshaller.Patcher", bundle);
    }

    public void testAttributesImpl() {
        checkClassInBundle("com.sun.xml.bind.util.AttributesImpl", bundle);
    }

    public void testContextFactory() {
        checkClassInBundle("com.sun.xml.bind.v2.ContextFactory", bundle);
    }
}
