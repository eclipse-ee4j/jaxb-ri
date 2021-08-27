/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.runtime.v2.runtime.Coordinator;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.xml.sax.SAXException;

/**
 * {@link Lister} that adapts individual item types.
 */
final class AdaptedLister<BeanT,PropT,InMemItemT,OnWireItemT,PackT> extends Lister<BeanT,PropT,OnWireItemT,PackT> {
    private final Lister<BeanT,PropT,InMemItemT,PackT> core;
    private final Class<? extends XmlAdapter<OnWireItemT,InMemItemT>> adapter;

    /*package*/ AdaptedLister(
        Lister<BeanT,PropT,InMemItemT,PackT> core,
        Class<? extends XmlAdapter<OnWireItemT,InMemItemT>> adapter) {

        this.core = core;
        this.adapter = adapter;
    }

    private XmlAdapter<OnWireItemT,InMemItemT> getAdapter() {
        return Coordinator._getInstance().getAdapter(adapter);
    }

    @Override
    public ListIterator<OnWireItemT> iterator(PropT prop, XMLSerializer context) {
        return new ListIteratorImpl( core.iterator(prop,context), context );
    }

    @Override
    public PackT startPacking(BeanT bean, Accessor<BeanT, PropT> accessor) throws AccessorException {
        return core.startPacking(bean,accessor);
    }

    @Override
    public void addToPack(PackT pack, OnWireItemT item) throws AccessorException {
        InMemItemT r;
        try {
            r = getAdapter().unmarshal(item);
        } catch (Exception e) {
            throw new AccessorException(e);
        }
        core.addToPack(pack,r);
    }

    @Override
    public void endPacking(PackT pack, BeanT bean, Accessor<BeanT,PropT> accessor) throws AccessorException {
        core.endPacking(pack,bean,accessor);
    }

    @Override
    public void reset(BeanT bean, Accessor<BeanT, PropT> accessor) throws AccessorException {
        core.reset(bean,accessor);
    }

    private final class ListIteratorImpl implements ListIterator<OnWireItemT> {
        private final ListIterator<InMemItemT> core;
        private final XMLSerializer serializer;

        public ListIteratorImpl(ListIterator<InMemItemT> core,XMLSerializer serializer) {
            this.core = core;
            this.serializer = serializer;
        }

        @Override
        public boolean hasNext() {
            return core.hasNext();
        }

        @Override
        public OnWireItemT next() throws SAXException, JAXBException {
            InMemItemT next = core.next();
            try {
                return getAdapter().marshal(next);
            } catch (Exception e) {
                serializer.reportError(null,e);
                return null; // recover this error by returning null
            }
        }
    }
}
