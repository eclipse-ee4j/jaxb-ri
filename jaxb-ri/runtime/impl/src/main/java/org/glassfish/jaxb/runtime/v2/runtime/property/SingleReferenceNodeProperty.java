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
import org.glassfish.jaxb.core.v2.ClassFactory;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.core.v2.model.core.WildcardMode;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeElement;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeReferencePropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.ElementBeanInfoImpl;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.ChildLoader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.WildcardLoader;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.DomHandler;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Kohsuke Kawaguchi
 */
final class SingleReferenceNodeProperty<BeanT,ValueT> extends PropertyImpl<BeanT> {

    private final Accessor<BeanT,ValueT> acc;

    private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap<JaxBeanInfo>();

    private final DomHandler domHandler;
    private final WildcardMode wcMode;

    public SingleReferenceNodeProperty(JAXBContextImpl context, RuntimeReferencePropertyInfo prop) {
        super(context,prop);
        acc = prop.getAccessor().optimize(context);

        for (RuntimeElement e : prop.getElements()) {
            expectedElements.put( e.getElementName(), context.getOrCreate(e) );
        }

        if(prop.getWildcard()!=null) {
            domHandler = (DomHandler) ClassFactory.create(prop.getDOMHandler());
            wcMode = prop.getWildcard();
        } else {
            domHandler = null;
            wcMode = null;
        }
    }

    public void reset(BeanT bean) throws AccessorException {
        acc.set(bean,null);
    }

    public String getIdValue(BeanT beanT) {
        return null;
    }

    public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        ValueT v = acc.get(o);
        if(v!=null) {
            try {
                JaxBeanInfo bi = w.grammar.getBeanInfo(v,true);
                if(bi.jaxbType==Object.class && domHandler!=null)
                    // even if 'v' is a DOM node, it always derive from Object,
                    // so the getBeanInfo returns BeanInfo for Object
                    w.writeDom(v,domHandler,o,fieldName);
                else
                    bi.serializeRoot(v,w);
            } catch (JAXBException e) {
                w.reportError(fieldName,e);
                // recover by ignoring this property
            }
        }
    }

    public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
        for (QNameMap.Entry<JaxBeanInfo> n : expectedElements.entrySet())
            handlers.put(n.nsUri,n.localName, new ChildLoader(n.getValue().getLoader(chain.context,true),acc));

        if(domHandler!=null)
            handlers.put(CATCH_ALL,new ChildLoader(new WildcardLoader(domHandler,wcMode),acc));

    }

    public PropertyKind getKind() {
        return PropertyKind.REFERENCE;
    }

    @Override
    public Accessor getElementPropertyAccessor(String nsUri, String localName) {
        JaxBeanInfo bi = expectedElements.get(nsUri, localName);
        if(bi!=null) {
            if(bi instanceof ElementBeanInfoImpl) {
                final ElementBeanInfoImpl ebi = (ElementBeanInfoImpl) bi;
                // a JAXBElement. We need to handle JAXBElement for JAX-WS
                return new Accessor<BeanT,Object>(ebi.expectedType) {
                    public Object get(BeanT bean) throws AccessorException {
                        ValueT r = acc.get(bean);
                        if(r instanceof JAXBElement) {
                            return ((JAXBElement)r).getValue();
                        } else
                            // this is sloppy programming, but hey...
                            return r;
                    }

                    public void set(BeanT bean, Object value) throws AccessorException {
                        if(value!=null) {
                            try {
                                value = ebi.createInstanceFromValue(value);
                            } catch (IllegalAccessException e) {
                                throw new AccessorException(e);
                            } catch (InvocationTargetException e) {
                                throw new AccessorException(e);
                            } catch (InstantiationException e) {
                                throw new AccessorException(e);
                            }
                        }
                        acc.set(bean,(ValueT)value);
                    }
                };
            } else {
                // a custom element type, like @XmlRootElement class Foo { ... }
                return acc;
            }
        } else
            return null;
    }
}
