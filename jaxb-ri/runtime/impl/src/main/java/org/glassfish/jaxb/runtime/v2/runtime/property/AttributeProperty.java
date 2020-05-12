/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.property;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.core.v2.model.core.AttributePropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeAttributePropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.TransducedAccessor;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.ChildLoader;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * {@link Property} implementation for {@link AttributePropertyInfo}.
 *
 * <p>
 * This one works for both leaves and nodes, scalars and arrays.
 *
 * <p>
 * Implements {@link Comparable} so that it can be sorted lexicographically.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public final class AttributeProperty<BeanT> extends PropertyImpl<BeanT>
    implements Comparable<AttributeProperty> {

    /**
     * Attribute name.
     */
    public final Name attName;

    /**
     * Heart of the conversion logic.
     */
    public final TransducedAccessor<BeanT> xacc;

    private final Accessor acc;

    public AttributeProperty(JAXBContextImpl context, RuntimeAttributePropertyInfo prop) {
        super(context,prop);
        this.attName = context.nameBuilder.createAttributeName(prop.getXmlName());
        this.xacc = TransducedAccessor.get(context,prop);
        this.acc = prop.getAccessor();   // we only use this for binder, so don't waste memory by optimizing
    }

    /**
     * Marshals one attribute.
     *
     * @see JaxBeanInfo#serializeAttributes(Object, XMLSerializer)
     */
    public void serializeAttributes(BeanT o, XMLSerializer w) throws SAXException, AccessorException, IOException, XMLStreamException {
        CharSequence value = xacc.print(o);
        if(value!=null)
            w.attribute(attName,value.toString());
    }

    public void serializeURIs(BeanT o, XMLSerializer w) throws AccessorException, SAXException {
        xacc.declareNamespace(o,w);
    }

    public boolean hasSerializeURIAction() {
        return xacc.useNamespace();
    }

    public void buildChildElementUnmarshallers(UnmarshallerChain chainElem, QNameMap<ChildLoader> handlers) {
        throw new IllegalStateException();
    }

   
    public PropertyKind getKind() {
        return PropertyKind.ATTRIBUTE;
    }

    public void reset(BeanT o) throws AccessorException {
        acc.set(o,null);
    }

    public String getIdValue(BeanT bean) throws AccessorException, SAXException {
        return xacc.print(bean).toString();
    }

    public int compareTo(AttributeProperty that) {
        return this.attName.compareTo(that.attName);
    }
}
