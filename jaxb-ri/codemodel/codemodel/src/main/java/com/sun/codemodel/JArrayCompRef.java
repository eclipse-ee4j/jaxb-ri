/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

/**
 * array component reference.
 */
public final class JArrayCompRef extends JExpressionImpl implements JAssignmentTarget {
    /**
     * JArray expression upon which this component will be accessed.
     */
    private final JExpression array;

    /**
     * Integer expression representing index of the component
     */
    private final JExpression index;

    /**
     * JArray component reference constructor given an array expression
     * and index.
     *
     * @param array
     *        JExpression for the array upon which
     *        the component will be accessed,
     *
     * @param index
     *        JExpression for index of component to access
     */
    JArrayCompRef(JExpression array, JExpression index) {
        if ((array == null) || (index == null)) {
            throw new NullPointerException();
        }
        this.array = array;
        this.index = index;
    }

    public void generate(JFormatter f) {
        f.g(array).p('[').g(index).p(']');
    }

    public JExpression assign(JExpression rhs) {
		return JExpr.assign(this,rhs);
    }
    public JExpression assignPlus(JExpression rhs) {
		return JExpr.assignPlus(this,rhs);
    }
}
