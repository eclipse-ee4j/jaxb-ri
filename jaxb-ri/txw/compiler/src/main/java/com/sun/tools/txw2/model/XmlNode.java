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

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Either an {@link Element} or {@link Attribute}.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class XmlNode extends WriterNode {
    /**
     * Name of the attribute/element.
     *
     * In TXW, we ignore all infinite names.
     * (finite name class will be expanded to a list of {@link XmlNode}s.
     */
    public final QName name;

    protected XmlNode(Locator location, QName name, Leaf leaf) {
        super(location, leaf);
        this.name = name;
    }

    /**
     * Expand all refs and collect all children.
     */
    protected final Set<Leaf> collectChildren() {
        Set<Leaf> result = new HashSet<Leaf>();

        Stack<Node> work = new Stack<Node>();
        work.push(this);

        while(!work.isEmpty()) {
            for( Leaf l : work.pop() ) {
                if( l instanceof Ref ) {
                    work.push( ((Ref)l).def );
                } else {
                    result.add(l);
                }
            }
        }

        return result;
    }
}
