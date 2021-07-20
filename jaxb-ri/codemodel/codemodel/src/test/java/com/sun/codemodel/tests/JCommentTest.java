/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.codemodel.writer.OutputStreamCodeWriter;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;

/**
 *
 * @author lukas
 */
public class JCommentTest extends TestCase {

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

        String generatedClass = os.toString("UTF-8");
        System.out.println(generatedClass);
        assertTrue(generatedClass.contains("<b>"));
        assertTrue(generatedClass.contains("</p>"));
        assertFalse(generatedClass.contains("&"));
    }
}
