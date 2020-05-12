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

import org.glassfish.jaxb.core.v2.runtime.unmarshaller.LocatorEx;
import jakarta.xml.bind.ValidationEventLocator;
import jakarta.xml.bind.helpers.ValidationEventLocatorImpl;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/**
 * @author Kohsuke Kawaguchi
 */
abstract class StAXConnector {
    public abstract void bridge() throws XMLStreamException;


    // event sink
    protected final XmlVisitor visitor;

    protected final UnmarshallingContext context;
    protected final XmlVisitor.TextPredictor predictor;

    private final class TagNameImpl extends TagName {
        public String getQname() {
            return StAXConnector.this.getCurrentQName();
        }
    }

    protected final TagName tagName = new TagNameImpl();

    protected StAXConnector(XmlVisitor visitor) {
        this.visitor = visitor;
        context = visitor.getContext();
        predictor = visitor.getPredictor();
    }

    /**
     * Gets the {@link Location}. Used for implementing the line number information.
     * @return must not null.
     */
    protected abstract Location getCurrentLocation();

    /**
     * Gets the QName of the current element.
     */
    protected abstract String getCurrentQName();

    protected final void handleStartDocument(NamespaceContext nsc) throws SAXException {
        visitor.startDocument(new LocatorEx() {
            public ValidationEventLocator getLocation() {
                return new ValidationEventLocatorImpl(this);
            }
            public int getColumnNumber() {
                return getCurrentLocation().getColumnNumber();
            }
            public int getLineNumber() {
                return getCurrentLocation().getLineNumber();
            }
            public String getPublicId() {
                return getCurrentLocation().getPublicId();
            }
            public String getSystemId() {
                return getCurrentLocation().getSystemId();
            }
        },nsc);
    }

    protected final void handleEndDocument() throws SAXException {
        visitor.endDocument();
    }

    protected static String fixNull(String s) {
        if(s==null) return "";
        else        return s;
    }

    protected final String getQName(String prefix, String localName) {
        if(prefix==null || prefix.length()==0)
            return localName;
        else
            return prefix + ':' + localName;
    }
}
