/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Kohsuke Kawaguchi
 */
public class TypeUtilJUTest {

    @Test
    public void test1() {
        JCodeModel cm = new JCodeModel();
        JType t = TypeUtil.getCommonBaseType(cm,
                    cm.ref(List.class).narrow(Integer.class),
                    cm.ref(List.class).narrow(Float.class),
                    cm.ref(List.class).narrow(Number.class) );
        System.out.println(t.fullName());
        assertEquals("java.util.List<? extends java.lang.Number>",t.fullName());
    }
}
