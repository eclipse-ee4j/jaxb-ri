/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

/**
 * Namespace URIs and local names sorted by their indices.
 * Number of Names used for EIIs and AIIs
 *
 * @author Kohsuke Kawaguchi
 */
public final class NameList {
    /**
     * Namespace URIs by their indices. No nulls in this array.
     * Read-only.
     */
    public final String[] namespaceURIs;

    /**
     * For each entry in {@link #namespaceURIs}, whether the namespace URI
     * can be declared as the default. If namespace URI is used in attributes,
     * we always need a prefix, so we can't.
     *
     * True if this URI has to have a prefix.
     */
    public final boolean[] nsUriCannotBeDefaulted;

    /**
     * Local names by their indices. No nulls in this array.
     * Read-only.
     */ 
    public final String[] localNames;

    /**
     * Number of Names for elements
     */
    public final int numberOfElementNames;
    
    /**
     * Number of Names for attributes
     */
    public final int numberOfAttributeNames;
    
    public NameList(String[] namespaceURIs, boolean[] nsUriCannotBeDefaulted, String[] localNames, int numberElementNames, int numberAttributeNames) {
        this.namespaceURIs = namespaceURIs;
        this.nsUriCannotBeDefaulted = nsUriCannotBeDefaulted;
        this.localNames = localNames;
        this.numberOfElementNames = numberElementNames;
        this.numberOfAttributeNames = numberAttributeNames;
    }
}
