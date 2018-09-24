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

import java.lang.reflect.Type;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;

/**
 * @author Kohsuke Kawaguchi
 */
class EagerNType implements NType {
    /*package*/ final Type t;

    public EagerNType(Type type) {
        this.t = type;
        assert t!=null;
    }

    public JType toType(Outline o, Aspect aspect) {
        try {
            return o.getCodeModel().parseType(t.toString());
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EagerNType)) return false;

        final EagerNType eagerNType = (EagerNType) o;

        return t.equals(eagerNType.t);
    }

    public boolean isBoxedType() {
        return false;
    }

    public int hashCode() {
        return t.hashCode();
    }

    public String fullName() {
        return Utils.REFLECTION_NAVIGATOR.getTypeName(t);
    }
}
