/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect.opt;

import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;

/**
 * Template {@link Accessor} for boolean getter/setter.
 *
 * <p>
 * All the MethodAccessors are generated from <code>MethodAccessor_B y t e</code>
 *
 * @author Kohsuke Kawaguchi
 */
public class MethodAccessor_Byte extends Accessor {
    public MethodAccessor_Byte() {
        super(Byte.class);
    }

    public Object get(Object bean) {
        return ((Bean)bean).get_byte();
    }

    public void set(Object bean, Object value) {
        ((Bean)bean).set_byte( value==null ? Const.default_value_byte : (Byte)value );
    }
}
