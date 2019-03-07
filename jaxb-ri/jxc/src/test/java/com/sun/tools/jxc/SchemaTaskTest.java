/*
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Yan GAO (gaoyan.gao@oracle.com)
 */
public class SchemaTaskTest extends SchemaAntTaskTestBase {

    private File pkg;
    private File metainf;

    @Override
    public String getBuildScript() {
        return "schemagen.xml";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pkg = new File(srcDir, "test");
        metainf = new File(buildDir, "META-INF");
        assertTrue(pkg.mkdirs());
        assertTrue(metainf.mkdirs());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFork() throws FileNotFoundException, IOException {
        copy(pkg, "MyTrackingOrder.java", SchemaTaskTest.class.getResourceAsStream("resources/MyTrackingOrder.java_"));
        assertEquals(0, AntExecutor.exec(script, "schemagen-fork"));
    }

    public void testAddmodules() throws IOException {
        copy(pkg, "MyTrackingOrder.java", SchemaTaskTest.class.getResourceAsStream("resources/MyTrackingOrder.java_"));
        assertEquals(0, AntExecutor.exec(script, "schemagen-addmodules"));
    }
}
