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
import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSVariety;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import org.xml.sax.Locator;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ListSimpleTypeImpl extends SimpleTypeImpl implements XSListSimpleType
{
    public ListSimpleTypeImpl( SchemaDocumentImpl _parent,
                               AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa,
                               String _name, boolean _anonymous, Set<XSVariety> finalSet,
                               Ref.SimpleType _itemType ) {

        super(_parent,_annon,_loc,_fa,_name,_anonymous, finalSet,
            _parent.getSchema().parent.anySimpleType);

        this.itemType = _itemType;
    }

    private final Ref.SimpleType itemType;
    public XSSimpleType getItemType() { return itemType.getType(); }

    public void visit( XSSimpleTypeVisitor visitor ) {
        visitor.listSimpleType(this);
    }
    public Object apply( XSSimpleTypeFunction function ) {
        return function.listSimpleType(this);
    }

    // list type by itself doesn't have any facet. */
    public XSFacet getFacet( String name ) { return null; }
    public List<XSFacet> getFacets( String name ) { return Collections.EMPTY_LIST; }

    public XSVariety getVariety() { return XSVariety.LIST; }

    public XSSimpleType getPrimitiveType() { return null; }

    public XSListSimpleType getBaseListType() {return this;}

    public boolean isList() { return true; }
    public XSListSimpleType asList() { return this; }
}
