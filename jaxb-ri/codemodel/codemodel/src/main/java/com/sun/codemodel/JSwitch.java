/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Switch statement
 */
public final class JSwitch implements JStatement {

    /**
     * Test part of switch statement.
     */
    private JExpression test;

    /**
     * vector of JCases.
     */
    private List<JCase> cases = new ArrayList<>();
    
    /**
     * a single default case
     */
    private JCase defaultCase = null;

    /**
     * Construct a While statment
     */
    JSwitch(JExpression test) {
        this.test = test;
    }

    public JExpression test() { return test; }

    public Iterator<JCase> cases() { return cases.iterator(); }

    public JCase _case( JExpression label ) {
        JCase c = new JCase( label );
        cases.add(c);
        return c;
    }

    public JCase _default() {
        // what if (default != null) ???
        
        // default cases statements don't have a label
        defaultCase = new JCase(null, true);
        return defaultCase;
    }
    
    @Override
    public void state(JFormatter f) {
        if (JOp.hasTopOp(test)) {
            f.p("switch ").g(test).p(" {").nl();
        } else {
            f.p("switch (").g(test).p(')').p(" {").nl();
        }
        for( JCase c : cases )
            f.s(c);
        if( defaultCase != null )
            f.s( defaultCase );
        f.p('}').nl();
    }

}
