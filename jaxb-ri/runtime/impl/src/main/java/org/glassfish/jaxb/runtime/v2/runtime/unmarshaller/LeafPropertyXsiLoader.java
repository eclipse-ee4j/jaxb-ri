/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.runtime.DatatypeConverterImpl;
import org.glassfish.jaxb.core.v2.WellKnownNamespace;
import org.glassfish.jaxb.runtime.v2.runtime.ClassBeanInfoImpl;
import org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.TransducedAccessor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.util.Collection;

public final class LeafPropertyXsiLoader extends Loader {

    private final Loader defaultLoader;
    private final TransducedAccessor xacc;
    private final Accessor acc;

    public LeafPropertyXsiLoader(Loader defaultLoader, TransducedAccessor xacc, Accessor acc) {
        this.defaultLoader = defaultLoader;
        this.expectText = true;
        this.xacc = xacc;
        this.acc = acc;
    }


    @Override
    public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
        final Loader loader = selectLoader(state, ea);
        state.setLoader(loader);
        loader.startElement(state, ea);
    }

    protected Loader selectLoader(UnmarshallingContext.State state, TagName ea) throws SAXException {

        UnmarshallingContext context = state.getContext();
        JaxBeanInfo beanInfo = null;

        // look for @xsi:type
        Attributes atts = ea.atts;
        int idx = atts.getIndex(WellKnownNamespace.XML_SCHEMA_INSTANCE, "type");

        if (idx >= 0) {
            String value = atts.getValue(idx);

            QName type = DatatypeConverterImpl._parseQName(value, context);

            if (type == null)
                return defaultLoader;

            beanInfo = context.getJAXBContext().getGlobalType(type);
            if (beanInfo == null)
                return defaultLoader;
            ClassBeanInfoImpl cbii;
            try {
                cbii = (ClassBeanInfoImpl) beanInfo;
            } catch (ClassCastException cce) {
                return defaultLoader;
            }

            if (null == cbii.getTransducer()) {
                return defaultLoader;
            }

            return new LeafPropertyLoader(
                    new TransducedAccessor.CompositeTransducedAccessorImpl(
                            state.getContext().getJAXBContext(),
                            cbii.getTransducer(),
                            acc));
        }

        return defaultLoader;
    }

    @Override
    public Collection<QName> getExpectedChildElements() {
        return defaultLoader.getExpectedChildElements();
    }

    @Override
    public Collection<QName> getExpectedAttributes() {
        return defaultLoader.getExpectedAttributes();
    }
}
