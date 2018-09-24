/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2;

/**
 * Used by {@link DatatypeWriter} to declare additional namespaces.
 *
 * @see DatatypeWriter
 * @author Kohsuke Kawaguchi
 */
public interface NamespaceResolver {
    /**
     * Allocates a prefix for the specified URI and returns it.
     *
     * @param nsUri
     *      the namespace URI to be declared. Can be empty but must not be null.
     * @return
     *      the empty string if the URI is bound to the default namespace.
     *      Otherwise a non-empty string that represents a prefix.
     */
    String getPrefix(String nsUri);
}
