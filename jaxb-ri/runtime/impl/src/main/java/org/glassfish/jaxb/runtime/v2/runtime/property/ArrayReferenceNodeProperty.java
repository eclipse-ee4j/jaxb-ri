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

import org.glassfish.jaxb.core.v2.ClassFactory;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.core.v2.model.core.WildcardMode;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeElement;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeReferencePropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.ListIterator;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.DomHandler;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.*;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
class ArrayReferenceNodeProperty<BeanT,ListT,ItemT> extends ArrayERProperty<BeanT,ListT,ItemT> {

    /**
     * Expected element names and what class to unmarshal.
     */
    private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap<JaxBeanInfo>();

    private final boolean isMixed;

    private final DomHandler domHandler;
    private final WildcardMode wcMode;

    public ArrayReferenceNodeProperty(JAXBContextImpl p, RuntimeReferencePropertyInfo prop) {
        super(p, prop, prop.getXmlName(), prop.isCollectionNillable());

        for (RuntimeElement e : prop.getElements()) {
            JaxBeanInfo bi = p.getOrCreate(e);
            expectedElements.put( e.getElementName().getNamespaceURI(),e.getElementName().getLocalPart(), bi );
        }

        isMixed = prop.isMixed();

        if(prop.getWildcard()!=null) {
            domHandler = (DomHandler) ClassFactory.create(prop.getDOMHandler());
            wcMode = prop.getWildcard();
        } else {
            domHandler = null;
            wcMode = null;
        }
    }

    protected final void serializeListBody(BeanT o, XMLSerializer w, ListT list) throws IOException, XMLStreamException, SAXException {
        ListIterator<ItemT> itr = lister.iterator(list, w);

        while(itr.hasNext()) {
            try {
                ItemT item = itr.next();
                if (item != null) {
                    if(isMixed && item.getClass()==String.class) {
                        w.text((String)item,null);
                    } else {
                        JaxBeanInfo bi = w.grammar.getBeanInfo(item,true);
                        if(bi.jaxbType==Object.class && domHandler!=null)
                            // even if 'v' is a DOM node, it always derive from Object,
                            // so the getBeanInfo returns BeanInfo for Object
                            w.writeDom(item,domHandler,o,fieldName);
                        else
                            bi.serializeRoot(item,w);
                    }
                }
            } catch (JAXBException e) {
                w.reportError(fieldName,e);
                // recover by ignoring this item
            }
        }
    }

    public void createBodyUnmarshaller(UnmarshallerChain chain, QNameMap<ChildLoader> loaders) {
        final int offset = chain.allocateOffset();

        Receiver recv = new ReceiverImpl(offset);

        for( QNameMap.Entry<JaxBeanInfo> n : expectedElements.entrySet() ) {
            final JaxBeanInfo beanInfo = n.getValue();
            loaders.put(n.nsUri,n.localName,new ChildLoader(beanInfo.getLoader(chain.context,true),recv));
        }

        if(isMixed) {
            // handler for processing mixed contents.
            loaders.put(TEXT_HANDLER,
                new ChildLoader(new MixedTextLoader(recv),null));
        }

        if(domHandler!=null) {
            loaders.put(CATCH_ALL,
                new ChildLoader(new WildcardLoader(domHandler,wcMode),recv));
        }
    }

    private static final class MixedTextLoader extends Loader {

        private final Receiver recv;

        public MixedTextLoader(Receiver recv) {
            super(true);
            this.recv = recv;
        }

        public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
            if(text.length()!=0) // length 0 text is pointless
                recv.receive(state,text.toString());
        }
    }


    public PropertyKind getKind() {
        return PropertyKind.REFERENCE;
    }

    @Override
    public Accessor getElementPropertyAccessor(String nsUri, String localName) {
        // TODO: unwrap JAXBElement
        if(wrapperTagName!=null) {
            if(wrapperTagName.equals(nsUri,localName))
                return acc;
        } else {
            if(expectedElements.containsKey(nsUri,localName))
                return acc;
        }
        return null;
    }
}
