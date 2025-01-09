/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.tests;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.OutputStreamCodeWriter;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JDefinedClassInstanceInitTest {

    @Test
    public void generatesInstanceInit() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass c = cm._package("myPackage")._class(0, "MyClass");
        JFieldVar myField = c.field(JMod.PRIVATE, String.class, "myField");
        c.instanceInit().assign(JExpr._this().ref(myField),
                JExpr.lit("myValue"));
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final String encoding = "UTF-8";
//		cm.build(new OutputStreamCodeWriter(System.out, encoding));
        cm.build(new OutputStreamCodeWriter(bos, encoding));
        bos.close();

        final ByteArrayInputStream bis = new ByteArrayInputStream(
                bos.toByteArray());

        CompilationUnit compilationUnit = JavaParser.parse(bis, encoding);

        TypeDeclaration typeDeclaration = compilationUnit.getTypes().get(0);
        ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;

        final InitializerDeclaration initializerDeclaration = (InitializerDeclaration) classDeclaration
                .getMembers().get(1);

        assertNotNull(initializerDeclaration);
    }
}
