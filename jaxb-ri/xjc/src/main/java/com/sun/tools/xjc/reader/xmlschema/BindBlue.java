/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * This is the first color invoked from the parent component.
 *
 * @author Kohsuke Kawaguchi
 */
final class BindBlue extends ColorBinder {

    public void complexType(XSComplexType ct) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    public void elementDecl(XSElementDecl e) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }




    public void wildcard(XSWildcard xsWildcard) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    public void attGroupDecl(XSAttGroupDecl xsAttGroupDecl) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void attributeDecl(XSAttributeDecl xsAttributeDecl) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void attributeUse(XSAttributeUse use) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void modelGroup(XSModelGroup xsModelGroup) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void particle(XSParticle xsParticle) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void empty(XSContentType xsContentType) {
        // TODO
        throw new UnsupportedOperationException();
    }


/**
 * Components that always map to a type
 */
    public void simpleType(XSSimpleType type) {
        throw new IllegalStateException();
    }
}
