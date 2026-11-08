/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.tests;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.writer.OutputStreamCodeWriter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavadocInlineTagTest {

    private static String generate(String text) throws Exception {
        JCodeModel model = new JCodeModel();
        JDefinedClass cls = model._class("inline.JavadocTest", ClassType.CLASS);
        cls.javadoc().appendXML(text);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.build(new OutputStreamCodeWriter(os, "UTF-8"));
        return os.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void inlineTagIsKept() throws Exception {
        String generated = generate("See {@link java.lang.String}.");
        assertTrue(generated.contains("See {@link java.lang.String}."), generated);
    }

    @Test
    public void blockTagIsStillEscaped() throws Exception {
        String generated = generate("mail me @home");
        assertTrue(generated.contains("mail me &#064;home"), generated);
    }
}
