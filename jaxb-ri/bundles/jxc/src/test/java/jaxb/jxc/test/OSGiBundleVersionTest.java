/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jaxb.jxc.test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests if MANIFEST Bundle-Version has correct version format.
 *
 * @author Martin Vojtek
 */
public class OSGiBundleVersionTest {

    @Test
    public void testJaxbOsgiBundleVersion() throws IOException {
        String osgiJar = System.getProperty("osgi.dist");
        assertNotNull(osgiJar, "osgi.dist not set");
        checkVersion(new File(osgiJar + ".jar"));
    }

    private void checkVersion(File f) throws IOException {
        System.out.println("Checking: " + f.getAbsolutePath());
        Manifest mf = new JarFile(f).getManifest();
        String version = mf.getMainAttributes().getValue("Bundle-Version");
        assertNotNull(version);
        String[] v = version.split("\\.");
        assertTrue(v.length <= 4, "only <X.Y.Z> or <X.Y.Z.SNAPSHOT> is allowed but was: <" + version + ">");
        for (int i = 0; i < (4 == v.length ? v.length - 1 : v.length); i++) {
            try {
                Integer.parseInt(v[i]);
            } catch (Throwable t) {
                fail("'" + v[i] + "' is not a number");
            }
        }
    }

}
