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

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

import java.util.Collections;
import java.util.List;

/**
 * {@link XSIdentityConstraint} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
public class IdentityConstraintImpl extends ComponentImpl implements XSIdentityConstraint, Ref.IdentityConstraint {

    private XSElementDecl parent;
    private final short category;
    private final String name;
    private final XSXPath selector;
    private final List<XSXPath> fields;
    private final Ref.IdentityConstraint refer;

    public IdentityConstraintImpl(SchemaDocumentImpl _owner, AnnotationImpl _annon, Locator _loc,
        ForeignAttributesImpl fa, short category, String name, XPathImpl selector,
        List<XPathImpl> fields, Ref.IdentityConstraint refer) {

        super(_owner, _annon, _loc, fa);
        this.category = category;
        this.name = name;
        this.selector = selector;
        selector.setParent(this);
        this.fields = (List<XSXPath>) Collections.unmodifiableList((List<? extends XSXPath>)fields);
        for( XPathImpl xp : fields )
            xp.setParent(this);
        this.refer = refer;
    }


    @Override
    public void visit(XSVisitor visitor) {
        visitor.identityConstraint(this);
    }

    @Override
    public <T> T apply(XSFunction<T> function) {
        return function.identityConstraint(this);
    }

    public void setParent(ElementDecl parent) {
        this.parent = parent;
        parent.getOwnerSchema().addIdentityConstraint(this);
    }

    @Override
    public XSElementDecl getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTargetNamespace() {
        return getParent().getTargetNamespace();
    }

    @Override
    public short getCategory() {
        return category;
    }

    @Override
    public XSXPath getSelector() {
        return selector;
    }

    @Override
    public List<XSXPath> getFields() {
        return fields;
    }

    @Override
    public XSIdentityConstraint getReferencedKey() {
        if(category==KEYREF)
            return refer.get();
        else
            throw new IllegalStateException("not a keyref");
    }

    @Override
    public XSIdentityConstraint get() {
        return this;
    }
}
