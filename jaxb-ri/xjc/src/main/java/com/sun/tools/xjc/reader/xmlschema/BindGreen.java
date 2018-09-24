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

import java.util.Iterator;

import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeFieldBuilder;
import com.sun.xml.xsom.XSAttContainer;
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
public final class BindGreen extends ColorBinder {

    private final ComplexTypeFieldBuilder ctBuilder = Ring.get(ComplexTypeFieldBuilder.class);

    public void attGroupDecl(XSAttGroupDecl ag) {
        attContainer(ag);
    }

    public void attContainer(XSAttContainer cont) {
        // inline
        Iterator itr = cont.iterateDeclaredAttributeUses();
        while(itr.hasNext())
            builder.ying((XSAttributeUse)itr.next(),cont);
        itr = cont.iterateAttGroups();
        while(itr.hasNext())
            builder.ying((XSAttGroupDecl)itr.next(),cont);

        XSWildcard w = cont.getAttributeWildcard();
        if(w!=null)
            builder.ying(w,cont);
    }

    public void complexType(XSComplexType ct) {
        ctBuilder.build(ct);
    }








    public void attributeDecl(XSAttributeDecl xsAttributeDecl) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    public void wildcard(XSWildcard xsWildcard) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    public void modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    public void modelGroup(XSModelGroup xsModelGroup) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    public void elementDecl(XSElementDecl xsElementDecl) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    public void particle(XSParticle xsParticle) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    public void empty(XSContentType xsContentType) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }


/*

    Components for which ying should yield to purple.

*/
    public void simpleType(XSSimpleType xsSimpleType) {
        // simple type always maps to a type, so this is never possible
        throw new IllegalStateException();
    }

    public void attributeUse(XSAttributeUse use) {
        // attribute use always maps to a property
        throw new IllegalStateException();
    }
}
