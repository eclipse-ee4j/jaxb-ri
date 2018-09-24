/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.txw2.model.prop.Prop;
import com.sun.xml.txw2.TypedXmlWriter;

import java.util.HashSet;
import java.util.Set;


/**
 * A named pattern.
 *
 * @author Kohsuke Kawaguchi
 */
public class Define extends WriterNode {
    public final Grammar scope;
    public final String name;

    JDefinedClass clazz;

    public Define(Grammar scope, String name) {
        super(null,null);
        if(scope==null)     scope = (Grammar)this;  // hack for start pattern
        this.scope = scope;
        this.name = name;
        assert name!=null;
    }

    /**
     * Returns true if this define only contains
     * one child (and thus considered inlinable.)
     *
     * A pattern definition is also inlineable if
     * it's the start of the grammar (because "start" isn't a meaningful name)
     */
    public boolean isInline() {
        return hasOneChild() || name==Grammar.START;
    }

    void declare(NodeSet nset) {
        if(isInline())  return;

        clazz = nset.createClass(name);
        clazz._implements(TypedXmlWriter.class);
    }

    void generate(NodeSet nset) {
        if(clazz==null)     return;

        HashSet<Prop> props = new HashSet<Prop>();
        for( Leaf l : this )
            l.generate(clazz,nset,props);
    }

    void generate(JDefinedClass clazz, NodeSet nset, Set<Prop> props) {
        if(isInline()) {
            for( Leaf l : this )
                l.generate(clazz,nset, props);
        } else {
            assert this.clazz!=null;
            clazz._implements(this.clazz);
        }
    }

    void prepare(NodeSet nset) {
        if(isInline() && leaf instanceof WriterNode && !name.equals(Grammar.START))
            ((WriterNode)leaf).alternativeName = name;
    }

    public String toString() {
        return "Define "+name;
    }
}
