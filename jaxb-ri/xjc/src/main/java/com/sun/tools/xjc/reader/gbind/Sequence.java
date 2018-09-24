/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.gbind;

/**
 * {@link Expression} that represents a concatanation of two expressions
 * "A,B".
 *
 * @author Kohsuke Kawaguchi
 */
public final class Sequence extends Expression {
    /**
     * 'A' of 'A,B'
     */
    private final Expression lhs;
    /**
     * 'B' of 'A,B'
     */
    private final Expression rhs;
    /**
     * Compute this value eagerly for better performance
     */
    private final boolean isNullable;

    /**
     * Cached value of {@link #lastSet()} for better performance.
     * Sequence tends to be where the recursive lastSet computation occurs.
     */
    private ElementSet lastSet;

    public Sequence(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        isNullable = lhs.isNullable() && rhs.isNullable();
    }

    ElementSet lastSet() {
        if(lastSet==null) {
            if(rhs.isNullable())
                lastSet = ElementSets.union(lhs.lastSet(),rhs.lastSet());
            else
                lastSet = rhs.lastSet();
        }
        return lastSet;
    }

    boolean isNullable() {
        return isNullable;
    }

    void buildDAG(ElementSet incoming) {
        lhs.buildDAG(incoming);
        if(lhs.isNullable())
            rhs.buildDAG(ElementSets.union(incoming,lhs.lastSet()));
        else
            rhs.buildDAG(lhs.lastSet());
    }

    public String toString() {
        return '('+lhs.toString()+','+rhs.toString()+')';
    }
}
