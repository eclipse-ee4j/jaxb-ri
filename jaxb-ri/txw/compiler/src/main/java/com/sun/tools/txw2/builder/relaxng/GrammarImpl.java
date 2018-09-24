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

import com.sun.tools.txw2.model.Leaf;
import com.sun.tools.txw2.model.Ref;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.Grammar;
import com.sun.tools.rngom.ast.builder.Scope;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.util.LocatorImpl;

/**
 * @author Kohsuke Kawaguchi
 */
class GrammarImpl extends GrammarSectionImpl
    implements Grammar<Leaf,ParsedElementAnnotation,LocatorImpl,AnnotationsImpl,CommentListImpl> {

    GrammarImpl(Scope<Leaf,ParsedElementAnnotation,LocatorImpl,AnnotationsImpl,CommentListImpl> scope) {
        super(scope,new com.sun.tools.txw2.model.Grammar());
    }

    public Leaf endGrammar(LocatorImpl locator, AnnotationsImpl annotations) throws BuildException {
        return new Ref(locator,grammar,com.sun.tools.txw2.model.Grammar.START);
    }

    public Leaf makeParentRef(String name, LocatorImpl locator, AnnotationsImpl annotations) throws BuildException {
        return parent.makeRef(name,locator,annotations);
    }

    public Leaf makeRef(String name, LocatorImpl locator, AnnotationsImpl annotations) throws BuildException {
        return new Ref(locator,grammar,name);
    }
}
