/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.impl;

import java.lang.reflect.Type;

import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.bind.v2.runtime.Transducer;

/**
 * @author Kohsuke Kawaguchi
 */
final class RuntimeAnyTypeImpl extends AnyTypeImpl<Type,Class> implements RuntimeNonElement {
    private RuntimeAnyTypeImpl() {
        super(Utils.REFLECTION_NAVIGATOR);
    }

    public <V> Transducer<V> getTransducer() {
        return null;
    }

    static final RuntimeNonElement theInstance = new RuntimeAnyTypeImpl();
}
