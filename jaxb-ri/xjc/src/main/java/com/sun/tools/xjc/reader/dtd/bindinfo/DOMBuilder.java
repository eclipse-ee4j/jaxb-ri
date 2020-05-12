/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd.bindinfo;

import javax.xml.parsers.ParserConfigurationException;

import org.glassfish.jaxb.core.marshaller.SAX2DOMEx;

import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/**
 * @author Kohsuke Kawaguchi
 */
final class DOMBuilder extends SAX2DOMEx {
    private Locator locator;

    public DOMBuilder(DocumentBuilderFactory f) throws ParserConfigurationException {
        super(f);
    }
    
    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }

    @Override
    public void startElement(String namespace, String localName, String qName, Attributes attrs) {
        super.startElement(namespace, localName, qName, attrs);
        DOMLocator.setLocationInfo(getCurrentElement(),locator);
    }
}
