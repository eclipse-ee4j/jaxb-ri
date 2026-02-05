/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect.opt;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstTest {

    @Test
    public void testFieldsFinal() {
        for(Field f : Const.class.getDeclaredFields()) {
            if (!f.isSynthetic()){
                assertTrue(Modifier.isFinal(f.getModifiers()), "Field [" +f.getName()+  "] must be final!");
            }
        }
    }

}
