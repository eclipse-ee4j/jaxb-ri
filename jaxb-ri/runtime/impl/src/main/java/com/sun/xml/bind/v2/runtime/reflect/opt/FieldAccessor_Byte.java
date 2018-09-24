/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;

/**
 * Template {@link Accessor} for byte fields.
 *
 * <p>
 * All the FieldAccessors are generated from <code>FieldAccessor_B y t e</code>
 * 
 * @author Kohsuke Kawaguchi
 */
public class FieldAccessor_Byte extends Accessor {
    public FieldAccessor_Byte() {
        super(Byte.class);
    }

    public Object get(Object bean) {
        return ((Bean)bean).f_byte;
    }

    public void set(Object bean, Object value) {
        ((Bean)bean).f_byte = value==null ? Const.default_value_byte : (Byte)value;
    }
}
