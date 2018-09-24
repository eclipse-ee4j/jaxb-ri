/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.property;


import java.io.IOException;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.bind.v2.runtime.unmarshaller.ValuePropertyLoader;
import com.sun.xml.bind.v2.util.QNameMap;

import org.xml.sax.SAXException;

/**
 * {@link Property} implementation for {@link XmlValue} properties.
 *
 * <p>
 * This one works for both leaves and nodes, scalars and arrays.
 *
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
public final class ValueProperty<BeanT> extends PropertyImpl<BeanT> {

    /**
     * Heart of the conversion logic.
     */
    private final TransducedAccessor<BeanT> xacc;
    private final Accessor<BeanT,?> acc;


    public ValueProperty(JAXBContextImpl context, RuntimeValuePropertyInfo prop) {
        super(context,prop);
        xacc = TransducedAccessor.get(context,prop);
        acc = prop.getAccessor();   // we only use this for binder, so don't waste memory by optimizing
    }

    public final void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        if(xacc.hasValue(o))
            xacc.writeText(w,o,fieldName);
    }

    public void serializeURIs(BeanT o, XMLSerializer w) throws SAXException, AccessorException {
        xacc.declareNamespace(o,w);
    }

    public boolean hasSerializeURIAction() {
        return xacc.useNamespace();
    }

    public void buildChildElementUnmarshallers(UnmarshallerChain chainElem, QNameMap<ChildLoader> handlers) {
        handlers.put(StructureLoaderBuilder.TEXT_HANDLER,
                new ChildLoader(new ValuePropertyLoader(xacc),null));
    }

    public PropertyKind getKind() {
        return PropertyKind.VALUE;
    }

    public void reset(BeanT o) throws AccessorException {
        acc.set(o,null);
    }

    public String getIdValue(BeanT bean) throws AccessorException, SAXException {
        return xacc.print(bean).toString();
    }

}
