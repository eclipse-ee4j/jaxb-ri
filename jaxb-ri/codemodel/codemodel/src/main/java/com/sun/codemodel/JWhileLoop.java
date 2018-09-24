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


/**
 * While statement
 */

public class JWhileLoop implements JStatement {

    /**
     * Test part of While statement for determining exit state
     */
    private JExpression test;

    /**
     * JBlock of statements which makes up body of this While statement
     */
    private JBlock body = null;

    /**
     * Construct a While statment
     */
    JWhileLoop(JExpression test) {
        this.test = test;
    }

    public JExpression test() {
        return test;
    }

    public JBlock body() {
        if (body == null) body = new JBlock();
        return body;
    }

    public void state(JFormatter f) {
        if (JOp.hasTopOp(test)) {
            f.p("while ").g(test);
        } else {
            f.p("while (").g(test).p(')');
        }
        if (body != null)
            f.s(body);
        else
            f.p(';').nl();
    }

}
