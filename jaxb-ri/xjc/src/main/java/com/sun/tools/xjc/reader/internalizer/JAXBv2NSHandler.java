/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.internalizer;

import com.sun.tools.xjc.reader.Const;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Upgrades the jaxb 2.x customizations in a XML Schema document
 * to version 3.0 and prints warnings.
 *
 */
final class JAXBv2NSHandler extends XMLFilterImpl {

    private Locator locator;

    /**
     * Stores the location of the start tag of the root tag.
     */
    private Locator rootTagStart;

    /** Will be set to true once we hit the root element. */
    private boolean seenRoot = false;

    /** Will be set to true once we hit a JAXB 2.x binding declaration. */
    private boolean seenOldBindings = false;

    /** Will be set to true once we hit a JAXB 2.x binding version attribute declaration. */
    private boolean seenOldBindingsVersion = false;

    public JAXBv2NSHandler(ContentHandler handler, ErrorHandler eh, EntityResolver er) {
        setContentHandler(handler);
        if(eh!=null)    setErrorHandler(eh);
        if(er!=null)    setEntityResolver(er);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if(!seenRoot) {
            // if this is the root element
            seenRoot = true;
            rootTagStart = new LocatorImpl(locator);
        }

        AttributesImpl as = new AttributesImpl();
        for (int i = 0; i < atts.getLength(); i++) {
            if ("http://java.sun.com/xml/ns/jaxb".equals(atts.getURI(i))) {
                if ("version".equals(atts.getLocalName(i))) {
                    as.addAttribute(Const.JAXB_NSURI, atts.getLocalName(i), atts.getQName(i), atts.getType(i), "3.0");
                    seenOldBindingsVersion = true;
                } else {
                    as.addAttribute(Const.JAXB_NSURI, atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i));
                    seenOldBindings = true;
                }
            } else {
                as.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i));
            }
        }
        if ("http://java.sun.com/xml/ns/jaxb".equals(uri)) {
            super.startElement(Const.JAXB_NSURI, localName, qName, as);
            seenOldBindings = true;
        } else {
            super.startElement(uri, localName, qName, as);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        if (seenOldBindings) {
            SAXParseException e = new SAXParseException(
                    Messages.format(Messages.OLD_CUSTOMIZATION), rootTagStart);
            getErrorHandler().warning(e);
        }
        if (seenOldBindingsVersion) {
            SAXParseException e = new SAXParseException(
                    Messages.format(Messages.OLD_CUSTOMIZATION_VERSION), rootTagStart);
            getErrorHandler().warning(e);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }
}
