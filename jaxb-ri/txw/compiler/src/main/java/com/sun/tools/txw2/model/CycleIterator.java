/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model;

import java.util.Iterator;

/**
 * @author Kohsuke Kawaguchi
 */
final class CycleIterator implements Iterator<Leaf> {
    private Leaf start;
    private Leaf current;
    private boolean hasNext = true;

    public CycleIterator(Leaf start) {
        assert start!=null;
        this.start = start;
        this.current = start;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public Leaf next() {
        Leaf last = current;
        current = current.getNext();
        if(current==start)
            hasNext = false;

        return last;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
