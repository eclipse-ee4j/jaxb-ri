/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd;

import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
abstract class Term {
    abstract void normalize( List<Block> r, boolean optional );

    abstract void addAllElements(Block b);

    abstract boolean isOptional();

    abstract boolean isRepeated();

    /**
     * Represents empty term.
     * <p>
     * This special term is only used to represent #PCDATA-only content model.
     */
    static final Term EMPTY = new Term() {
        void normalize(List<Block> r, boolean optional) {
        }

        void addAllElements(Block b) {
        }

        boolean isOptional() {
            return false;
        }

        boolean isRepeated() {
            return false;
        }
    };
}
