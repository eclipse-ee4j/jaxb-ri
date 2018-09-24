/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.util.AbstractList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * {@link List} that filters another {@link List} instance by a type.
 * 
 * <p>
 * This filtered list provides a live view of another list. It is
 * useful when you want to look for objects of a particular class
 * inside a heterogeneous list.
 * 
 * <p>
 * The filtered list is "live" in the sense that any modification done
 * to this list will be immediately propagated to the underlying list,
 * and any modification done on the underlying list will be immediately
 * visible through this list.
 * In fact, this filter doesn't actually store objects by itself,
 * but rather delegate all the work to the underlying list.
 * 
 * <p>
 * Note that the performance characteristic of this {@link List} is
 * closer to that of {@link java.util.LinkedList}. It's not suited
 * for random access by index (but iteration works nicely.)
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class FilterList extends AbstractList {
    
    /**
     * Instances of this class will be visible through this list.
     */
    private final Class type;
    
    /**
     * The underlying {@link List} that this object works on.
     */
    private final List core;
    
    /**
     * We use an iterator from the core to detect modification to it.
     * The assumption here is that the iterator is fail-fast,
     * and it tells us so when the underlying list is modified
     * without our knowledge.
     * 
     * <p>
     * Note that the List interface doesn't mandate the fail-fast
     * behavior, so there's a risk of this doesn't work correctly.
     * But I think the risk is minimal in practice, given that
     * all the JDK classes and {@link AbstractList} implements
     * the semantics - KK.
     */
    private ListIterator itr;
    
    /**
     * Cached value of the {@link #size()}.
     * 
     * The above method could be called very frequently, and
     * it involves the whole re-scanning of the list, so we
     * cache the result and use it.
     */
    private int size = -1;
    
    /**
     * Creates a new filtered list that filters objects in
     * the spcified {@link List}.
     * 
     * @param core
     *      {@link List} to be filtered.
     * @param type
     *      Out of all the objects in the <tt>core</tt> list,
     *      only objects of this type will be visible through
     *      this list.
     */
    public FilterList( List core, Class type ) {
        this.core = core;
        this.type = type;
        itr = core.listIterator();
    }
    
    /**
     * Returns true if the core is modified.
     */
    private boolean isCoreModified() {
        try {
            itr.next();
            itr.previous();
            return false;
        } catch( NoSuchElementException e ) {
            ; // this is cool. 
            return false;
        } catch( ConcurrentModificationException e ) {
            itr = core.listIterator();
            return true;
        }
    }
    
    /**
     * Converts the index of this collection to that of the underlying list.
     * 
     * @param allowEnd
     *      If true, the index maps idx==this.size() to core.size().
     *      Otherwise it results in {@link IndexOutOfBoundsException}.
     */
    private int toCoreIndex(int idx,boolean allowEnd) {
        int i=0;
        final int index = idx;  // keep the original value
        
        for (Iterator itr = core.iterator(); itr.hasNext();i++) {
            if(type.isInstance(itr.next())) {
                if(idx==0)  return i;
                idx--;
            }
        }
        
        if(allowEnd && idx==0)
            return i;
        else
            throw new IndexOutOfBoundsException(Integer.toString(index));
    }
    
    public void add(int index, Object element) {
        core.add(toCoreIndex(index,true),element);
    }
    
    public Iterator iterator() {
        return listIterator();
    }
    
    public ListIterator listIterator(final int index) {
        return new ListIterator() {
            /**
             * Index of the current item in this list (not the core list).
             * This will be the one returned from the next {@link #next()}.
             * 
             * When the iterator hits the end of the list, this equals to
             * the size of this filtered list.
             * 
             * Note that just because the iterator is positioned at the
             * beginning of the list doesn't mean its thisIndex==0.
             */
            private int thisIndex = index;
            
            /**
             * Index of the current item in the core list (not this list).
             * 
             * When the iterator hits the end of the list, this equals to
             * the size of the core list.
             */
            private int coreIndex = toCoreIndex(index,true);

            /**
             * Index of element returned by most recent call to next or
             * previous.  Reset to -1 if this element is deleted by a call
             * to remove.
             */
            private int lastRet = -1;
            
            public int nextIndex() {
                return thisIndex;
            }
            public int previousIndex() {
                return thisIndex-1;
            }
            public void remove() {
                if (lastRet == -1)
                    throw new IllegalStateException();

                try {
                    core.remove(lastRet);
                    if (lastRet<coreIndex) {
                        coreIndex--;
                        thisIndex--;
                    }
                    lastRet = -1;
                } catch (IndexOutOfBoundsException e) {
                    throw new ConcurrentModificationException();
                }
            }
            public boolean hasNext() {
                return coreIndex<core.size();
            }
            public Object next() {
                int coreSize = core.size();
                
                if(coreIndex>=coreSize)
                    throw new NoSuchElementException();
                lastRet = coreIndex;
                
                // move forward
                thisIndex++;
                do {
                    coreIndex++;
                } while( !match(core.get(coreIndex)) && coreIndex<coreSize );
                    
                return core.get(lastRet);
            }
            public boolean hasPrevious() {
                // TODO: this could be made bit more efficient
                
                // look for the previous item
                int idx = coreIndex-1;
                while(idx>=0 && !match(core.get(idx)))
                    idx--;
                
                return idx>=0;
            }
            public Object previous() {
                // TODO: this could be made bit more efficient
                
                // look for the previous item
                int idx = coreIndex-1;
                while(idx>=0 && !match(core.get(idx)))
                    idx--;
                
                if(idx<0)
                    throw new NoSuchElementException();
                lastRet=idx;
                
                thisIndex--;
                coreIndex=idx;
                
                return core.get(lastRet);
            }
            public void add(Object o) {
                try {
                    core.add(coreIndex,o);
                    lastRet = -1;
                } catch (IndexOutOfBoundsException e) {
                    throw new ConcurrentModificationException();
                }
            }
            public void set(Object o) {
                if (lastRet == -1)
                    throw new IllegalStateException();

                try {
                    core.set(lastRet,o);
                } catch (IndexOutOfBoundsException e) {
                    throw new ConcurrentModificationException();
                }
            }
        };
    }
    
    public Object set(int index, Object element) {
        return core.set(toCoreIndex(index,false),element);
    }

    /**
     * Remove the object at the specified offset.
     *
     * @param index The offset of the object.
     * @return The Object which was removed.
     */
    public Object remove(int index) {
        return core.remove(toCoreIndex(index,false));
    }
    
    public Object get(int index) {
        return core.get(toCoreIndex(index,false));
    }

    public int size() {
        if(isCoreModified() || size==-1) {
            size=0;
            for (Iterator itr = core.iterator(); itr.hasNext();) {
                if(match(itr.next()))
                    size++;
            }
        }
        
        return size;
    }
    
    /**
     * Returns true if the object matches the filtering criteria.
     */
    private boolean match(Object o) {
        return type.isInstance(o);
    }
}
