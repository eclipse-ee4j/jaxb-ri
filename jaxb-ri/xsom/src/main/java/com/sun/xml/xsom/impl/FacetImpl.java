/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XmlString;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

public class FacetImpl extends ComponentImpl implements XSFacet {
    public FacetImpl( SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa,
        String _name, XmlString _value, boolean _fixed ) {
        
        super(owner,_annon,_loc,_fa);
        
        this.name = _name;
        this.value = _value;
        this.fixed = _fixed;
    }
    
    private final String name;
    public String getName() { return name; }
    
    private final XmlString value;
    public XmlString getValue() { return value; }

    private boolean fixed;
    public boolean isFixed() { return fixed; }
    
    
    public void visit( XSVisitor visitor ) {
        visitor.facet(this);
    }
    public Object apply( XSFunction function ) {
        return function.facet(this);
    }
}
