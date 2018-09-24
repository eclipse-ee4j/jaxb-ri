/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jaxb.osgi_test;

import org.apache.felix.ipojo.junit4osgi.OSGiTestCase;
import org.osgi.framework.Bundle;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * junit4osgi tests helper
 *
 * @author yaroska
 */
public abstract class JaxbOsgiTest extends OSGiTestCase {

    protected void checkBundle(Bundle bundle) {
        assertEquals("Bundle " + bundle.getSymbolicName() + " isn't active", bundle.getState(), Bundle.ACTIVE);
    }

    protected void checkClassInBundle(String className, Bundle bundle) {
        try {
            Class<?> clazz = bundle.loadClass(className);
            if (clazz == null) {
                throw new NullPointerException("Class " + className + " is null");
            }
            assertNotNull("Can't load class: " + className, clazz);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            fail("Cannot find and load class: " + className + " from the bundle " + bundle.getSymbolicName());
        }
    }
}
