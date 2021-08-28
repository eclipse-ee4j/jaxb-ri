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

import com.sun.tools.xjc.model.CElement;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;

/**
 * {@link ClassBinder} that delegates the call to another {@link ClassBinder}.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
abstract class ClassBinderFilter implements ClassBinder {
    private final ClassBinder core;

    protected ClassBinderFilter(ClassBinder core) {
        this.core = core;
    }

    @Override
    public CElement annotation(XSAnnotation xsAnnotation) {
        return core.annotation(xsAnnotation);
    }

    @Override
    public CElement attGroupDecl(XSAttGroupDecl xsAttGroupDecl) {
        return core.attGroupDecl(xsAttGroupDecl);
    }

    @Override
    public CElement attributeDecl(XSAttributeDecl xsAttributeDecl) {
        return core.attributeDecl(xsAttributeDecl);
    }

    @Override
    public CElement attributeUse(XSAttributeUse xsAttributeUse) {
        return core.attributeUse(xsAttributeUse);
    }

    @Override
    public CElement complexType(XSComplexType xsComplexType) {
        return core.complexType(xsComplexType);
    }

    @Override
    public CElement schema(XSSchema xsSchema) {
        return core.schema(xsSchema);
    }

    @Override
    public CElement facet(XSFacet xsFacet) {
        return core.facet(xsFacet);
    }

    @Override
    public CElement notation(XSNotation xsNotation) {
        return core.notation(xsNotation);
    }

    @Override
    public CElement simpleType(XSSimpleType xsSimpleType) {
        return core.simpleType(xsSimpleType);
    }

    @Override
    public CElement particle(XSParticle xsParticle) {
        return core.particle(xsParticle);
    }

    @Override
    public CElement empty(XSContentType xsContentType) {
        return core.empty(xsContentType);
    }

    @Override
    public CElement wildcard(XSWildcard xsWildcard) {
        return core.wildcard(xsWildcard);
    }

    @Override
    public CElement modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
        return core.modelGroupDecl(xsModelGroupDecl);
    }

    @Override
    public CElement modelGroup(XSModelGroup xsModelGroup) {
        return core.modelGroup(xsModelGroup);
    }

    @Override
    public CElement elementDecl(XSElementDecl xsElementDecl) {
        return core.elementDecl(xsElementDecl);
    }

    @Override
    public CElement identityConstraint(XSIdentityConstraint xsIdentityConstraint) {
        return core.identityConstraint(xsIdentityConstraint);
    }

    @Override
    public CElement xpath(XSXPath xsxPath) {
        return core.xpath(xsxPath);
    }
}
