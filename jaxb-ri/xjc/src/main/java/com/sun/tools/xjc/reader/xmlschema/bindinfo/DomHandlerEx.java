/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.annotation.DomHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.glassfish.jaxb.core.marshaller.SAX2DOMEx;

import org.glassfish.jaxb.core.v2.util.XmlFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * {@link DomHandler} that produces a W3C DOM but with a location information.
 *
 * @author Kohsuke Kawaguchi
 */
final class DomHandlerEx implements DomHandler<DomHandlerEx.DomAndLocation,DomHandlerEx.ResultImpl> {

    public static final class DomAndLocation {
        public final Element element;
        public final Locator loc;

        public DomAndLocation(Element element, Locator loc) {
            this.element = element;
            this.loc = loc;
        }
    }

    public ResultImpl createUnmarshaller(ValidationEventHandler errorHandler) {
        return new ResultImpl();
    }

    public DomAndLocation getElement(ResultImpl r) {
        return new DomAndLocation( ((Document)r.s2d.getDOM()).getDocumentElement(), r.location );
    }

    public Source marshal(DomAndLocation domAndLocation, ValidationEventHandler errorHandler) {
        return new DOMSource(domAndLocation.element);
    }

    public static final class ResultImpl extends SAXResult {
        final SAX2DOMEx s2d;

        Locator location = null;

        ResultImpl() {
            try {
                DocumentBuilderFactory factory = XmlFactory.createDocumentBuilderFactory(false); // safe - only used for BI
                s2d = new SAX2DOMEx(factory);
            } catch (ParserConfigurationException e) {
                throw new AssertionError(e);    // impossible
            }

            XMLFilterImpl f = new XMLFilterImpl() {
                @Override
                public void setDocumentLocator(Locator locator) {
                    super.setDocumentLocator(locator);
                    location = new LocatorImpl(locator);
                }
            };
            f.setContentHandler(s2d);

            setHandler(f);
        }

    }
}
