/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.internalizer;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

/**
 * Stores {@link Locator} objects for every {@link Element}.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class LocatorTable {
    /** Locations of the start element. */
    private final Map startLocations = new HashMap();
    
    /** Locations of the end element. */
    private final Map endLocations = new HashMap();
    
    public void storeStartLocation( Element e, Locator loc ) {
        startLocations.put(e,new LocatorImpl(loc));
    }
    
    public void storeEndLocation( Element e, Locator loc ) {
        endLocations.put(e,new LocatorImpl(loc));
    }
    
    public Locator getStartLocation( Element e ) {
        return (Locator)startLocations.get(e);
    }
    
    public Locator getEndLocation( Element e ) {
        return (Locator)endLocations.get(e);
    }
}
