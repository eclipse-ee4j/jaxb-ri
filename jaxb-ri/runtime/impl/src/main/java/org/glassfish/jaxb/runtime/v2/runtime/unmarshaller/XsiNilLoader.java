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
import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.core.v2.WellKnownNamespace;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import jakarta.xml.bind.JAXBElement;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.util.Collection;

/**
 * Looks for xsi:nil='true' and sets the target to null.
 * Otherwise delegate to another handler.
 *
 * @author Kohsuke Kawaguchi
 */
public class XsiNilLoader extends ProxyLoader {

    private final Loader defaultLoader;

    public XsiNilLoader(Loader defaultLoader) {
        this.defaultLoader = defaultLoader;
        assert defaultLoader!=null;
    }

    protected Loader selectLoader(UnmarshallingContext.State state, TagName ea) throws SAXException {
        int idx = ea.atts.getIndex(WellKnownNamespace.XML_SCHEMA_INSTANCE,"nil");

        if (idx!=-1) {
            Boolean b = DatatypeConverterImpl._parseBoolean(ea.atts.getValue(idx));

            if (b != null && b) {
                onNil(state);
                boolean hasOtherAttributes = (ea.atts.getLength() - 1) > 0;
                // see issues 6759703 and 565 - need to preserve attributes even if the element is nil; only when the type is stored in JAXBElement
                if (!(hasOtherAttributes && (state.getPrev().getTarget() instanceof JAXBElement))) {
                    return Discarder.INSTANCE;
                }
            }
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

    /**
     * Called when xsi:nil='true' was found.
     */
    protected void onNil(UnmarshallingContext.State state) throws SAXException {
    }

    public static final class Single extends XsiNilLoader {
        private final Accessor acc;
        public Single(Loader l, Accessor acc) {
            super(l);
            this.acc = acc;
        }

        @Override
        protected void onNil(UnmarshallingContext.State state) throws SAXException {
            try {
                acc.set(state.getPrev().getTarget(),null);
                state.getPrev().setNil(true);
            } catch (AccessorException e) {
                handleGenericException(e,true);
            }
        }

    }

    public static final class Array extends XsiNilLoader {
        public Array(Loader core) {
            super(core);
        }

        @Override
        protected void onNil(UnmarshallingContext.State state) {
            // let the receiver add this to the lister
            state.setTarget(null);
        }
    }
}
