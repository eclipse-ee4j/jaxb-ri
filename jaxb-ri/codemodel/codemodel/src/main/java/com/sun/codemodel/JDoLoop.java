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
 * Do loops
 */

public class JDoLoop implements JStatement {

    /**
     * Test part of Do statement for determining exit state
     */
    private JExpression test;

    /**
     * JBlock of statements which makes up body of this Do statement
     */
    private JBlock body = null;

    /**
     * Construct a Do statment
     */
    JDoLoop(JExpression test) {
        this.test = test;
    }

    public JBlock body() {
        if (body == null) body = new JBlock();
        return body;
    }

    public void state(JFormatter f) {
        f.p("do");
        if (body != null)
            f.g(body);
        else
            f.p("{ }");

        if (JOp.hasTopOp(test)) {
            f.p("while ").g(test);
        } else {
            f.p("while (").g(test).p(')');
        }
        f.p(';').nl();
    }

}
