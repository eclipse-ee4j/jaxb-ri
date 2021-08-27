/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.core.v2.runtime.unmarshaller.LocatorEx;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;

/**
 * {@link XmlVisitor} decorator that interns all string tokens.
 *
 * @author Kohsuke Kawaguchi
 */
public final class InterningXmlVisitor implements XmlVisitor {
    private final XmlVisitor next;

    private final AttributesImpl attributes = new AttributesImpl();

    public InterningXmlVisitor(XmlVisitor next) {
        this.next = next;
    }

    @Override
    public void startDocument(LocatorEx locator, NamespaceContext nsContext) throws SAXException {
        next.startDocument(locator,nsContext);
    }

    @Override
    public void endDocument() throws SAXException {
        next.endDocument();
    }

    @Override
    public void startElement(TagName tagName ) throws SAXException {
        attributes.setAttributes(tagName.atts);
        tagName.atts = attributes;
        tagName.uri = intern(tagName.uri);
        tagName.local = intern(tagName.local);
        next.startElement(tagName);
    }

    @Override
    public void endElement(TagName tagName ) throws SAXException {
        tagName.uri = intern(tagName.uri);
        tagName.local = intern(tagName.local);
        next.endElement(tagName);
    }

    @Override
    public void startPrefixMapping( String prefix, String nsUri ) throws SAXException {
        next.startPrefixMapping(intern(prefix),intern(nsUri));
    }

    @Override
    public void endPrefixMapping( String prefix ) throws SAXException {
        next.endPrefixMapping(intern(prefix));
    }

    @Override
    public void text( CharSequence pcdata ) throws SAXException {
        next.text(pcdata);
    }

    @Override
    public UnmarshallingContext getContext() {
        return next.getContext();
    }
    
    @Override
    public TextPredictor getPredictor() {
        return next.getPredictor();
    }

    private static class AttributesImpl implements Attributes {
        private Attributes core;

        void setAttributes(Attributes att) {
            this.core = att;
        }

        @Override
        public int getIndex(String qName) {
            return core.getIndex(qName);
        }

        @Override
        public int getIndex(String uri, String localName) {
            return core.getIndex(uri, localName);
        }

        @Override
        public int getLength() {
            return core.getLength();
        }

        @Override
        public String getLocalName(int index) {
            return intern(core.getLocalName(index));
        }

        @Override
        public String getQName(int index) {
            return intern(core.getQName(index));
        }

        @Override
        public String getType(int index) {
            return intern(core.getType(index));
        }

        @Override
        public String getType(String qName) {
            return intern(core.getType(qName));
        }

        @Override
        public String getType(String uri, String localName) {
            return intern(core.getType(uri, localName));
        }

        @Override
        public String getURI(int index) {
            return intern(core.getURI(index));
        }

        //
        // since values may vary a lot,
        // we don't (probably shouldn't) intern values.
        //

        @Override
        public String getValue(int index) {
            return core.getValue(index);
        }

        @Override
        public String getValue(String qName) {
            return core.getValue(qName);
        }

        @Override
        public String getValue(String uri, String localName) {
            return core.getValue(uri, localName);
        }
    }

    private static String intern(String s) {
        if(s==null)     return null;
        else            return s.intern();
    }
}
