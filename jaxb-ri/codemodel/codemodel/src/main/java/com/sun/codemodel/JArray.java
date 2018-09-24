/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.util.ArrayList;
import java.util.List;


/**
 * array creation and initialization.
 */
public final class JArray extends JExpressionImpl {

    private final JType type;
    private final JExpression size;
    private List<JExpression> exprs = null;

    /**
     * Add an element to the array initializer
     */
    public JArray add(JExpression e) {
        if (exprs == null)
            exprs = new ArrayList<JExpression>();
        exprs.add(e);
        return this;
    }

    JArray(JType type, JExpression size) {
        this.type = type;
        this.size = size;
    }

    public void generate(JFormatter f) {
        
        // generally we produce new T[x], but when T is an array type (T=T'[])
        // then new T'[][x] is wrong. It has to be new T'[x][].
        int arrayCount = 0;
        JType t = type;
        
        while( t.isArray() ) {
            t = t.elementType();
            arrayCount++;
        }
        
        f.p("new").g(t).p('[');
        if (size != null)
            f.g(size);
        f.p(']');
        
        for( int i=0; i<arrayCount; i++ )
            f.p("[]");
        
        if ((size == null) || (exprs != null))
            f.p('{');
        if (exprs != null) {
            f.g(exprs);
        } else {
            f.p(' ');
        }
        if ((size == null) || (exprs != null))
            f.p('}');
    }

}
