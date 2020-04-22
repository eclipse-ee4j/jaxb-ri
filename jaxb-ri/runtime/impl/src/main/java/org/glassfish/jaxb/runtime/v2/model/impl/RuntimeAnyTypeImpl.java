/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeNonElement;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;

import java.lang.reflect.Type;

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
