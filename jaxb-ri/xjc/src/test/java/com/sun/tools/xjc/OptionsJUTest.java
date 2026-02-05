/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author Lukas Jungmann
 */
public class OptionsJUTest {

    private Options o;

    @BeforeEach
    protected void setUp() throws Exception {
        o = new Options();
        o.targetDir = new File(System.getProperty("java.io.tmpdir"), "jxc_optionsTest");
        o.targetDir.mkdirs();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        delDirs(o.targetDir);
    }

    @Test
    public void testCreateCodeWriter() throws JClassAlreadyExistsException, IOException {
        Locale locale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
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
            String inStr = new String(in, StandardCharsets.UTF_8);
            System.out.println("===");
            System.out.println(inStr);
            System.out.println("===");
            assertTrue(inStr.contains("// This f"), "Got: '" + inStr + "'");

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
            inStr = new String(in, StandardCharsets.UTF_16);
            assertTrue(inStr.contains("package t"), "Got: '" + inStr + "'");

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
            assertTrue(inStr.contains("// This f"), "Got: '" + inStr + "'");
        } finally {
            Locale.setDefault(locale);
        }
    }

    @Test
    public void testProxySettings() throws Exception {
        Options opts = new Options();
        File grammar = File.createTempFile("jaxbproxytest", "xsd");
        grammar.deleteOnExit();

        try {
            opts.parseArguments(new String[]{"-httpproxy", "www.proxy", grammar.getAbsolutePath()});
            assertEquals("www.proxy", System.getProperty("http.proxyHost"));
            assertEquals("www.proxy", System.getProperty("https.proxyHost"));
            assertEquals("80", System.getProperty("http.proxyPort"));
            assertEquals("80", System.getProperty("https.proxyPort"));
            assertNull(opts.proxyAuth);
        } catch (BadCommandLineException ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } finally {
            if (opts.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
            System.clearProperty("http.proxyHost");
            System.clearProperty("https.proxyHost");
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyPort");
        }
        opts = new Options();
        try {
            opts.parseArguments(new String[]{"-httpproxy", "www.proxy1:4321", grammar.getAbsolutePath()});
            assertEquals("www.proxy1", System.getProperty("http.proxyHost"));
            assertEquals("www.proxy1", System.getProperty("https.proxyHost"));
            assertEquals("4321", System.getProperty("http.proxyPort"));
            assertEquals("4321", System.getProperty("https.proxyPort"));
            assertNull(opts.proxyAuth);
        } catch (BadCommandLineException ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } finally {
            if (opts.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
            System.clearProperty("http.proxyHost");
            System.clearProperty("https.proxyHost");
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyPort");
        }
        opts = new Options();
        try {
            opts.parseArguments(new String[]{"-httpproxy", "user:pwd@www.proxy3:7890", grammar.getAbsolutePath()});
            assertEquals("www.proxy3", System.getProperty("http.proxyHost"));
            assertEquals("www.proxy3", System.getProperty("https.proxyHost"));
            assertEquals("7890", System.getProperty("http.proxyPort"));
            assertEquals("7890", System.getProperty("https.proxyPort"));
            assertEquals("user:pwd", opts.proxyAuth);
        } catch (BadCommandLineException ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } finally {
            if (opts.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
            System.clearProperty("http.proxyHost");
            System.clearProperty("https.proxyHost");
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyPort");
        }
        opts = new Options();
        try {
            opts.parseArguments(new String[]{"-httpproxy", "duke:s@cr@t@proxy98", grammar.getAbsolutePath()});
            assertEquals("proxy98", System.getProperty("http.proxyHost"));
            assertEquals("proxy98", System.getProperty("https.proxyHost"));
            assertEquals("80", System.getProperty("http.proxyPort"));
            assertEquals("80", System.getProperty("https.proxyPort"));
            assertEquals("duke:s@cr@t", opts.proxyAuth);
        } catch (BadCommandLineException ex) {
            Logger.getLogger(OptionsJUTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } finally {
            if (opts.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
            System.clearProperty("http.proxyHost");
            System.clearProperty("https.proxyHost");
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyPort");
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
}
