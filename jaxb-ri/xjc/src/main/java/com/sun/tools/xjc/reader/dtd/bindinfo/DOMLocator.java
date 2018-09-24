/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd.bindinfo;

import org.w3c.dom.Element;
import org.xml.sax.Locator;

class DOMLocator {
    private static final String locationNamespace =
        "http://www.sun.com/xmlns/jaxb/dom-location";
    private static final String systemId    = "systemid";
    private static final String column      = "column";
    private static final String line        = "line";
    
    /** Sets the location information to a specified element. */
    public static void setLocationInfo( Element e, Locator loc ) {
        e.setAttributeNS(locationNamespace,"loc:"+systemId,loc.getSystemId());
        e.setAttributeNS(locationNamespace,"loc:"+column,Integer.toString(loc.getLineNumber()));
        e.setAttributeNS(locationNamespace,"loc:"+line,Integer.toString(loc.getColumnNumber()));
    }
    
    /**
     * Gets the location information from an element.
     * 
     * <p>
     * For this method to work, the setLocationInfo method has to be
     * called before.
     */
    public static Locator getLocationInfo( final Element e ) {
        if(DOMUtil.getAttribute(e,locationNamespace,systemId)==null)
            return null;    // no location information
        
        return new Locator(){
            public int getLineNumber() {
                return Integer.parseInt(DOMUtil.getAttribute(e,locationNamespace,line));
            }
            public int getColumnNumber() {
                return Integer.parseInt(DOMUtil.getAttribute(e,locationNamespace,column));
            }
            public String getSystemId() {
                return DOMUtil.getAttribute(e,locationNamespace,systemId);
            }
            // we are not interested in PUBLIC ID.
            public String getPublicId() { return null; }
        };
    }
}
