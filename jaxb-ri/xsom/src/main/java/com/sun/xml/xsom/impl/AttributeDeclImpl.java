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
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XmlString;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

public class AttributeDeclImpl extends DeclarationImpl implements XSAttributeDecl, Ref.Attribute
{
    public AttributeDeclImpl( SchemaDocumentImpl owner,
        String _targetNamespace, String _name,
        AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, boolean _anonymous,
        XmlString _defValue, XmlString _fixedValue,
        Ref.SimpleType _type ) {
        
        super(owner,_annon,_loc,_fa,_targetNamespace,_name,_anonymous);
        
        if(_name==null) // assertion failed.
            throw new IllegalArgumentException();
        
        this.defaultValue = _defValue;
        this.fixedValue = _fixedValue;
        this.type = _type;
    }
    
    private final Ref.SimpleType type;
    public XSSimpleType getType() { return type.getType(); }

    private final XmlString defaultValue;
    public XmlString getDefaultValue() { return defaultValue; }
    
    private final XmlString fixedValue;
    public XmlString getFixedValue() { return fixedValue; }
    
    public void visit( XSVisitor visitor ) {
        visitor.attributeDecl(this);
    }
    public Object apply( XSFunction function ) {
        return function.attributeDecl(this);
    }


    // Ref.Attribute implementation
    public XSAttributeDecl getAttribute() { return this; }
 }
