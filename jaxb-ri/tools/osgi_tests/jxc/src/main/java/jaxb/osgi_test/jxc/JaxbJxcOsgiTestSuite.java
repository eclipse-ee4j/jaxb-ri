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

import junit.framework.Test;
import org.apache.felix.ipojo.junit4osgi.OSGiTestSuite;
import org.osgi.framework.BundleContext;

/**
 * Test suite to run OSGi test
 *
 * @author yaroska
 */
public class JaxbJxcOsgiTestSuite {

    public static Test suite(BundleContext bc) {
        OSGiTestSuite suite = new OSGiTestSuite(JaxbJxcOsgiTestSuite.class.getName(), bc);
        suite.addTestSuite(JaxbJxcOsgiTest.class);
        return suite;
    }
}
