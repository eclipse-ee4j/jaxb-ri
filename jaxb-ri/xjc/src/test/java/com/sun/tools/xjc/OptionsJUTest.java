/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.istack.tools.DefaultAuthenticator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author Lukas Jungmann
 */
public class OptionsJUTest extends TestCase {

    private Options o;

    public OptionsJUTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        o = new Options();
        o.targetDir = new File(System.getProperty("java.io.tmpdir"), "jxc_optionsTest");
        o.targetDir.mkdirs();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        delDirs(o.targetDir);
    }

    public void testCreateCodeWriter() throws JClassAlreadyExistsException, IOException {
        JCodeModel jcm = new JCodeModel();
        JDefinedClass c = jcm._class("test.TestClass");
        c.constructor(JMod.PUBLIC);
        o.readOnly = false;

        //test UTF-8
        o.encoding = "UTF-8";
        jcm.build(o.createCodeWriter());
        File cls = new File(o.targetDir, "test/TestClass.java");
        FileInputStream fis = new FileInputStream(cls);
        //same string in UTF-8 is 1byte shorter on JDK6 than on JDK5
        //therefore final check is for 'contains' and not for 'endsWith'
        byte[] in = new byte[13];
        fis.read(in);
        fis.close();
        cls.delete();
        String inStr = new String(in, "UTF-8");
        assertTrue("Got: '" + inStr + "'", inStr.contains("// This f"));

        //test UTF-16
        o.noFileHeader = true;
        o.encoding = "UTF-16";
        jcm.build(o.createCodeWriter());
        cls = new File(o.targetDir, "test/TestClass.java");
        fis = new FileInputStream(cls);
        in = new byte[26];
        fis.read(in);
        fis.close();
        cls.delete();
        inStr = new String(in, "UTF-16");
        assertTrue("Got: '" + inStr + "'", inStr.contains("package t"));

        //test default encoding
        o.noFileHeader = false;
        o.encoding = null;
        jcm.build(o.createCodeWriter());
        cls = new File(o.targetDir, "test/TestClass.java");
        fis = new FileInputStream(cls);
        //this should handle also UTF-32...
        in = new byte[84];
        fis.read(in);
        fis.close();
        cls.delete();
        inStr = new String(in, Charset.defaultCharset().name());
        assertTrue("Got: '" + inStr + "'", inStr.contains("// This f"));
    }

    public void testProxySettings() throws Exception {
        Options opts = new Options();
        File grammar = File.createTempFile("jaxbproxytest", "xsd");
        grammar.deleteOnExit();

        try {
            opts.parseArguments(new String[]{"-httpproxy", "www.proxy", grammar.getAbsolutePath()});
            assertEquals("www.proxy", getField("proxyHost", opts));
            assertEquals("80", getField("proxyPort", opts));
            assertNull(opts.proxyAuth);
        } catch (BadCommandLineException ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } finally {
            if (opts.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
        }
        opts = new Options();
        try {
            opts.parseArguments(new String[]{"-httpproxy", "www.proxy1:4321", grammar.getAbsolutePath()});
            assertEquals("www.proxy1", getField("proxyHost", opts));
            assertEquals("4321", getField("proxyPort", opts));
            assertNull(opts.proxyAuth);
        } catch (BadCommandLineException ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } finally {
            if (opts.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
        }
        opts = new Options();
        try {
            opts.parseArguments(new String[]{"-httpproxy", "user:pwd@www.proxy3:7890", grammar.getAbsolutePath()});
            assertEquals("www.proxy3", getField("proxyHost", opts));
            assertEquals("7890", getField("proxyPort", opts));
            assertEquals("user:pwd", opts.proxyAuth);
        } catch (BadCommandLineException ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } finally {
            if (opts.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
        }
        opts = new Options();
        try {
            opts.parseArguments(new String[]{"-httpproxy", "duke:s@cr@t@proxy98", grammar.getAbsolutePath()});
            assertEquals("proxy98", getField("proxyHost", opts));
            assertEquals("80", getField("proxyPort", opts));
            assertEquals("duke:s@cr@t", opts.proxyAuth);
        } catch (BadCommandLineException ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } finally {
            if (opts.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
        }
    }

    public static void delDirs(File... dirs) {
        for (File dir : dirs) {
            if (!dir.exists()) {
                continue;
            }
            if (dir.isDirectory()) {
                for (File f : dir.listFiles()) {
                    delDirs(f);
                }
                dir.delete();
            } else {
                dir.delete();
            }
        }
    }

    private String getField(String fieldName, Object instance) {
        Field f = null;
        boolean reset = false;
        try {
            f = Options.class.getDeclaredField(fieldName);
            if (!f.isAccessible()) {
                f.setAccessible(reset = true);
            }
            return (String) f.get(instance);
        } catch (Exception ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reset && f != null) {
                f.setAccessible(false);
            }
        }
        return null;
    }
}
