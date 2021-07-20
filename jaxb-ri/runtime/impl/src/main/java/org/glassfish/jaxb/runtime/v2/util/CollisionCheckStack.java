/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.util;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * {@link Stack}-like data structure that allows the following efficient operations:
 *
 * <ol>
 * <li>Push/pop operation.
 * <li>Duplicate check. When an object that's already in the stack is pushed,
 *     this class will tell you so.
 * </ol>
 *
 * <p>
 * Object equality is their identity equality.
 *
 * <p>
 * This class implements {@link List} for accessing items in the stack,
 * but {@link List} methods that alter the stack is not supported.
 *
 * @author Kohsuke Kawaguchi
 */
public final class CollisionCheckStack<E> extends AbstractList<E> {
    private Object[] data;
    private int[] next;
    private int size = 0;

    private boolean latestPushResult = false;

    /**
     * True if the check shall be done by using the object identity.
     * False if the check shall be done with the equals method.
     */
    private boolean useIdentity = true;

    // for our purpose, there isn't much point in resizing this as we don't expect
    // the stack to grow that much.
    private final int[] initialHash;

    public CollisionCheckStack() {
    	initialHash = new int[17];
        data = new Object[16];
        next = new int[16];
    }

    /**
     * Set to false to use {@link Object#equals(Object)} to detect cycles.
     * This method can be only used when the stack is empty.
     */
    public void setUseIdentity(boolean useIdentity) {
        this.useIdentity = useIdentity;
    }

    public boolean getUseIdentity() {
        return useIdentity;
    }

    public boolean getLatestPushResult() {
        return latestPushResult;
    }

    /**
     * Pushes a new object to the stack.
     *
     * @return
     *      true if this object has already been pushed
     */
    public boolean push(E o) {
        if(data.length==size)
            expandCapacity();

        data[size] = o;
        int hash = hash(o);
        boolean r = findDuplicate(o, hash);
        next[size] = initialHash[hash];
        initialHash[hash] = size+1;
        size++;
        this.latestPushResult = r;
        return latestPushResult;
    }

    /**
     * Pushes a new object to the stack without making it participate
     * with the collision check.
     */
    public void pushNocheck(E o) {
        if(data.length==size)
            expandCapacity();
        data[size] = o;
        next[size] = -1;
        size++;
    }

    public boolean findDuplicate(E o) {
        int hash = hash(o);
        return findDuplicate(o, hash);
    }

    @Override
    public E get(int index) {
        return (E)data[index];
    }

    @Override
    public int size() {
        return size;
    }

    private int hash(Object o) {
        return ((useIdentity?System.identityHashCode(o):o.hashCode())&0x7FFFFFFF) % initialHash.length;
    }

    /**
     * Pops an object from the stack
     */
    public E pop() {
        size--;
        Object o = data[size];
        data[size] = null;  // keeping references too long == memory leak
        int n = next[size];
        if(n<0) {
            // pushed by nocheck. no need to update hash
        } else {
            int hash = hash(o);
            assert initialHash[hash]==size+1;
            initialHash[hash] = n;
        }
        return (E)o;
    }

    /**
     * Returns the top of the stack.
     */
    public E peek() {
        return (E)data[size-1];
    }

    private boolean findDuplicate(E o, int hash) {
        int p = initialHash[hash];
        while(p!=0) {
            p--;
            Object existing = data[p];
            if (useIdentity) {
                if(existing==o)     return true;
            } else {
                if (o.equals(existing)) return true;
            }
            p = next[p];
        }
        return false;
    }

    private void expandCapacity() {
        int oldSize = data.length;
        int newSize = oldSize * 2;
        Object[] d = new Object[newSize];
        int[] n = new int[newSize];

        System.arraycopy(data,0,d,0,oldSize);
        System.arraycopy(next,0,n,0,oldSize);

        data = d;
        next = n;
    }

    /**
     * Clears all the contents in the stack.
     */
    public void reset() {
        if(size>0) {
            size = 0;
            Arrays.fill(initialHash,0);
        }
    }

    /**
     * String that represents the cycle.
     */
    public String getCycleString() {
        StringBuilder sb = new StringBuilder();
        int i=size()-1;
        E obj = get(i);
        sb.append(obj);
        Object x;
        do {
            sb.append(" -> ");
            x = get(--i);
            sb.append(x);
        } while(obj!=x);

        return sb.toString();
    }
}
