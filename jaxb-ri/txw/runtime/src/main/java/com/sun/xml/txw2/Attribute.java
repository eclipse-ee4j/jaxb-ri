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
 * @author Kohsuke Kawaguchi
 */
final class Attribute {
    final String nsUri;
    final String localName;

    /**
     * Attributes of an element form a linked list.
     */
    Attribute next;

    /**
     * Attribute value that potentially contains dummy prefixes.
     */
    final StringBuilder value = new StringBuilder();

    Attribute(String nsUri, String localName) {
        assert nsUri!=null && localName!=null;

        this.nsUri = nsUri;
        this.localName = localName;
    }

    boolean hasName( String nsUri, String localName ) {
        return this.localName.equals(localName) && this.nsUri.equals(nsUri);
    }
}
