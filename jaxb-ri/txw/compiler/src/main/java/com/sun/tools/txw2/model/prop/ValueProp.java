/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model.prop;

import com.sun.codemodel.JType;

/**
 * @author Kohsuke Kawaguchi
 */
public class ValueProp extends Prop {
    private final JType type;

    public ValueProp(JType type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ValueProp)) return false;

        final ValueProp that = (ValueProp) o;

        return type.equals(that.type);
    }

    public int hashCode() {
        return type.hashCode();
    }
}
