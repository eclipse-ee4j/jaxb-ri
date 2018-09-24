/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.impl.IdentityConstraintImpl;
import com.sun.xml.xsom.impl.XPathImpl;

/**
 * Visitor for {@link com.sun.xml.xsom.XSComponent}
 */
public interface XSVisitor extends XSTermVisitor, XSContentTypeVisitor {
    void annotation( XSAnnotation ann );
    void attGroupDecl( XSAttGroupDecl decl );
    void attributeDecl( XSAttributeDecl decl );
    void attributeUse( XSAttributeUse use );
    void complexType( XSComplexType type );
    void schema( XSSchema schema );
//    void schemaSet( XSSchemaSet schema );
    void facet( XSFacet facet );
    void notation( XSNotation notation );
    void identityConstraint( XSIdentityConstraint decl);
    void xpath(XSXPath xp);
}
