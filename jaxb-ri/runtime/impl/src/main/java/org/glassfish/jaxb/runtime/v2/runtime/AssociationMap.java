/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bi-directional map between elements, inner peers,
 * and outer peers.
 * 
 * <p>
 * TODO: this should be rewritten for efficiency.
 * 
 * @since 2.0
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class AssociationMap<XmlNode> {
    public final static class Entry<XmlNode> {
        /** XML element. */
    	private XmlNode element;
        /** inner peer, or null. */
        private Object inner;
        /** outer peer, or null. */
        private Object outer;

        private Entry() {}
        public XmlNode element() {
        	return element;
        }
        public Object inner() {
        	return inner;
        }
        public Object outer() {
        	return outer;
        }
    }
    
    private final Map<XmlNode,Entry<XmlNode>> byElement = new IdentityHashMap<>();
    private final Map<Object,Entry<XmlNode>> byPeer = new IdentityHashMap<>();
    private final Set<XmlNode> usedNodes = new HashSet<>();

    /**
     * Default constructor.
     */
    public AssociationMap() {}

    /** Records the new {@code element <->inner} peer association. */
    public void addInner( XmlNode element, Object inner ) {
        Entry<XmlNode> e = byElement.get(element);
        if(e!=null) {
        	if(e.inner!=null)
                byPeer.remove(e.inner);
            e.inner = inner;
        } else {
        	e = new Entry<>();
            e.element = element;
            e.inner = inner;
        }
        
        byElement.put(element,e);
        
        Entry<XmlNode> old = byPeer.put(inner,e);
        if(old!=null) {
            if(old.outer!=null)
                byPeer.remove(old.outer);
            if(old.element!=null)
                byElement.remove(old.element);
        }
    }
    
    /** Records the new {@code element <-> outer} peer association. */
    public void addOuter( XmlNode element, Object outer ) {
        Entry<XmlNode> e = byElement.get(element);
        if(e!=null) {
            if(e.outer!=null)
                byPeer.remove(e.outer);
        } else {
            e = new Entry<>();
            e.element = element;
        }
        e.outer = outer;

        byElement.put(element,e);
        
        Entry<XmlNode> old = byPeer.put(outer,e);
        if(old!=null) {
            old.outer=null;
            
            if(old.inner==null)
                // remove this entry
                byElement.remove(old.element);
        }
    }

    public void addUsed( XmlNode n ) {
        usedNodes.add(n);
    }

    public Entry<XmlNode> byElement( Object e ) {
        return byElement.get(e);
    }
    
    public Entry<XmlNode> byPeer( Object o ) {
        return byPeer.get(o);
    }
    
    public Object getInnerPeer( XmlNode element ) {
        Entry<XmlNode> e = byElement(element);
        if(e==null)     return null;
        else            return e.inner;
    }
    
    public Object getOuterPeer( XmlNode element ) {
        Entry<XmlNode> e = byElement(element);
        if(e==null)     return null;
        else            return e.outer;
    }
}
