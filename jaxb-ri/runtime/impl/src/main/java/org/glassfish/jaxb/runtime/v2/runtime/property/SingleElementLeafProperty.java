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
import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeElementPropertyInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeRef;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.TransducedAccessor;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;
import jakarta.xml.bind.JAXBElement;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * {@link Property} that contains a leaf value.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
final class SingleElementLeafProperty<BeanT> extends PropertyImpl<BeanT> {

    private final Name tagName;
    private final boolean nillable;
    private final Accessor acc;
    private final String defaultValue;
    private final TransducedAccessor<BeanT> xacc;
    private final boolean improvedXsiTypeHandling;
    private final boolean idRef;

    public SingleElementLeafProperty(JAXBContextImpl context, RuntimeElementPropertyInfo prop) {
        super(context, prop);
        RuntimeTypeRef ref = prop.getTypes().get(0);
        tagName = context.nameBuilder.createElementName(ref.getTagName());
        assert tagName != null;
        nillable = ref.isNillable();
        defaultValue = ref.getDefaultValue();
        this.acc = prop.getAccessor().optimize(context);

        xacc = TransducedAccessor.get(context, ref);
        assert xacc != null;

        improvedXsiTypeHandling = context.improvedXsiTypeHandling;
        idRef = ref.getSource().id() == ID.IDREF;
    }

    public void reset(BeanT o) throws AccessorException {
        acc.set(o, null);
    }

    public String getIdValue(BeanT bean) throws AccessorException, SAXException {
        return xacc.print(bean).toString();
    }

    @Override
    public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        boolean hasValue = xacc.hasValue(o);

        Object obj = null;

        try {
            obj = acc.getUnadapted(o);
        } catch (AccessorException ae) {
            ; // noop
        }

        Class valueType = acc.getValueType();

        // check for different type than expected. If found, add xsi:type declaration
        if (xsiTypeNeeded(o, w, obj, valueType)) {
            w.startElement(tagName, outerPeer);
            w.childAsXsiType(obj, fieldName, w.grammar.getBeanInfo(valueType), false);
            w.endElement();
        } else {  // current type is expected
            if (hasValue) {
                xacc.writeLeafElement(w, tagName, o, fieldName);
            } else if (nillable) {
                w.startElement(tagName, null);
                w.writeXsiNilTrue();
                w.endElement();
            }
        }
    }

    /**
     * Checks if xsi type needed to be specified
     */
    private boolean xsiTypeNeeded(BeanT bean, XMLSerializer w, Object value, Class valueTypeClass) {
        if (!improvedXsiTypeHandling) // improved xsi type set
            return false;
        if (acc.isAdapted()) // accessor is not adapted
            return false;
        if (value == null) // value is not null
            return false;
        if (value.getClass().equals(valueTypeClass)) // value represented by different class
            return false;
        if (idRef) // IDREF
            return false;
        if (valueTypeClass.isPrimitive()) // is not primitive
            return false;
        return acc.isValueTypeAbstractable() || isNillableAbstract(bean, w.grammar, value, valueTypeClass);
    }

    /**
     * Checks if element is nillable and represented by abstract class.
     */
    private boolean isNillableAbstract(BeanT bean, JAXBContextImpl context, Object value, Class valueTypeClass) {
        if (!nillable) // check if element is nillable
            return false;
        if (valueTypeClass != Object.class) // required type wasn't recognized
            return false;
        if (bean.getClass() != JAXBElement.class) // is JAXBElement
            return false;
        JAXBElement jaxbElement = (JAXBElement) bean;
        Class valueClass = value.getClass();
        Class declaredTypeClass = jaxbElement.getDeclaredType();
        if (declaredTypeClass.equals(valueClass)) // JAXBElement<class> is different from unadapted class)
            return false;
        if (!declaredTypeClass.isAssignableFrom(valueClass)) // and is subclass from it
            return false;
        if (!Modifier.isAbstract(declaredTypeClass.getModifiers())) // declared class is abstract
            return false;
        return acc.isAbstractable(declaredTypeClass); // and is not builtin type
    }

    public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
        Loader l = new LeafPropertyLoader(xacc);
        if (defaultValue != null)
            l = new DefaultValueLoaderDecorator(l, defaultValue);
        if (nillable || chain.context.allNillable)
            l = new XsiNilLoader.Single(l, acc);

        // LeafPropertyXsiLoader doesn't work well with nillable elements
        if (improvedXsiTypeHandling)
            l = new LeafPropertyXsiLoader(l, xacc, acc);

        handlers.put(tagName, new ChildLoader(l, null));
    }


    public PropertyKind getKind() {
        return PropertyKind.ELEMENT;
    }

    @Override
    public Accessor getElementPropertyAccessor(String nsUri, String localName) {
        if (tagName.equals(nsUri, localName))
            return acc;
        else
            return null;
    }
}
