/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025, 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.OutputStreamCodeWriter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author lukas
 */
public class JCommentTest {

    public void testJavadoc() throws Throwable {
        JCodeModel model = new JCodeModel();
        String className = "gh1471.JavadocTest";
        JDefinedClass cls = model._class(className, ClassType.CLASS);
        JDocComment comment = cls.javadoc();
        comment.add("<b>a bold text</b><br>\n");
        comment.add("<p>a text within paraphrases</p>");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStreamCodeWriter fileCodeWriter = new OutputStreamCodeWriter(os, "UTF-8");
        model.build(fileCodeWriter);

        String generatedClass = os.toString(StandardCharsets.UTF_8);
        System.out.println(generatedClass);
        assertTrue(generatedClass.contains("<b>"));
        assertTrue(generatedClass.contains("</p>"));
        assertFalse(generatedClass.contains("&"));
    }

    /**
     * This should produce the following javadoc :<br><br>
     * Age &lt;&gt; 18 &amp; age &lt;&gt; 21 &#064;home
     */
    public void testJavadocXML() throws Throwable {
        JCodeModel model = new JCodeModel();
        String className = "gh1826.JavadocTest";
        JDefinedClass cls = model._class(className, ClassType.CLASS);
        JDocComment comment = cls.javadoc();
        comment.appendXML("Age <> 18 & age <> 21 @home");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStreamCodeWriter fileCodeWriter = new OutputStreamCodeWriter(os, "UTF-8");
        model.build(fileCodeWriter);

        String generatedClass = os.toString(StandardCharsets.UTF_8);
        System.out.println(generatedClass);
        assertFalse(generatedClass.contains("<"));
        assertFalse(generatedClass.contains(">"));
        assertFalse(generatedClass.contains("@"));
        assertTrue(generatedClass.contains("&amp;"));
        assertTrue(generatedClass.contains("&#064;"));
    }

    /**
     * javac translates Unicode escapes before it looks for the end of a comment,
     * so they must not reach the generated javadoc verbatim.
     */
    @Test
    public void testJavadocUnicodeEscape() throws Throwable {
        JCodeModel model = new JCodeModel();
        JDefinedClass cls = model._class("unicode.JavadocTest", ClassType.CLASS);
        cls.javadoc().append("Stored under C:\\users\\shared");
        JMethod m = cls.method(JMod.PUBLIC, model.VOID, "run");
        m.javadoc().append("text \\u002a/ static { init(); } /\\u002a\\u002a");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStreamCodeWriter fileCodeWriter = new OutputStreamCodeWriter(os, "UTF-8");
        model.build(fileCodeWriter);

        String generatedClass = os.toString(StandardCharsets.UTF_8);
        assertFalse(generatedClass.contains("\\u"));
        assertTrue(generatedClass.contains("Stored under C:\\<!---->users\\shared"));
        assertTrue(generatedClass.contains("text \\<!---->u002a/ static { init(); } /\\<!---->u002a\\<!---->u002a"));
    }
}
