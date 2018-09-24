/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test Java module.
 * @author Tomas Kraus
 */
public class JModuleTest {

    /** Java package name used in tests. */
    static final String PKG_NAME = "jaxb.package";

    /** Java module name used in tests. */
    static final String MODULE_NAME = "jaxb.test";

    /** Dependent Java module name used in tests. */
    static final String DEP_MODULE_NAME = "other.module";

    /** System dependent new line. */
    static final String EOL = System.getProperty("line.separator");

    /** Pattern to match white spaces sequences. */
    private static final Pattern WH_SPACES = Pattern.compile("\\p{javaWhitespace}+");

    /** Character array writer used to verify code generation output. */
    protected CharArrayWriter out;

    /** Java formatter used to generate code output. */
    protected JFormatter jf;

    /**
     * Normalize white space sequences for {@code String} verification.
     * @param str Source {@code String}.
     * @return Source {@code String} with all whitespace sequences normalized.
     */
    static String normalizeWhiteSpaces(final String str) {
        if (str == null) {
            return str;
        }
        return WH_SPACES.matcher(str).replaceAll(" ");
    }

    /**
     * Creates an instance of Java module test.
     */
    public JModuleTest() {
    }

    /**
     * Initialize test.
     */
    @Before
    public void setUp() {
        openOutput();
    }

    /**
     * Cleanup test.
     */
    @After
    public void tearDown() {
        closeOutput();
    }

    /**
     * Open Java formatting output for this test.
     */
    protected void openOutput() {
        out = new CharArrayWriter();
        jf = new JFormatter(new PrintWriter(out));
    }

    /**
     * Close Java formatting output for this test.
     */
    protected void closeOutput() {
        jf.close();
        out.close();
    }

    /**
     * Reopen Java formatting output for this test.
     * Used to reset output content during tests.
     */
    protected void reopenOutput() {
        closeOutput();
        openOutput();
    }

    /**
     * Test of name method, of class JModule.
     */
    @Test
    public void testName() {
        final JModule instance = new JModule(MODULE_NAME);
        assertEquals(MODULE_NAME, instance.name());
    }

    /**
     * Check that module directives set contains exactly one element.
     * @param instance Java module instance being checked.
     * @return Directive element stored in the set.
     */
    private static JModuleDirective directivesSingleElementCheck(final JModule instance) {
        final JModuleDirective[] directives = instance.getDirectives().toArray(new JModuleDirective[1]);
        assertNotNull(directives);
        assertEquals(1, directives.length);
        return directives[0];
    }

    /**
     * Test of _exports method, of class JModule.
     * {@code JModule} instance must contain exactly one directive instance
     * of {@code JExportsDirective} class after {@code _exports} method call.
     */
    @Test
    public void test_exports() {
        final JModule instance = new JModule(MODULE_NAME);
        final JCodeModel cm = new JCodeModel();
        final JPackage pkg = new JPackage(PKG_NAME, cm);
        instance._exports(pkg);
        JModuleDirective directive = directivesSingleElementCheck(instance);
        assertTrue(directive instanceof JExportsDirective);
        assertEquals(PKG_NAME, directive.name);
    }

    /**
     * Test of _requiresModule method, of class JModule.
     */
    @Test
    public void test_requires() {
        final JModule instance = new JModule(MODULE_NAME);
        instance._requires(DEP_MODULE_NAME);
        JModuleDirective directive = directivesSingleElementCheck(instance);
        assertTrue(directive instanceof JRequiresDirective);
        assertEquals(DEP_MODULE_NAME, directive.name);
    }

    /**
     * Test of generate method, of class JModule.
     */
    @Test
    public void testGenerate() {
        final JModule instance = new JModule(MODULE_NAME);
        instance.generate(jf);
        final String output = normalizeWhiteSpaces(out.toString());
        //System.out.println("OUT: \""+output+"\"");
        verifyModuleEnvelope(output, instance);
    }

    /**
     * Verify generated module envelope.
     */
    private static void verifyModuleEnvelope(final String output, final JModule instance) {
        final int len = 14 + instance.name().length();
        final StringBuilder sb = new StringBuilder(len);
        sb.append("module ");
        sb.append(instance.name());
        sb.append(' ');
        sb.append('{').append(EOL);
        sb.append('}').append(EOL);
        //System.out.println("verifyModuleEnvelope LEN: " + sb.length() + " - " + len);
        final String check = normalizeWhiteSpaces(sb.toString());
        assertEquals(check, output);
    }

}
