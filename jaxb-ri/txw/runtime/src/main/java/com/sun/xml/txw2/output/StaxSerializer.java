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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * XML serializer for StAX XMLStreamWriter.
 *
 * TODO: add support for XMLEventWriter (if it makes sense)
 *
 * @author Ryan.Shoemaker@Sun.COM
 */

public class StaxSerializer implements XmlSerializer {
    private final XMLStreamWriter out;

    public StaxSerializer(XMLStreamWriter writer) {
        this(writer,true);
    }

    public StaxSerializer(XMLStreamWriter writer, boolean indenting) {
        if(indenting)
            writer = new IndentingXMLStreamWriter(writer);
        this.out = writer;
    }

    @Override
    public void startDocument() {
        try {
            out.writeStartDocument();
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void beginStartTag(String uri, String localName, String prefix) {
        try {
            out.writeStartElement(prefix, localName, uri);
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
        try {
            out.writeAttribute(prefix, uri, localName, value.toString());
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void writeXmlns(String prefix, String uri) {
        try {
            if (prefix.length() == 0) {
                out.setDefaultNamespace(uri);
            } else {
                out.setPrefix(prefix, uri);
            }

            // this method handles "", null, and "xmlns" prefixes properly
            out.writeNamespace(prefix, uri);
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void endStartTag(String uri, String localName, String prefix) {
        // NO-OP
    }

    @Override
    public void endTag() {
        try {
            out.writeEndElement();
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void text(StringBuilder text) {
        try {
            out.writeCharacters(text.toString());
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void cdata(StringBuilder text) {
        try {
            out.writeCData(text.toString());
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void comment(StringBuilder comment) {
        try {
            out.writeComment(comment.toString());
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void endDocument() {
        try {
            out.writeEndDocument();
            out.flush();
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }
}
