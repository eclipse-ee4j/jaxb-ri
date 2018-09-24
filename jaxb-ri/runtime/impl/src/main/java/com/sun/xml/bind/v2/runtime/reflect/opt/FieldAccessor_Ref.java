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
 * Template {@link Accessor} for reference type fields.
 * 
 * @author Kohsuke Kawaguchi
 */
public class FieldAccessor_Ref extends Accessor {
    public FieldAccessor_Ref() {
        super(Ref.class);
    }

    public Object get(Object bean) {
        return ((Bean)bean).f_ref;
    }

    public void set(Object bean, Object value) {
        ((Bean)bean).f_ref = (Ref)value;
    }
}
