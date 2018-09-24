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
 * Assignment statements, which are also expressions.
 */
public class JAssignment extends JExpressionImpl implements JStatement {

    JAssignmentTarget lhs;
    JExpression rhs;
    String op = "";

    JAssignment(JAssignmentTarget lhs, JExpression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    JAssignment(JAssignmentTarget lhs, JExpression rhs, String op) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    public void generate(JFormatter f) {
        f.g(lhs).p(op + '=').g(rhs);
    }

    public void state(JFormatter f) {
        f.g(this).p(';').nl();
    }

}
