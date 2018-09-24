/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.builder.relaxng;

import com.sun.tools.txw2.model.Define;
import com.sun.tools.txw2.model.Grammar;
import com.sun.tools.txw2.model.Leaf;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.Div;
import com.sun.tools.rngom.ast.builder.GrammarSection;
import com.sun.tools.rngom.ast.builder.Include;
import com.sun.tools.rngom.ast.builder.Scope;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.util.LocatorImpl;

/**
 * @author Kohsuke Kawaguchi
 */
abstract class GrammarSectionImpl implements GrammarSection<Leaf,ParsedElementAnnotation,LocatorImpl,AnnotationsImpl,CommentListImpl> {

    protected final Scope<Leaf,ParsedElementAnnotation,LocatorImpl,AnnotationsImpl,CommentListImpl> parent;

    protected final Grammar grammar;

    GrammarSectionImpl(
        Scope<Leaf,ParsedElementAnnotation,LocatorImpl,AnnotationsImpl,CommentListImpl> scope,
        Grammar grammar ) {
        this.parent = scope;
        this.grammar = grammar;
    }

    public void topLevelAnnotation(ParsedElementAnnotation parsedElementAnnotation) throws BuildException {
    }

    public void topLevelComment(CommentListImpl commentList) throws BuildException {
    }

    public Div<Leaf, ParsedElementAnnotation, LocatorImpl, AnnotationsImpl, CommentListImpl> makeDiv() {
        return new DivImpl(parent,grammar);
    }

    public Include<Leaf, ParsedElementAnnotation, LocatorImpl, AnnotationsImpl, CommentListImpl> makeInclude() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void define(String name, Combine combine, Leaf leaf, LocatorImpl locator, AnnotationsImpl annotations) throws BuildException {
        Define def = grammar.get(name);
        def.location = locator;

        if(combine==null || def.leaf==null) {
            def.leaf = leaf;
        } else {
            def.leaf.merge(leaf);
        }
    }
}
