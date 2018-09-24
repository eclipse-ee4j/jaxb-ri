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
import com.sun.codemodel.JModuleDirective.Type;
import static com.sun.codemodel.JModuleTest.EOL;
import static com.sun.codemodel.JModuleTest.PKG_NAME;
import static com.sun.codemodel.JModuleTest.normalizeWhiteSpaces;
import static org.junit.Assert.*;

/**
 * Test Java module {@code exports} directive.
 * @author kratz
 */
public class JExportsTest extends JTestModuleDirective {

    /**
     * Test of getType method to make sure that it returns ExportsDirective.
     */
    @Test
    public void testGetType() {
        final JExportsDirective instance = new JExportsDirective(PKG_NAME);
        assertEquals(Type.ExportsDirective, instance.getType());
    }

    /**
     * Test of generate method.
     */
    @Test
    public void testGenerate() {
        final JExportsDirective instance = new JExportsDirective(PKG_NAME);
        instance.generate(jf);
        final String output = normalizeWhiteSpaces(out.toString());
        final StringBuilder sb = new StringBuilder(24);
        sb.append("exports");
        sb.append(' ');
        sb.append(PKG_NAME);
        sb.append(';').append(EOL);
        //System.out.println("LEN: " + sb.length());
        final String check = normalizeWhiteSpaces(sb.toString());
        //System.out.println("OUT: \""+output+"\" -- CHK: \""+check+"\"");
        assertEquals(check, output);
    }

}
