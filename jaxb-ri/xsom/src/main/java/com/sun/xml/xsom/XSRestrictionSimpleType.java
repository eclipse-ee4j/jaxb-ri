/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;

/**
 * Restriction simple type.
 * 
 * @author
 *  Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSRestrictionSimpleType extends XSSimpleType {
    // TODO
    
    /** Iterates facets that are specified in this step of derivation. */
    public Iterator<XSFacet> iterateDeclaredFacets();

    /**
     * Gets all the facets that are declared on this restriction.
     *
     * @return
     *      Can be empty but always non-null.
     */
    public Collection<? extends XSFacet> getDeclaredFacets();

    /**
     * Gets the declared facet object of the given name.
     * 
     * <p>
     * This method returns a facet object that is added in this
     * type and does not recursively check the ancestors.
     * 
     * <p>
     * For those facets that can have multiple values
     * (pattern facets and enumeration facets), this method
     * will return only the first one.
     *
     * @return
     *      Null if the facet is not specified in the last step
     *      of derivation.
     */
    XSFacet getDeclaredFacet( String name );

    /**
     * Gets the declared facets of the given name.
     *
     * This method is for those facets (such as 'pattern') that
     * can be specified multiple times on a simple type.
     *
     * @return
     *      can be empty but never be null.
     */
    List<XSFacet> getDeclaredFacets( String name );
}
