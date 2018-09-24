/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.reflect.opt;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ConstTest extends TestCase {

    public void testFieldsFinal() {
        for(Field f : Const.class.getDeclaredFields()) {
            assertTrue("Field [" +f.getName()+  "] must be final!", Modifier.isFinal(f.getModifiers()));
        }
    }

}
