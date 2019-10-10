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
 * A cast operation.
 */
public final class JCast extends JExpressionImpl {
    /**
     * JType to which the expression is to be cast.
     */
    private final JType type;

    /**
     * JExpression to be cast.
     */
    private final JExpression object;

    /**
     * JCast constructor 
     *
     * @param type
     *        JType to which the expression is cast
     *
     * @param object
     *        JExpression for the object upon which
     *        the cast is applied
     */
    JCast(JType type, JExpression object) {
        this.type = type;
        this.object = object;
    }

    public void generate(JFormatter f) {
        f.p("((").g(type).p(')').g(object).p(')');
    }
}
