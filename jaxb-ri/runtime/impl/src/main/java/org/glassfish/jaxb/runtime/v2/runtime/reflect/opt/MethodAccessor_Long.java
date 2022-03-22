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
 * <p><b>
 *     Auto-generated, do not edit.
 * </b></p>
 * <p>
 *     All the MethodAccessors are generated from <code>MethodAccessor_Byte</code>
 * </p>
 * @author Kohsuke Kawaguchi
 */
public class MethodAccessor_Long<B> extends Accessor<B, Long> {
    public MethodAccessor_Long() {
        super(Long.class);
    }

    @Override
    public Long get(B bean) {
        return ((Bean)bean).get_long();
    }

    @Override
    public void set(B bean, Long value) {
        ((Bean)bean).set_long( value==null ? Const.default_value_long : value );
    }
}
