/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeArrayInfo;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Loader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Receiver;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.TagName;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallingContext;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link JaxBeanInfo} implementation that binds T[] to a complex type
 * with an element for each item.
 *
 * @author Kohsuke Kawaguchi
 */
final class ArrayBeanInfoImpl  extends JaxBeanInfo {

    private final Class itemType;
    private final JaxBeanInfo itemBeanInfo;
    private Loader loader;

    public ArrayBeanInfoImpl(JAXBContextImpl owner, RuntimeArrayInfo rai) {
        super(owner,rai,rai.getType(), rai.getTypeName(), false, true, false);
        this.itemType = jaxbType.getComponentType();
        this.itemBeanInfo = owner.getOrCreate(rai.getItemType());
    }

    @Override
    protected void link(JAXBContextImpl grammar) {
        getLoader(grammar,false);
        super.link(grammar);
    }

    private final class ArrayLoader extends Loader implements Receiver {
        public ArrayLoader(JAXBContextImpl owner) {
            super(false);
            itemLoader = itemBeanInfo.getLoader(owner,true);
        }

        private final Loader itemLoader;

        @Override
        public void startElement(UnmarshallingContext.State state, TagName ea) {
            state.setTarget(new ArrayList());
        }

        @Override
        public void leaveElement(UnmarshallingContext.State state, TagName ea) {
            state.setTarget(toArray((List)state.getTarget()));
        }

        @Override
        public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
            if(ea.matches("","item")) {
                state.setLoader(itemLoader);
                state.setReceiver(this);
            } else {
                super.childElement(state,ea);
            }
        }

        @Override
        public Collection<QName> getExpectedChildElements() {
            return Collections.singleton(new QName("","item"));
        }

        @Override
        public void receive(UnmarshallingContext.State state, Object o) {
            ((List)state.getTarget()).add(o);
        }
    }

    protected Object toArray( List list ) {
        int len = list.size();
        Object array = Array.newInstance(itemType,len);
        for( int i=0; i<len; i++ )
            Array.set(array,i,list.get(i));
        return array;
    }

    @Override
    public void serializeBody(Object array, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        int len = Array.getLength(array);
        for( int i=0; i<len; i++ )  {
            Object item = Array.get(array,i);
            // TODO: check the namespace URI.
            target.startElement("","item",null,null);
            if(item==null) {
                target.writeXsiNilTrue();
            } else {
                target.childAsXsiType(item,"arrayItem",itemBeanInfo, false);
            }
            target.endElement();
        }
    }

    @Override
    public String getElementNamespaceURI(Object array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getElementLocalName(Object array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object createInstance(UnmarshallingContext context) {
        // we first create a List and then later convert it to an array
        return new ArrayList();
    }

    @Override
    public boolean reset(Object array, UnmarshallingContext context) {
        return false;
    }

    @Override
    public String getId(Object array, XMLSerializer target) {
        return null;
    }

    @Override
    public void serializeAttributes(Object array, XMLSerializer target) {
        // noop
    }

    @Override
    public void serializeRoot(Object array, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        target.reportError(
                new ValidationEventImpl(
                        ValidationEvent.ERROR,
                        Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(array.getClass().getName()),
                        null,
                        null));
    }

    @Override
    public void serializeURIs(Object array, XMLSerializer target) {
        // noop
    }

    @Override
    public Transducer getTransducer() {
        return null;
    }

    @Override
    public Loader getLoader(JAXBContextImpl context, boolean typeSubstitutionCapable) {
        if(loader==null)
            loader = new ArrayLoader(context);

        // type substitution not possible
        return loader;
    }
}
