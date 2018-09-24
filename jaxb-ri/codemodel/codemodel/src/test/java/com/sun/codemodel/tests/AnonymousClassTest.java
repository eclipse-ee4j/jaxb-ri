/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.tests;

import java.util.Iterator;

import org.junit.Test;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class AnonymousClassTest {

    @Test
    public void main() throws Exception {
        JCodeModel cm = new JCodeModel();
        JDefinedClass cls = cm._class("Test");
        JMethod m = cls.method(JMod.PUBLIC, cm.VOID, "foo");

        JDefinedClass c = cm.anonymousClass(cm.ref(Iterator.class));
        c.method(0, cm.VOID, "bob");
        c.field(JMod.PRIVATE, cm.DOUBLE, "y");
        m.body().decl(cm.ref(Object.class), "x",
                JExpr._new(c));

        cm.build(new SingleStreamCodeWriter(System.out));
    }
}
