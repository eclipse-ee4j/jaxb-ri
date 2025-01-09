/*
 * Copyright (c) 2017, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @BeforeEach
    protected void setUp(TestInfo info) throws Exception {
        super.setUp(info);
        pkg = new File(srcDir, "test");
        metainf = new File(buildDir, "META-INF");
        assertTrue(pkg.mkdirs());
        assertTrue(metainf.mkdirs());
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testFork() throws IOException {
        copy(pkg, "MyTrackingOrder.java", SchemaTaskTest.class.getResourceAsStream("resources/MyTrackingOrder.java_"));
        assertEquals(0, AntExecutor.exec(script, "schemagen-fork"));
    }

    @Test
    public void testAddmodules() throws IOException {
        copy(pkg, "MyTrackingOrder.java", SchemaTaskTest.class.getResourceAsStream("resources/MyTrackingOrder.java_"));
        assertEquals(0, AntExecutor.exec(script, "schemagen-addmodules"));
    }
}
