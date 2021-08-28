/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.util;

import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.XMLFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

/**
 * {@link XMLFilter} that can cut sub-trees.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class SubtreeCutter extends XMLFilterImpl {
    /**
     * When we are pruning a sub tree, this field holds the depth of
     * elements that are being cut. Used to resume event forwarding.
     *
     * As long as this value is 0, we will pass through data.
     */
    private int cutDepth=0;


    /**
     * This object will receive SAX events while a sub tree is being
     * pruned.
     */
    private static final ContentHandler stub = new DefaultHandler();

    /**
     * This field remembers the user-specified ContentHandler.
     * So that we can restore it once the sub tree is completely pruned.
     */
    private ContentHandler next;


    @Override
    public void startDocument() throws SAXException {
        cutDepth=0;
        super.startDocument();
    }

    public boolean isCutting() {
        return cutDepth>0;
    }

    /**
     * Starts cutting a sub-tree. Should be called from within the
     * {@link #startElement(String, String, String, Attributes)} implementation
     * before the execution is passed to {@link SubtreeCutter#startElement(String, String, String, Attributes)} .
     * The current element will be cut.
     */
    public void startCutting() {
        super.setContentHandler(stub);
        cutDepth=1;
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        next = handler;
        // changes take effect immediately unless the sub-tree is being pruned
        if(getContentHandler()!=stub)
            super.setContentHandler(handler);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if(cutDepth>0)
            cutDepth++;
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        super.endElement(namespaceURI, localName, qName);

        if( cutDepth!=0 ) {
            cutDepth--;
            if( cutDepth == 1 ) {
                // pruning completed. restore the user handler
                super.setContentHandler(next);
                cutDepth=0;
            }
        }
    }
}
