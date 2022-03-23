/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.output;

import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * Abstract implementation of {@link XmlOutput}
 *
 * Implements the optimal methods, where defer to 
 * the non-optimal methods.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class XmlOutputAbstractImpl implements XmlOutput {

    /**
     * Default constructor.
     */
    protected XmlOutputAbstractImpl() {}

//
//
// Contracts
//
//
    /**
     * Called at the very beginning.
     *
     * @param serializer
     *      the {@link XMLSerializer} that coordinates this whole marshalling episode.
     * @param fragment
     *      true if we are marshalling a fragment.
     */
    @Override
    public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        this.nsUriIndex2prefixIndex = nsUriIndex2prefixIndex;
        this.nsContext = nsContext;
        this.serializer = serializer;
    }

    /**
     * Called at the very end.
     *
     * @param fragment
     *      false if we are writing the whole document.
     */
    @Override
    public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
        serializer = null;
    }

    /**
     * Writes a start tag.
     *
     * <p>
     * At this point {@link #nsContext} holds namespace declarations needed for this
     * new element.
     *
     * <p>
     * This method is used for writing tags that are indexed.
     */
    @Override
    public void beginStartTag(Name name) throws IOException, XMLStreamException {
        beginStartTag( nsUriIndex2prefixIndex[name.nsUriIndex], name.localName );
    }

    @Override
    public abstract void beginStartTag(int prefix, String localName) throws IOException, XMLStreamException;

    @Override
    public void attribute( Name name, String value ) throws IOException, XMLStreamException {
        short idx = name.nsUriIndex;
        if(idx==-1)
            attribute(-1,name.localName, value);
        else
            attribute( nsUriIndex2prefixIndex[idx], name.localName, value );
    }
    /**
     * @param prefix
     *      -1 if this attribute does not have a prefix
     *      (this handling differs from that of elements.)
     */
    @Override
    public abstract void attribute( int prefix, String localName, String value ) throws IOException, XMLStreamException;

    @Override
    public abstract void endStartTag() throws IOException, SAXException;

    @Override
    public void endTag(Name name) throws IOException, SAXException, XMLStreamException {
        endTag( nsUriIndex2prefixIndex[name.nsUriIndex], name.localName);
    }
    @Override
    public abstract void endTag(int prefix, String localName) throws IOException, SAXException, XMLStreamException;




//
//
// Utilities for implementations
//
//
    /**
     * The conversion table from the namespace URI index to prefix index.
     *
     * This array is shared with {@link XMLSerializer} and
     * is updated by it automatically.
     *
     * This allows {@link Name#nsUriIndex} to be converted to prefix index
     * (for {@link NamespaceContextImpl}) quickly.
     */
    protected int[] nsUriIndex2prefixIndex;

    /**
     * Set by the marshaller before the start tag is written for the root element.
     */
    protected NamespaceContextImpl nsContext;

    protected XMLSerializer serializer;
}
