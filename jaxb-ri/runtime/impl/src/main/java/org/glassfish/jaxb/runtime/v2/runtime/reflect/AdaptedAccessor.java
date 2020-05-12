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
import org.glassfish.jaxb.core.v2.ClassFactory;
import org.glassfish.jaxb.core.v2.model.core.Adapter;
import org.glassfish.jaxb.runtime.v2.runtime.Coordinator;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@link Accessor} that adapts the value by using {@link Adapter}.
 *
 * @see Accessor#adapt
 * @author Kohsuke Kawaguchi
 */
final class AdaptedAccessor<BeanT,InMemValueT,OnWireValueT> extends Accessor<BeanT,OnWireValueT> {
    private final Accessor<BeanT,InMemValueT> core;
    private final Class<? extends XmlAdapter<OnWireValueT,InMemValueT>> adapter;

    /*pacakge*/ AdaptedAccessor(Class<OnWireValueT> targetType, Accessor<BeanT, InMemValueT> extThis, Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> adapter) {
        super(targetType);
        this.core = extThis;
        this.adapter = adapter;
    }

    @Override
    public boolean isAdapted() {
        return true;
    }

    public OnWireValueT get(BeanT bean) throws AccessorException {
        InMemValueT v = core.get(bean);

        XmlAdapter<OnWireValueT,InMemValueT> a = getAdapter();
        try {
            return a.marshal(v);
        } catch (Exception e) {
            throw new AccessorException(e);
        }
    }

    public void set(BeanT bean, OnWireValueT o) throws AccessorException {
        XmlAdapter<OnWireValueT, InMemValueT> a = getAdapter();
        try {
            core.set(bean, (o == null ? null : a.unmarshal(o)));
        } catch (Exception e) {
            throw new AccessorException(e);
        }
    }

    public Object getUnadapted(BeanT bean) throws AccessorException {
        return core.getUnadapted(bean);
    }

    public void setUnadapted(BeanT bean, Object value) throws AccessorException {
        core.setUnadapted(bean,value);
    }

    /**
     * Sometimes Adapters are used directly by JAX-WS outside any
     * {@link Coordinator}. Use this lazily-created cached
     * {@link XmlAdapter} in such cases.
     */
    private XmlAdapter<OnWireValueT, InMemValueT> staticAdapter;

    private XmlAdapter<OnWireValueT, InMemValueT> getAdapter() {
        Coordinator coordinator = Coordinator._getInstance();
        if(coordinator!=null)
            return coordinator.getAdapter(adapter);
        else {
            synchronized(this) {
                if(staticAdapter==null)
                    staticAdapter = ClassFactory.create(adapter);
            }
            return staticAdapter;
        }
    }
}
