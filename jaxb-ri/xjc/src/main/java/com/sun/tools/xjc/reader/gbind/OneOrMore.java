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
 * {@link Expression} that represents kleene-star operation (A+)
 *
 * @author Kohsuke Kawaguchi
 */
public final class OneOrMore extends Expression {
    /**
     * 'A' of 'A+'.
     */
    private final Expression child;

    public OneOrMore(Expression child) {
        this.child = child;
    }

    ElementSet lastSet() {
        return child.lastSet();
    }

    boolean isNullable() {
        return child.isNullable();
    }

    void buildDAG(ElementSet incoming) {
        child.buildDAG(ElementSets.union(incoming,child.lastSet()));
    }

    public String toString() {
        return child.toString()+'+';
    }
}
