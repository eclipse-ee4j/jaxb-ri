/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.util;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * {@link Iterator} that walks over a map of maps.
 *
 * @author Kohsuke Kawaguchi
 * @since 2.0
 */
public final class FlattenIterator<T> implements Iterator<T> {

    private final Iterator<? extends Map<?,? extends T>> parent;
    private Iterator<? extends T> child = null;
    private T next;

    public FlattenIterator( Iterable<? extends Map<?,? extends T>> core ) {
        this.parent = core.iterator();
    }


    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        getNext();
        return next!=null;
    }

    public T next() {
        T r = next;
        next = null;
        if(r==null)
            throw new NoSuchElementException();
        return r;
    }

    private void getNext() {
        if(next!=null)  return;

        if(child!=null && child.hasNext()) {
            next = child.next();
            return;
        }
        // child is empty
        if(parent.hasNext()) {
            child = parent.next().values().iterator();
            getNext();
        }
        // else
        //      no more object
    }
}
