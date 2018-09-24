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

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XmlString;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

public class AttributeUseImpl extends ComponentImpl implements XSAttributeUse
{
    public AttributeUseImpl( SchemaDocumentImpl owner, AnnotationImpl ann, Locator loc, ForeignAttributesImpl fa, Ref.Attribute _decl,
        XmlString def, XmlString fixed, boolean req ) {
        
        super(owner,ann,loc,fa);
        
        this.att = _decl;
        this.defaultValue = def;
        this.fixedValue = fixed;
        this.required = req;
    }
    
    private final Ref.Attribute att;    
    public XSAttributeDecl getDecl() { return att.getAttribute(); }
    
    private final XmlString defaultValue;
    public XmlString getDefaultValue() {
        if( defaultValue!=null )    return defaultValue;
        else                        return getDecl().getDefaultValue();
    }
    
    private final XmlString fixedValue;
    public XmlString getFixedValue() {
        if( fixedValue!=null )      return fixedValue;
        else                        return getDecl().getFixedValue();
    }
    
    private final boolean required;
    public boolean isRequired() { return required; }
    
    public Object apply( XSFunction f ) {
        return f.attributeUse(this);
    }
    public void visit( XSVisitor v ) {
        v.attributeUse(this);
    }
}
