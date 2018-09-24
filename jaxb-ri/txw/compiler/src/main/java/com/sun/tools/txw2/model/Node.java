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

import org.xml.sax.Locator;

import java.util.Iterator;

/**
 * {@link Node} is a {@link Leaf} that has children.
 * getting and setting the parent of a node, and for removing a node.
 *
 * @since 1.6, SAAJ 1.2
 * Children are orderless.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Node extends Leaf implements Iterable<Leaf> {

    /**
     * Children of this node.
     */
    public Leaf leaf;

    protected Node(Locator location, Leaf leaf) {
        super(location);
        this.leaf = leaf;
    }

    /**
     * Iterates all the children.
     */
    public final Iterator<Leaf> iterator() {
        return new CycleIterator(leaf);
    }

    /**
     * Returns true if this node has only one child node.
     */
    public final boolean hasOneChild() {
        return leaf==leaf.getNext();
    }

    /**
     * Adds the given {@link Leaf} and their sibling as children of this {@link Node}.
     */
    public final void addChild(Leaf child) {
        if(this.leaf==null)
            leaf = child;
        else
            leaf.merge(child);
    }

}
