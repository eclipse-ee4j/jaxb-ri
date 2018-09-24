/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xmlpp;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Introduces additional NLs after cetrain end tags to enhance
 * readability.
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
class Spacer extends XMLFilterImpl {
    
    private final Set spacedTags = new HashSet();
    
    Spacer( XMLReader parent ) {
        this();
        setParent(parent);
    }
    
    Spacer() {
        // TODO: move this build.xml-specific logic to outside this class.
        spacedTags.add("target");
        spacedTags.add("taskdef");
    }

    public void endElement(String uri, String localName, String qName)
        throws SAXException {
        super.endElement(uri, localName, qName);
        if( spacedTags.contains(localName) )
            characters("\n".toCharArray(),0,1);
        
    }

}
