/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.bind.api.CompositeStructure;
import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;

import org.xml.sax.SAXException;

/**
 * @author Kohsuke Kawaguchi
 */
public class CompositeStructureBeanInfo extends JaxBeanInfo<CompositeStructure> {
    public CompositeStructureBeanInfo(JAXBContextImpl context) {
        super(context,null, CompositeStructure.class,false,true,false);
    }

    public String getElementNamespaceURI(CompositeStructure o) {
        throw new UnsupportedOperationException();
    }

    public String getElementLocalName(CompositeStructure o) {
        throw new UnsupportedOperationException();
    }

    public CompositeStructure createInstance(UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException {
        throw new UnsupportedOperationException();
    }

    public boolean reset(CompositeStructure o, UnmarshallingContext context) throws SAXException {
        throw new UnsupportedOperationException();
    }

    public String getId(CompositeStructure o, XMLSerializer target) throws SAXException {
        return null;
    }

    public Loader getLoader(JAXBContextImpl context, boolean typeSubstitutionCapable) {
        // no unmarshaller support for this.
        throw new UnsupportedOperationException();
    }

    public void serializeRoot(CompositeStructure o, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        target.reportError(
                new ValidationEventImpl(
                        ValidationEvent.ERROR,
                        Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(o.getClass().getName()),
                        null,
                        null));
    }

    public void serializeURIs(CompositeStructure o, XMLSerializer target) throws SAXException {
        // noop
    }

    public void serializeAttributes(CompositeStructure o, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        // noop
    }

    public void serializeBody(CompositeStructure o, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        int len = o.bridges.length;
        for( int i=0; i<len; i++ ) {
            Object value = o.values[i];
            InternalBridge bi = (InternalBridge)o.bridges[i];
            bi.marshal( value, target );
        }
    }

    public Transducer<CompositeStructure> getTransducer() {
        return null;
    }
}
