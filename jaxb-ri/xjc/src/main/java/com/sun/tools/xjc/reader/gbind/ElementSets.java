/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.gbind;

import java.util.LinkedHashSet;

/**
 * Factory methods for {@link ElementSet}.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ElementSets {

    private ElementSets() {}

    /**
     * Returns an union of two {@link ElementSet}s.
     *
     * This method performs better if lhs is bigger than rhs
     */
    public static ElementSet union(ElementSet lhs, ElementSet rhs) {
        if(lhs.contains(rhs))
            return lhs;
        if(lhs==ElementSet.EMPTY_SET)
            return rhs;
        if(rhs==ElementSet.EMPTY_SET)
            return lhs;
        return new MultiValueSet(lhs,rhs);
    }

    /**
     * {@link ElementSet} that has multiple {@link Element}s in it.
     *
     * This isn't particularly efficient or anything, but it will do for now.
     */
    private static final class MultiValueSet extends LinkedHashSet<Element> implements ElementSet {

        private static final long serialVersionUID = -2635296810004496648L;

        public MultiValueSet(ElementSet lhs, ElementSet rhs) {
            addAll(lhs);
            addAll(rhs);
            // not that anything will break with size==1 MultiValueSet,
            // but it does suggest that we are missing an easy optimization
            assert size()>1;
        }

        private void addAll(ElementSet lhs) {
            if(lhs instanceof MultiValueSet) {
                super.addAll((MultiValueSet)lhs);
            } else {
                for (Element e : lhs)
                    add(e);
            }
        }

        @Override
        public boolean contains(ElementSet rhs) {
            // this isn't complete but sound
            return super.contains(rhs) || rhs==ElementSet.EMPTY_SET;
        }

        @Override
        public void addNext(Element element) {
            for (Element e : this)
                e.addNext(element);
        }
    }
}
