/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSWildcard;

/**
 * @author Kohsuke Kawaguchi
 */
public final class BindYellow extends ColorBinder {
    @Override
    public void complexType(XSComplexType ct) {
    }

    @Override
    public void wildcard(XSWildcard xsWildcard) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    @Override
    public void elementDecl(XSElementDecl xsElementDecl) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    @Override
    public void simpleType(XSSimpleType xsSimpleType) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    @Override
    public void attributeDecl(XSAttributeDecl xsAttributeDecl) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }


/*

    Components that can never map to a type

*/
    @Override
    public void attGroupDecl(XSAttGroupDecl xsAttGroupDecl) {
        throw new IllegalStateException();
    }

    @Override
    public void attributeUse(XSAttributeUse use) {
        throw new IllegalStateException();
    }

    @Override
    public void modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
        throw new IllegalStateException();
    }

    @Override
    public void modelGroup(XSModelGroup xsModelGroup) {
        throw new IllegalStateException();
    }

    @Override
    public void particle(XSParticle xsParticle) {
        throw new IllegalStateException();
    }

    @Override
    public void empty(XSContentType xsContentType) {
        throw new IllegalStateException();
    }
}
