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
 * A return statement
 */
class JReturn implements JStatement {

    /**
     * JExpression to return; may be null.
     */
    private JExpression expr;

    /**
     * JReturn constructor
     *
     * @param expr
     *        JExpression which evaluates to return value
     */
    JReturn(JExpression expr) {
       this.expr = expr;
    }

    public void state(JFormatter f) {
        f.p("return ");
        if (expr != null) f.g(expr);
        f.p(';').nl();
    }

}
