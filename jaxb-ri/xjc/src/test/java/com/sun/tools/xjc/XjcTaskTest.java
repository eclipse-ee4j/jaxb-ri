/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @BeforeEach
    @Override
    protected void setUp(TestInfo info) throws Exception {
        super.setUp(info);
        pkg = new File(srcDir, "test");
        metainf = new File(buildDir, "META-INF");
        schema = copy(projectDir, "simple.xsd", XjcTaskTest.class.getResourceAsStream("resources/simple.xsd"));
        assertTrue(pkg.mkdirs());
        assertTrue(metainf.mkdirs());
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception {
        if (tryDelete) {
            schema.delete();
        }
        super.tearDown();
    }

    @Test
    public void testFork() throws FileNotFoundException, IOException {
        if (is9()){
           assertEquals(0, AntExecutor.exec(script, "xjc-fork"));
        }
    }

    @Test
    public void testWithoutFork() throws IOException {
        assertEquals(0, AntExecutor.exec(script, "xjc-no-fork"));
    }

    @Test
    public void testAddmodules() throws IOException {
        if (is9()){
            assertEquals(0, AntExecutor.exec(script, "xjc-addmodules"));
        }
    }
}
