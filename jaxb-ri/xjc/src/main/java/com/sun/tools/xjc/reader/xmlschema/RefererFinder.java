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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.visitor.XSVisitor;

/**
 * Finds which {@link XSComponent}s refer to which {@link XSComplexType}s.
 *
 * @author Kohsuke Kawaguchi
 */
final class RefererFinder implements XSVisitor {
    private final Set<Object> visited = new HashSet<Object>();

    private final Map<XSComponent,Set<XSComponent>> referers = new HashMap<XSComponent,Set<XSComponent>>();

    public Set<XSComponent> getReferer(XSComponent src) {
        Set<XSComponent> r = referers.get(src);
        if(r==null) return Collections.emptySet();
        return r;
    }


    public void schemaSet(XSSchemaSet xss) {
        if(!visited.add(xss))       return;

        for (XSSchema xs : xss.getSchemas()) {
            schema(xs);
        }
    }

    public void schema(XSSchema xs) {
        if(!visited.add(xs))       return;

        for (XSComplexType ct : xs.getComplexTypes().values()) {
            complexType(ct);
        }

        for (XSElementDecl e : xs.getElementDecls().values()) {
            elementDecl(e);
        }
    }

    public void elementDecl(XSElementDecl e) {
        if(!visited.add(e))       return;

        refer(e,e.getType());
        e.getType().visit(this);
    }

    public void complexType(XSComplexType ct) {
        if(!visited.add(ct))       return;

        refer(ct,ct.getBaseType());
        ct.getBaseType().visit(this);
        ct.getContentType().visit(this);
    }

    public void modelGroupDecl(XSModelGroupDecl decl) {
        if(!visited.add(decl))  return;

        modelGroup(decl.getModelGroup());
    }

    public void modelGroup(XSModelGroup group) {
        if(!visited.add(group))  return;

        for (XSParticle p : group.getChildren()) {
            particle(p);
        }
    }

    public void particle(XSParticle particle) {
        // since the particle method is side-effect free, no need to check for double-visit.
        particle.getTerm().visit(this);
    }


    // things we don't care
    public void simpleType(XSSimpleType simpleType) {}
    public void annotation(XSAnnotation ann) {}
    public void attGroupDecl(XSAttGroupDecl decl) {}
    public void attributeDecl(XSAttributeDecl decl) {}
    public void attributeUse(XSAttributeUse use) {}
    public void facet(XSFacet facet) {}
    public void notation(XSNotation notation) {}
    public void identityConstraint(XSIdentityConstraint decl) {}
    public void xpath(XSXPath xp) {}
    public void wildcard(XSWildcard wc) {}
    public void empty(XSContentType empty) {}

    /**
     * Called for each reference to record the fact.
     *
     * So far we only care about references to types.
     */
    private void refer(XSComponent source, XSType target) {
        Set<XSComponent> r = referers.get(target);
        if(r==null) {
            r = new HashSet<XSComponent>();
            referers.put(target,r);
        }
        r.add(source);
    }
}
