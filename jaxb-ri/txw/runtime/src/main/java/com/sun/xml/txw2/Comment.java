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
 * Comment.
 *
 * @author Kohsuke Kawaguchi
 */
final class Comment extends Content {
    /**
     * The text to be writtten.
     */
    private final StringBuilder buffer = new StringBuilder();

    public Comment(Document document, NamespaceResolver nsResolver, Object obj) {
        document.writeValue(obj,nsResolver,buffer);
    }

    boolean concludesPendingStartTag() {
        return false;
    }

    void accept(ContentVisitor visitor) {
        visitor.onComment(buffer);
    }
}
