/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.internalizer;

import java.util.Set;

import com.sun.tools.xjc.reader.Const;
import org.glassfish.jaxb.core.marshaller.SAX2DOMEx;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/**
 * Builds DOM while keeping the location information.
 * 
 * <p>
 * This class also looks for outer most {@code <jaxb:bindings>}
 * customizations.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class DOMBuilder extends SAX2DOMEx {
    /**
     * Grows a DOM tree under the given document, and
     * stores location information to the given table.
     * 
     * @param outerMostBindings
     *      This set will receive newly found outermost
     *      jaxb:bindings customizations.
     */
    public DOMBuilder( Document dom, LocatorTable ltable, Set outerMostBindings ) {
        super( dom );
        this.locatorTable = ltable;
        this.outerMostBindings = outerMostBindings;
    }
    
    /** Location information will be stored into this object. */
    private final LocatorTable locatorTable;
    
    private final Set outerMostBindings;
    
    private Locator locator;
    
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
        super.setDocumentLocator(locator);
    }
    

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        super.startElement(namespaceURI, localName, qName, atts);
        
        Element e = getCurrentElement();
        locatorTable.storeStartLocation( e, locator );
        
        // check if this element is an outer-most <jaxb:bindings>
        if( Const.JAXB_NSURI.equals(e.getNamespaceURI())
        &&  "bindings".equals(e.getLocalName()) ) {
            
            // if this is the root node (meaning that this file is an
            // external binding file) or if the parent is XML Schema element
            // (meaning that this is an "inlined" external binding)
            Node p = e.getParentNode();
            if( p instanceof Document
            ||( p instanceof Element && !e.getNamespaceURI().equals(p.getNamespaceURI()))) {
                outerMostBindings.add(e);   // remember this value
            }
        }
    }
    
    public void endElement(String namespaceURI, String localName, String qName) {
        locatorTable.storeEndLocation( getCurrentElement(), locator );
        super.endElement(namespaceURI, localName, qName);
    }
}
