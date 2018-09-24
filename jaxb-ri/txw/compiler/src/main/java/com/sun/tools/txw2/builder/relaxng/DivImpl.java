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

import com.sun.tools.txw2.model.Grammar;
import com.sun.tools.txw2.model.Leaf;
import com.sun.tools.rngom.ast.builder.Div;
import com.sun.tools.rngom.ast.builder.Scope;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.util.LocatorImpl;

/**
 * @author Kohsuke Kawaguchi
 */
class DivImpl
    extends GrammarSectionImpl
    implements Div<Leaf,ParsedElementAnnotation,LocatorImpl,AnnotationsImpl,CommentListImpl> {

    DivImpl(Scope<Leaf,ParsedElementAnnotation,LocatorImpl,AnnotationsImpl,CommentListImpl> parent, Grammar grammar) {
        super(parent,grammar);
    }

    public void endDiv(LocatorImpl locator, AnnotationsImpl annotations) {
    }
}
