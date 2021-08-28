/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2.output;

import com.sun.xml.txw2.TxwException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.SAXResult;
import java.util.Stack;

/**
 * {@link XmlSerializer} for {@link SAXResult} and {@link ContentHandler}.
 *
 * @author Ryan.Shoemaker@Sun.COM
 */
public class SaxSerializer implements XmlSerializer {

    private final ContentHandler writer;
    private final LexicalHandler lexical;

    public SaxSerializer(ContentHandler handler) {
        this(handler,null,true);
    }

    /**
     * Creates an {@link XmlSerializer} that writes SAX events.
     *
     * <p>
     * Sepcifying a non-null {@link LexicalHandler} allows applications
     * to write comments and CDATA sections.
     * @param handler writer
     * @param lex writer for comments and CDATA
     */
    public SaxSerializer(ContentHandler handler,LexicalHandler lex) {
        this(handler, lex, true);
    }

    public SaxSerializer(ContentHandler handler,LexicalHandler lex, boolean indenting) {
        if(!indenting) {
            writer = handler;
            lexical = lex;
        } else {
            IndentingXMLFilter indenter = new IndentingXMLFilter(handler, lex);
            writer = indenter;
            lexical = indenter;
        }
    }

    public SaxSerializer(SAXResult result) {
        this(result.getHandler(),result.getLexicalHandler());
    }


    // XmlSerializer implementation

    @Override
    public void startDocument() {
        try {
            writer.startDocument();
        } catch (SAXException e) {
            throw new TxwException(e);
        }
    }

    // namespace prefix bindings
    // add in #writeXmlns and fired in #endStartTag
    private final Stack<String> prefixBindings = new Stack<>();

    @Override
    public void writeXmlns(String prefix, String uri) {
        // defend against parsers that pass null in for "xmlns" prefix
        if (prefix == null) {
            prefix = "";
        }

        if (prefix.equals("xml")) {
            return;
        }

        prefixBindings.add(uri);
        prefixBindings.add(prefix);
    }

    // element stack
    private final Stack<String> elementBindings = new Stack<>();

    @Override
    public void beginStartTag(String uri, String localName, String prefix) {
        // save element bindings for #endTag
        elementBindings.add(getQName(prefix, localName));
        elementBindings.add(localName);
        elementBindings.add(uri);
    }

    // attribute storage
    // attrs are buffered in #writeAttribute and sent to the content
    // handler in #endStartTag
    private final AttributesImpl attrs = new AttributesImpl();

    @Override
    public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
        attrs.addAttribute(uri,
                localName,
                getQName(prefix, localName),
                "CDATA",
                value.toString());
    }

    @Override
    public void endStartTag(String uri, String localName, String prefix) {
        try {
            while (prefixBindings.size() != 0) {
                writer.startPrefixMapping(prefixBindings.pop(), // prefix
                        prefixBindings.pop()   // uri
                );
            }

            writer.startElement(uri,
                    localName,
                    getQName(prefix, localName),
                    attrs);

            attrs.clear();
        } catch (SAXException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void endTag() {
        try {
            writer.endElement(elementBindings.pop(), // uri
                    elementBindings.pop(), // localName
                    elementBindings.pop()  // qname
            );
        } catch (SAXException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void text(StringBuilder text) {
        try {
            writer.characters(text.toString().toCharArray(), 0, text.length());
        } catch (SAXException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void cdata(StringBuilder text) {
        if(lexical==null)
            throw new UnsupportedOperationException("LexicalHandler is needed to write PCDATA");

        try {
            lexical.startCDATA();
            text(text);
            lexical.endCDATA();
        } catch (SAXException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void comment(StringBuilder comment) {
        try {
            if(lexical==null)
                throw new UnsupportedOperationException("LexicalHandler is needed to write comments");
            else
                lexical.comment(comment.toString().toCharArray(), 0, comment.length() );
        } catch (SAXException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void endDocument() {
        try {
            writer.endDocument();
        } catch (SAXException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void flush() {
        // noop
    }

    // other methods
    private static String getQName(String prefix, String localName) {
        final String qName;
        if (prefix == null || prefix.length() == 0)
            qName = localName;
        else
            qName = prefix + ':' + localName;

        return qName;
    }
}
