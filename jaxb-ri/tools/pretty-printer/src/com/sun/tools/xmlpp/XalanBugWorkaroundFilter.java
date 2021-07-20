/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xmlpp;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Fixes error in the SAX events generated by Xalan. 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XalanBugWorkaroundFilter extends XMLFilterImpl {
    
    public XalanBugWorkaroundFilter( ContentHandler next ) {
        this.setContentHandler(next);
    }
    
    public void startElement(String uri, String local, String qname, Attributes arg3) throws SAXException {
        if(uri==null)   uri="";
        super.startElement(uri, local, qname, arg3);
    }

    public void endElement(String uri, String local, String qname) throws SAXException {
        if(uri==null)   uri="";
        super.endElement(uri, local, qname);
    }
}
