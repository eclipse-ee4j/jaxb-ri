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
import org.junit.jupiter.api.TestInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yan GAO (gaoyan.gao@oracle.com)
 */
public abstract class SchemaAntTaskTestBase {
    protected File projectDir;
    protected File srcDir;
    protected File buildDir;
    protected File script;
    protected boolean tryDelete = false;

    public abstract String getBuildScript();

    @BeforeEach
    protected void setUp(TestInfo info) throws Exception {
        projectDir = new File(System.getProperty("java.io.tmpdir"), getClass().getSimpleName() + "-" + info.getDisplayName());
        if (projectDir.exists() && projectDir.isDirectory()) {
            delDir(projectDir);
        }
        srcDir = new File(projectDir, "src");
        buildDir = new File(projectDir, "build");
        assertTrue(projectDir.mkdirs(), "project dir created");
        script = copy(projectDir, getBuildScript(),
            SchemaAntTaskTestBase.class.getResourceAsStream("resources/" + getBuildScript()));
    }

    @AfterEach
    protected void tearDown() throws Exception {
        if (tryDelete) {
            delDir(srcDir);
            delDir(buildDir);
            script.delete();
            assertTrue(projectDir.delete(), "project dir exists");
        }
    }

    protected static File copy(File dest, String name, InputStream is) throws IOException {
        return copy(dest, name, is, null);
    }

    protected static File copy(File dest, String name, InputStream is, String targetEncoding)
        throws IOException {
        File destFile = new File(dest, name);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(destFile));
        Writer w = targetEncoding != null ?
            new OutputStreamWriter(os, targetEncoding) : new OutputStreamWriter(os);
        byte[] b = new byte[4096];
        int len;
        while ((len = is.read(b)) > 0) {
            w.write(new String(b), 0, len);
        }
        w.flush();
        w.close();
        is.close();
        return destFile;
    }

    static boolean is9() {
        return System.getProperty("java.version").startsWith("9");
    }

    public static void delDir(File dir) {
        if (!dir.exists()) {
            return;
        }
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                delDir(f);
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }
}
