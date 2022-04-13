/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.util;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.visitor.XSFunction;

/**
 * Utility implementation of {@link XSFunction} that returns
 * {@link Boolean} to find something from schema objects.
 * 
 * <p>
 * This implementation returns <code>Boolean.FALSE</code> from
 * all of the methods. The derived class is expected to override
 * some of the methods to actually look for something.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XSFinder implements XSFunction<Boolean> {

    /**
     * Default constructor.
     */
    public XSFinder() {}

    /**
     * Invokes this object as a visitor with the specified component.
     */
    public final boolean find( XSComponent c ) {
        return c.apply(this);
    }
    
    public Boolean annotation(XSAnnotation ann) {
        return Boolean.FALSE;
    }

    public Boolean attGroupDecl(XSAttGroupDecl decl) {
        return Boolean.FALSE;
    }

    public Boolean attributeDecl(XSAttributeDecl decl) {
        return Boolean.FALSE;
    }

    public Boolean attributeUse(XSAttributeUse use) {
        return Boolean.FALSE;
    }

    public Boolean complexType(XSComplexType type) {
        return Boolean.FALSE;
    }

    public Boolean schema(XSSchema schema) {
        return Boolean.FALSE;
    }

    public Boolean facet(XSFacet facet) {
        return Boolean.FALSE;
    }

    public Boolean notation(XSNotation notation) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.xsom.visitor.XSContentTypeFunction#simpleType(com.sun.xml.xsom.XSSimpleType)
     */
    public Boolean simpleType(XSSimpleType simpleType) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.xsom.visitor.XSContentTypeFunction#particle(com.sun.xml.xsom.XSParticle)
     */
    public Boolean particle(XSParticle particle) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.xsom.visitor.XSContentTypeFunction#empty(com.sun.xml.xsom.XSContentType)
     */
    public Boolean empty(XSContentType empty) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.xsom.visitor.XSTermFunction#wildcard(com.sun.xml.xsom.XSWildcard)
     */
    public Boolean wildcard(XSWildcard wc) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.xsom.visitor.XSTermFunction#modelGroupDecl(com.sun.xml.xsom.XSModelGroupDecl)
     */
    public Boolean modelGroupDecl(XSModelGroupDecl decl) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.xsom.visitor.XSTermFunction#modelGroup(com.sun.xml.xsom.XSModelGroup)
     */
    public Boolean modelGroup(XSModelGroup group) {
        return Boolean.FALSE;
    }

    /**
     * @see com.sun.xml.xsom.visitor.XSTermFunction#elementDecl(com.sun.xml.xsom.XSElementDecl)
     */
    public Boolean elementDecl(XSElementDecl decl) {
        return Boolean.FALSE;
    }

    public Boolean identityConstraint(XSIdentityConstraint decl) {
        return Boolean.FALSE;
    }

    public Boolean xpath(XSXPath xpath) {
        return Boolean.FALSE;
    }
}
