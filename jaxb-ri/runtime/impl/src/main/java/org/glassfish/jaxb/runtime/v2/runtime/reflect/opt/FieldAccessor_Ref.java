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
 * Template {@link Accessor} for reference type fields.
 * 
 * @author Kohsuke Kawaguchi
 */
public class FieldAccessor_Ref<B> extends Accessor<B, Ref> {
    public FieldAccessor_Ref() {
        super(Ref.class);
    }

    @Override
    public Ref get(B bean) {
        return ((Bean)bean).f_ref;
    }

    @Override
    public void set(B bean, Ref value) {
        ((Bean)bean).f_ref = value;
    }
}
