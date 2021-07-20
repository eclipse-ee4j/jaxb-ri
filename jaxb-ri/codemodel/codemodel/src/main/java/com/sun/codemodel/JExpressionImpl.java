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
 * Provides default implementations for {@link JExpression}.
 */
public abstract class JExpressionImpl implements JExpression {

    protected JExpressionImpl() {
    }

    //
    //
    // from JOp
    //
    //
    @Override
    public final JExpression minus() {
        return JOp.minus(this);
    }

    /**
     * Logical not {@code '!x'}.
     */
    @Override
    public final JExpression not() {
        return JOp.not(this);
    }

    @Override
    public final JExpression complement() {
        return JOp.complement(this);
    }

    @Override
    public final JExpression incr() {
        return JOp.incr(this);
    }

    @Override
    public final JExpression decr() {
        return JOp.decr(this);
    }

    @Override
    public final JExpression plus(JExpression right) {
        return JOp.plus(this, right);
    }

    @Override
    public final JExpression minus(JExpression right) {
        return JOp.minus(this, right);
    }

    @Override
    public final JExpression mul(JExpression right) {
        return JOp.mul(this, right);
    }

    @Override
    public final JExpression div(JExpression right) {
        return JOp.div(this, right);
    }

    @Override
    public final JExpression mod(JExpression right) {
        return JOp.mod(this, right);
    }

    @Override
    public final JExpression shl(JExpression right) {
        return JOp.shl(this, right);
    }

    @Override
    public final JExpression shr(JExpression right) {
        return JOp.shr(this, right);
    }

    @Override
    public final JExpression shrz(JExpression right) {
        return JOp.shrz(this, right);
    }

    @Override
    public final JExpression band(JExpression right) {
        return JOp.band(this, right);
    }

    @Override
    public final JExpression bor(JExpression right) {
        return JOp.bor(this, right);
    }

    @Override
    public final JExpression cand(JExpression right) {
        return JOp.cand(this, right);
    }

    @Override
    public final JExpression cor(JExpression right) {
        return JOp.cor(this, right);
    }

    @Override
    public final JExpression xor(JExpression right) {
        return JOp.xor(this, right);
    }

    @Override
    public final JExpression lt(JExpression right) {
        return JOp.lt(this, right);
    }

    @Override
    public final JExpression lte(JExpression right) {
        return JOp.lte(this, right);
    }

    @Override
    public final JExpression gt(JExpression right) {
        return JOp.gt(this, right);
    }

    @Override
    public final JExpression gte(JExpression right) {
        return JOp.gte(this, right);
    }

    @Override
    public final JExpression eq(JExpression right) {
        return JOp.eq(this, right);
    }

    @Override
    public final JExpression ne(JExpression right) {
        return JOp.ne(this, right);
    }

    @Override
    public final JExpression _instanceof(JType right) {
        return JOp._instanceof(this, right);
    }

    //
    //
    // from JExpr
    //
    //
    @Override
    public final JInvocation invoke(JMethod method) {
        return JExpr.invoke(this, method);
    }

    @Override
    public final JInvocation invoke(String method) {
        return JExpr.invoke(this, method);
    }

    @Override
    public final JFieldRef ref(JVar field) {
        return JExpr.ref(this, field);
    }

    @Override
    public final JFieldRef ref(String field) {
        return JExpr.ref(this, field);
    }

    @Override
    public final JArrayCompRef component(JExpression index) {
        return JExpr.component(this, index);
    }
}
