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
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

import com.sun.tools.xjc.reader.gbind.Choice;
import com.sun.tools.xjc.reader.gbind.Element;
import com.sun.tools.xjc.reader.gbind.Expression;
import com.sun.tools.xjc.reader.gbind.OneOrMore;
import com.sun.tools.xjc.reader.gbind.Sequence;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.visitor.XSTermFunction;
import java.math.BigInteger;

/**
 * Visits {@link XSParticle} and creates a corresponding {@link Expression} tree.
 * @author Kohsuke Kawaguchi
 */
public final class ExpressionBuilder implements XSTermFunction<Expression> {

    public static Expression createTree(XSParticle p) {
        return new ExpressionBuilder().particle(p);
    }

    private ExpressionBuilder() {}

    /**
     * Wildcard instance needs to be consolidated to one,
     * and this is such instance (if any.)
     */
    private GWildcardElement wildcard = null;

    private final Map<QName,GElementImpl> decls = new HashMap<>();

    private XSParticle current;

    /**
     * We can only have one {@link XmlAnyElement} property,
     * so all the wildcards need to be treated as one node.
     */
    public Expression wildcard(XSWildcard wc) {
        if(wildcard==null)
            wildcard = new GWildcardElement();
        wildcard.merge(wc);
        wildcard.particles.add(current);
        return wildcard;
    }

    public Expression modelGroupDecl(XSModelGroupDecl decl) {
        return modelGroup(decl.getModelGroup());
    }

    public Expression modelGroup(XSModelGroup group) {
        XSModelGroup.Compositor comp = group.getCompositor();
        if(comp==XSModelGroup.CHOICE) {
            // empty choice is not epsilon, but empty set,
            // so this initial value is incorrect. But this
            // kinda works.
            // properly handling empty set requires more work.
            Expression e = Expression.EPSILON;
            for (XSParticle p : group.getChildren()) {
                if(e==null)     e = particle(p);
                else            e = new Choice(e,particle(p));
            }
            return e;
        } else {
            Expression e = Expression.EPSILON;
            for (XSParticle p : group.getChildren()) {
                if(e==null)     e = particle(p);
                else            e = new Sequence(e,particle(p));
            }
            return e;
        }
    }

    public Element elementDecl(XSElementDecl decl) {
        QName n = BGMBuilder.getName(decl);

        GElementImpl e = decls.get(n);
        if(e==null)
            decls.put(n,e=new GElementImpl(n,decl));

        e.particles.add(current);
        assert current.getTerm()==decl;

        return e;
    }

    public Expression particle(XSParticle p) {
        current = p;
        Expression e = p.getTerm().apply(this);

        if(p.isRepeated())
            e = new OneOrMore(e);

        if (BigInteger.ZERO.equals(p.getMinOccurs()))
            e = new Choice(e,Expression.EPSILON);

        return e;
    }

}
