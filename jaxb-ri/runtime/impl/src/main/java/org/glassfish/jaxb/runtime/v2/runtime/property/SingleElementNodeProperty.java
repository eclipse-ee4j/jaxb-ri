/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.property;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.core.v2.model.core.TypeRef;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeElementPropertyInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeRef;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.ChildLoader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Loader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.XsiNilLoader;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;
import jakarta.xml.bind.JAXBElement;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
final class SingleElementNodeProperty<BeanT,ValueT> extends PropertyImpl<BeanT> {

    private final Accessor<BeanT,ValueT> acc;

    private final boolean nillable;

    private final QName[] acceptedElements;

    private final Map<Class,TagAndType> typeNames = new HashMap<>();

    private RuntimeElementPropertyInfo prop;
    
    /**
     * The tag name used to produce xsi:nil. The first one in the list.
     */
    private final Name nullTagName;

    public SingleElementNodeProperty(JAXBContextImpl context, RuntimeElementPropertyInfo prop) {
        super(context,prop);
        acc = prop.getAccessor().optimize(context);
        this.prop = prop;

        QName nt = null;
        boolean nil = false;

        acceptedElements = new QName[prop.getTypes().size()];
        for( int i=0; i<acceptedElements.length; i++ )
            acceptedElements[i] = prop.getTypes().get(i).getTagName();

        for (RuntimeTypeRef e : prop.getTypes()) {
            JaxBeanInfo beanInfo = context.getOrCreate(e.getTarget());
            if(nt==null)    nt = e.getTagName();
            typeNames.put( beanInfo.jaxbType, new TagAndType(
                context.nameBuilder.createElementName(e.getTagName()),beanInfo) );
            nil |= e.isNillable();
        }
        
        nullTagName = context.nameBuilder.createElementName(nt);

        nillable = nil;
    }

    @Override
    public void wrapUp() {
        super.wrapUp();
        prop = null;
    }

    @Override
    public void reset(BeanT bean) throws AccessorException {
        acc.set(bean,null);
    }

    @Override
    public String getIdValue(BeanT beanT) {
        return null;
    }

    @Override
    public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        ValueT v = acc.get(o);
        if (v!=null) {
            Class vtype = v.getClass();
            TagAndType tt=typeNames.get(vtype); // quick way that usually works

            if(tt==null) {// slow way that always works
                for (Map.Entry<Class,TagAndType> e : typeNames.entrySet()) {
                    if(e.getKey().isAssignableFrom(vtype)) {
                        tt = e.getValue();
                        break;
                    }
                }
            }

            boolean addNilDecl = (o instanceof JAXBElement) && ((JAXBElement)o).isNil();
            if(tt==null) {
                // actually this is an error, because the actual type was not a sub-type
                // of any of the types specified in the annotations,
                // but for the purpose of experimenting with simple type substitution,
                // it's convenient to marshal this anyway (for example so that classes
                // generated from simple types like String can be marshalled as expected.)
                w.startElement(typeNames.values().iterator().next().tagName,null);
                w.childAsXsiType(v,fieldName,w.grammar.getBeanInfo(Object.class), addNilDecl && nillable);
            } else {
                w.startElement(tt.tagName,null);
                w.childAsXsiType(v,fieldName,tt.beanInfo, addNilDecl && nillable);
            }
            w.endElement();
        } else if (nillable) {
            w.startElement(nullTagName,null);
            w.writeXsiNilTrue();
            w.endElement();
        }
    }

    @Override
    public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
        JAXBContextImpl context = chain.context;

        for (TypeRef<Type,Class> e : prop.getTypes()) {
            JaxBeanInfo bi = context.getOrCreate((RuntimeTypeInfo) e.getTarget());
            // if the expected Java type is already final, type substitution won't really work anyway.
            // this also traps cases like trying to substitute xsd:long element with xsi:type='xsd:int'
            Loader l = bi.getLoader(context,!Modifier.isFinal(bi.jaxbType.getModifiers()));
            if(e.getDefaultValue()!=null)
                l = new DefaultValueLoaderDecorator(l,e.getDefaultValue());
            if(nillable || chain.context.allNillable)
                l = new XsiNilLoader.Single(l,acc);
            handlers.put( e.getTagName(), new ChildLoader(l,acc));
        }
    }

    @Override
    public PropertyKind getKind() {
        return PropertyKind.ELEMENT;
    }

    @Override
    public Accessor getElementPropertyAccessor(String nsUri, String localName) {
        for( QName n : acceptedElements) {
            if(n.getNamespaceURI().equals(nsUri) && n.getLocalPart().equals(localName))
                return acc;
        }
        return null;
    }

}
