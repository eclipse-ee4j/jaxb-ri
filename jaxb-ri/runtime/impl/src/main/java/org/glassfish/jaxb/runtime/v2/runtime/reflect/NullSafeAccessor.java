/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import org.glassfish.jaxb.runtime.api.AccessorException;

/**
 * {@link Accessor} wrapper that replaces a null with an empty collection.
 *
 * <p>
 * This is so that JAX-WS property accessor will work like an ordinary getter.
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class NullSafeAccessor<B,V,P> extends Accessor<B,V> {
    private final Accessor<B,V> core;
    private final Lister<B,V,?,P> lister;

    public NullSafeAccessor(Accessor<B,V> core, Lister<B,V,?,P> lister) {
        super(core.getValueType());
        this.core = core;
        this.lister = lister;
    }

    public V get(B bean) throws AccessorException {
        V v = core.get(bean);
        if(v==null) {
            // creates a new object
            P pack = lister.startPacking(bean,core);
            lister.endPacking(pack,bean,core);
            v = core.get(bean);
        }
        return v;
    }

    public void set(B bean, V value) throws AccessorException {
        core.set(bean,value);
    }
}
