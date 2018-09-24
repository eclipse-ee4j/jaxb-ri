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

import java.util.Collections;
import java.util.Iterator;

/**
 * A set over a list of {@link Element}.
 *
 * @author Kohsuke Kawaguchi
 */
interface ElementSet extends Iterable<Element> {
    /**
     * For each element in this set, adds an edge to the given element.
     */
    void addNext(Element element);

    public static final ElementSet EMPTY_SET = new ElementSet() {
        public void addNext(Element element) {
            // noop
        }

        public boolean contains(ElementSet element) {
            return this==element;
        }

        public Iterator<Element> iterator() {
            return Collections.<Element>emptySet().iterator();
        }
    };

    /**
     * Doesn't have to be strict (it's OK for this method to return false
     * when it's actually true) since this is used just for optimization.
     *
     * (Erring on the other side is NG)
     */
    boolean contains(ElementSet rhs);
}
