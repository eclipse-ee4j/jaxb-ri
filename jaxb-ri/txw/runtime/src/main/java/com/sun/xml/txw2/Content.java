/*
 * Copyright (c) 2005, 2022 Oracle and/or its affiliates. All rights reserved.
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
abstract class Content {
    private Content next;

    /**
     * Returns null if the next token has not decided yet.
     */
    final Content getNext() {
        return next;
    }

    final void setNext(Document doc,Content next) {
        assert next!=null;
        assert this.next==null : "next of "+this+" is already set to "+this.next;
        this.next = next;
        doc.run();
    }

    /**
     * Returns true if this content is ready to be committed.
     */
    boolean isReadyToCommit() {
        return true;
    }

    /**
     * Returns true if this  can guarantee that
     * no more new namespace decls is necessary for the currently
     * pending start tag.
     */
    abstract boolean concludesPendingStartTag();

    /**
     * Accepts a visitor.
     */
    abstract void accept(ContentVisitor visitor);

    /**
     * Called when this content is written to the output.
     */
    public void written() {
    }
}
