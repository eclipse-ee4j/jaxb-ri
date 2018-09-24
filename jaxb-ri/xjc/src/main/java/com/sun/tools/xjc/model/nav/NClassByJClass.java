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

import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;

/**
 * @author Kohsuke Kawaguchi
 */
class NClassByJClass implements NClass {
    /*package*/ final JClass clazz;

    NClassByJClass(JClass clazz) {
        this.clazz = clazz;
    }

    public JClass toType(Outline o, Aspect aspect) {
        return clazz;
    }

    public boolean isAbstract() {
        return clazz.isAbstract();
    }

    public boolean isBoxedType() {
        return clazz.getPrimitiveType()!=null;
    }

    public String fullName() {
        return clazz.fullName();
    }
}
