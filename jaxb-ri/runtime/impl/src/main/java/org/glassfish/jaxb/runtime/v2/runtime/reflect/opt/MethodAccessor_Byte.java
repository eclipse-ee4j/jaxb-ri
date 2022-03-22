/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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
 * All the MethodAccessors are generated from <code>MethodAccessor_Byte</code>
 *
 * @author Kohsuke Kawaguchi
 */
public class MethodAccessor_Byte<B> extends Accessor<B, Byte> {
    public MethodAccessor_Byte() {
        super(Byte.class);
    }

    @Override
    public Byte get(B bean) {
        return ((Bean)bean).get_byte();
    }

    @Override
    public void set(B bean, Byte value) {
        ((Bean)bean).set_byte( value==null ? Const.default_value_byte : value );
    }
}
