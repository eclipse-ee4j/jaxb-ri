/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model.nav;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;

/**
 * @author Kohsuke Kawaguchi
 */
public class EagerNClass extends EagerNType implements NClass {
    /*package*/ final Class c;

    public EagerNClass(Class type) {
        super(type);
        this.c = type;
    }

    @Override
    public boolean isBoxedType() {
        return boxedTypes.contains(c);
    }

    @Override
    public JClass toType(Outline o, Aspect aspect) {
        return o.getCodeModel().ref(c);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(c.getModifiers());
    }

    private static final Set<Class> boxedTypes = new HashSet<Class>();

    static {
        boxedTypes.add(Boolean.class);
        boxedTypes.add(Character.class);
        boxedTypes.add(Byte.class);
        boxedTypes.add(Short.class);
        boxedTypes.add(Integer.class);
        boxedTypes.add(Long.class);
        boxedTypes.add(Float.class);
        boxedTypes.add(Double.class);
    }
}
