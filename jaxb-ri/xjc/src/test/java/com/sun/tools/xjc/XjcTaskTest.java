/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Yan GAO (gaoyan.gao@oracle.com)
 */
public class XjcTaskTest extends XjcAntTaskTestBase {
    private File schema;
    private File pkg;
    private File metainf;

    @Override
    public String getBuildScript() {
        return "xjc.xml";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pkg = new File(srcDir, "test");
        metainf = new File(buildDir, "META-INF");
        schema = copy(projectDir, "simple.xsd", XjcTaskTest.class.getResourceAsStream("resources/simple.xsd"));
        assertTrue(pkg.mkdirs());
        assertTrue(metainf.mkdirs());
    }

    @Override
    protected void tearDown() throws Exception {
        if (tryDelete) {
            schema.delete();
        }
        super.tearDown();
    }

    public void testFork() throws FileNotFoundException, IOException {
        if (is9()){
           assertEquals(0, AntExecutor.exec(script, "xjc-fork"));
        }
    }

    public void testWithoutFork() throws IOException {
        assertEquals(0, AntExecutor.exec(script, "xjc-no-fork"));
    }

    public void testAddmodules() throws IOException {
        if (is9()){
            assertEquals(0, AntExecutor.exec(script, "xjc-addmodules"));
        }
    }
}
