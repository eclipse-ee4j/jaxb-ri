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

/**
 * If statement, with optional else clause
 */

public class JConditional implements JStatement {

    /**
     * JExpression to test to determine branching
     */
    private JExpression test = null;

    /**
     * JBlock of statements for "then" clause
     */
    private JBlock _then = new JBlock();

    /**
     * JBlock of statements for optional "else" clause
     */
    private JBlock _else = null;

    /**
     * Constructor
     *
     * @param test
     *        JExpression which will determine branching
     */
    JConditional(JExpression test) {
       this.test = test;
    }

    /**
     * Return the block to be excuted by the "then" branch
     *
     * @return Then block
     */
    public JBlock _then() {
        return _then;
    }

    /**
     * Create a block to be executed by "else" branch
     *
     * @return Newly generated else block
     */
    public JBlock _else() {
        if (_else == null) _else = new JBlock();
        return _else;
    }

    /**
     * Creates {@code ... else if(...) ...} code.
     */
    public JConditional _elseif(JExpression boolExp) {
        return _else()._if(boolExp);
    }

    @Override
    public void state(JFormatter f) {
        if(test==JExpr.TRUE) {
            _then.generateBody(f);
            return;
        }
        if(test==JExpr.FALSE) {
            _else.generateBody(f);
            return;
        }

        if (JOp.hasTopOp(test)) {
            f.p("if ").g(test);
        } else {
            f.p("if (").g(test).p(')');
        }
        f.g(_then);
        if (_else != null)
            f.p("else").g(_else);
        f.nl();
    }
}
