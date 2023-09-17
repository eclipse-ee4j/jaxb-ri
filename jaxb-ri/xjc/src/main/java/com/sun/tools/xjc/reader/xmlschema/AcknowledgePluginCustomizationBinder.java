/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXPluginCustomization;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.xml.xsom.*;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Mark as acknowledge every <code>BIXPluginCustomization</code>
 * seen in XSComponent and sub-tree.
 */
public class AcknowledgePluginCustomizationBinder extends BindingComponent implements XSVisitor, XSSimpleTypeVisitor {

    private final Set<XSComponent> visitedComponents = new HashSet<>();

    public AcknowledgePluginCustomizationBinder() {
    }

    /**
     * Acknowledge plugin customizations on this component
     * and returns true if this is the first time this
     * component is visited.
     */
    private boolean acknowledge( XSComponent c ) {
        if (!visitedComponents.add(c)) {
            return false;   // already processed
        }

        XSAnnotation ann = c.getAnnotation(false);
        if (ann != null && ann.getAnnotation() instanceof BindInfo) {
            for (BIDeclaration decl : ((BindInfo) ann.getAnnotation()).getDecls()) {
                if (decl instanceof BIXPluginCustomization) {
                    decl.markAsAcknowledged();
                }
            }
        }

        return true;
    }
    
    @Override
    public void annotation(XSAnnotation ann) {
    }

    @Override
    public void attGroupDecl(XSAttGroupDecl decl) {
        if (acknowledge(decl)) {
            attContainer(decl);
        }
    }

    @Override
    public void attributeDecl(XSAttributeDecl decl) {
        if (acknowledge(decl)) {
            decl.getType().visit((XSSimpleTypeVisitor) this);
        }
    }

    @Override
    public void attributeUse(XSAttributeUse use) {
        if (acknowledge(use)) {
            use.getDecl().visit(this);
        }
    }

    @Override
    public void complexType(XSComplexType type) {
        if (acknowledge(type)) {
            // don't need to acknowledge the base type -- it must be global, thus
            // it is covered already
            type.getContentType().visit(this);
            attContainer(type);
        }
    }

    private void attContainer( XSAttContainer cont ) {
        for (Iterator<? extends XSAttGroupDecl> itr = cont.iterateAttGroups(); itr.hasNext(); ) {
            itr.next().visit(this);
        }

        for (Iterator<? extends XSAttributeUse> itr = cont.iterateDeclaredAttributeUses(); itr.hasNext(); ) {
            itr.next().visit(this);
        }

        XSWildcard wc = cont.getAttributeWildcard();
        if (wc!=null) {
            wc.visit(this);
        }
    }

    @Override
    public void schema(XSSchema schema) {
        acknowledge(schema);
    }

    @Override
    public void facet(XSFacet facet) {
        acknowledge(facet);
    }

    @Override
    public void notation(XSNotation notation) {
        acknowledge(notation);
    }

    @Override
    public void wildcard(XSWildcard wc) {
        acknowledge(wc);
    }

    @Override
    public void modelGroupDecl(XSModelGroupDecl decl) {
        if (acknowledge(decl)) {
            decl.getModelGroup().visit(this);
        }
    }

    @Override
    public void modelGroup(XSModelGroup group) {
        if (acknowledge(group)) {
            for ( int i=0; i<group.getSize(); i++)
                group.getChild(i).visit(this);
        }
    }

    @Override
    public void elementDecl(XSElementDecl decl) {
        if (acknowledge(decl)) {
            decl.getType().visit(this);
            for (XSIdentityConstraint id : decl.getIdentityConstraints()) {
                id.visit(this);
            }
        }
    }

    @Override
    public void simpleType(XSSimpleType simpleType) {
        if (acknowledge(simpleType)) {
            simpleType.visit((XSSimpleTypeVisitor) this);
        }
    }

    @Override
    public void particle(XSParticle particle) {
        if (acknowledge(particle)) {
            particle.getTerm().visit(this);
        }
    }

    @Override
    public void empty(XSContentType empty) {
        acknowledge(empty);
    }

    @Override
    public void listSimpleType(XSListSimpleType type) {
        if (acknowledge(type)) {
            type.getItemType().visit((XSSimpleTypeVisitor) this);
        }
    }

    @Override
    public void restrictionSimpleType(XSRestrictionSimpleType type) {
        if (acknowledge(type)) {
            type.getBaseType().visit(this);
        }
    }

    @Override
    public void unionSimpleType(XSUnionSimpleType type) {
        if (acknowledge(type)) {
            for (int i=0; i<type.getMemberSize(); i++) {
                type.getMember(i).visit((XSSimpleTypeVisitor) this);
            }
        }
    }

    @Override
    public void identityConstraint(XSIdentityConstraint id) {
        if (acknowledge(id)) {
            id.getSelector().visit(this);
            for (XSXPath xp : id.getFields()) {
                xp.visit(this);
            }
        }
    }

    @Override
    public void xpath(XSXPath xp) {
        acknowledge(xp);
    }
}
