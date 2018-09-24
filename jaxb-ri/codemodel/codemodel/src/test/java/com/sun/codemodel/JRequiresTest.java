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

import org.junit.Test;
import static com.sun.codemodel.JModuleTest.EOL;
import static com.sun.codemodel.JModuleTest.MODULE_NAME;
import static com.sun.codemodel.JModuleTest.normalizeWhiteSpaces;
import static org.junit.Assert.*;

/**
 * Test Java module {@code requires} directive for module dependency.
 * @author Tomas Kraus
 */
public class JRequiresTest extends JTestModuleDirective {

    /**
     * Test of generateModifiers method with all combinations of {@code local) and {@code public) modifiers.
     */
    @Test
    public void testGenerateModifiers() {
        // All possible combinations of 2 boolean arguments has to be tested. They are masked
        // using 2 the least significant bits of byte value so we have to verify masks from 0x00 to 0x03.
        //  * 0x01 masks local
        //  * 0x02 masks public
        for (byte i = 0x00; i < 0x04; i++) {
            // Clean up output for next iteration.
            if (i > 0) {
                reopenOutput();
            }
            final boolean isPublic = (i & 0x01) > 0;
            final boolean isStatic = (i & 0x02) > 0;
            final JRequiresDirective instance = new JRequiresDirective(MODULE_NAME, isPublic, isStatic);
            instance.generateModifiers(jf);
            verifyModifiers(normalizeWhiteSpaces(out.toString()), isPublic, isStatic);
        }
    }

    /**
     * Test of generate method.
     */
    @Test
    public void testGenerate() {
        final JRequiresDirective instance = new JRequiresDirective(MODULE_NAME, false, false);
        instance.generate(jf);
        final String output = normalizeWhiteSpaces(out.toString());
        final StringBuilder sb = new StringBuilder(24);
        sb.append("requires ");
        sb.append(instance.name);
        sb.append(";").append(EOL);
        //System.out.println("LEN: " + sb.length());
        final String check = normalizeWhiteSpaces(sb.toString());
        //System.out.println("OUT: \""+output+"\" -- CHK: \""+check+"\"");
        assertEquals(check, output);
    }

    /**
     * Verify generated modifiers.
     */
    private static void verifyModifiers(final String output, final boolean isPublic, final boolean isStatic) {
        final int len = isPublic ? (isStatic ? 13 : 6) : (isStatic ? 6 : 0);
        final StringBuilder sb = new StringBuilder(len);
        if (isPublic) {
            sb.append("public");
        }
        if (isStatic) {
            if (isPublic) {
                sb.append(' ');
            }
            sb.append("static");
        }
        //System.out.println("verifyModifiers LEN: " + sb.length() + " - " + len);
        final String check = normalizeWhiteSpaces(sb.toString());
        //System.out.println("OUT: \""+output+"\" -- CHK: \""+check+"\"");
        assertEquals(check, output);
    }

}
