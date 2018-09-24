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
 * {@link Expression} that represents the union of two expressions "A|B".
 *
 * @author Kohsuke Kawaguchi
 */
public final class Choice extends Expression {
    /**
     * "A" of "A|B".
     */
    private final Expression lhs;
    /**
     * "B" of "A|B".
     */
    private final Expression rhs;
    /**
     * Compute this value eagerly for better performance
     */
    private final boolean isNullable;

    public Choice(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.isNullable = lhs.isNullable() || rhs.isNullable();
    }

    boolean isNullable() {
        return isNullable;
    }

    ElementSet lastSet() {
        return ElementSets.union(lhs.lastSet(),rhs.lastSet());
    }

    void buildDAG(ElementSet incoming) {
        lhs.buildDAG(incoming);
        rhs.buildDAG(incoming);
    }

    public String toString() {
        return '('+lhs.toString()+'|'+rhs.toString()+')';
    }
}
