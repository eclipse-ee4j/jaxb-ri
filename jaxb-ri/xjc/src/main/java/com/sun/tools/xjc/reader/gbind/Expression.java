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

import java.util.Set;

/**
 * This builds content models.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Expression {

    /**
     * Computes {@code LAST(exp)}
     */
    abstract ElementSet lastSet();

    /**
     * True of {@code \epsilon \in L(exp)}
     */
    abstract boolean isNullable();

    /**
     * Builds up a DAG among {@link Element}s in this expression.
     */
    abstract void buildDAG(ElementSet incoming);

    /**
     * {@link Expression} that represents epsilon, the length-0 string.
     */
    public static final Expression EPSILON = new Expression() {
        ElementSet lastSet() {
            return ElementSet.EMPTY_SET;
        }

        boolean isNullable() {
            return true;
        }

        void buildDAG(ElementSet incoming) {
            // noop
        }

        public String toString() {
            return "-";
        }
    };
}
