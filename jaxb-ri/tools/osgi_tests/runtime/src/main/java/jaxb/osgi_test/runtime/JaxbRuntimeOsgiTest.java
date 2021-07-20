/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
        checkClassInBundle("org.glassfish.jaxb.runtime.AccessorFactory", bundle);
    }

    public void testJaxbRiContext() {
        checkClassInBundle("org.glassfish.jaxb.runtime.api.JAXBRIContext", bundle);
    }

    public void testNamespacePrefixMapper() {
        checkClassInBundle("org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper", bundle);
    }

    public void testPatcher() {
        checkClassInBundle("org.glassfish.jaxb.runtime.unmarshaller.Patcher", bundle);
    }

    public void testAttributesImpl() {
        checkClassInBundle("org.glassfish.jaxb.runtime.util.AttributesImpl", bundle);
    }

    public void testContextFactory() {
        checkClassInBundle("org.glassfish.jaxb.runtime.v2.ContextFactory", bundle);
    }
}
