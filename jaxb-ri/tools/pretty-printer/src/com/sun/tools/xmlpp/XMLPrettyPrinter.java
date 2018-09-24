/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xmlpp;

import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class XMLPrettyPrinter implements ContentHandler, LexicalHandler {
    
    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    
    public XMLPrettyPrinter( Writer out ) {
        DataWriter writer = new DataWriter(out);
        writer.setIndentStep(2);
        
        this.lexicalHandler = writer;
        
        Spacer spacer = new Spacer();
        spacer.setContentHandler(writer);
        
        IgnorableWhitespaceFilter iwf = new IgnorableWhitespaceFilter();
        iwf.setContentHandler(spacer);

        this.contentHandler = new XalanBugWorkaroundFilter(iwf);
    }
    
    public static void main(String[] args) throws Exception {
        SAXParserFactory parser = SAXParserFactory.newInstance();
        parser.setNamespaceAware(true);
        parser.setValidating(false);
        
        XMLReader reader = parser.newSAXParser().getXMLReader();
        
        XMLPrettyPrinter pp = new XMLPrettyPrinter(new OutputStreamWriter(System.out));
        reader.setProperty("http://xml.org/sax/properties/lexical-handler",pp);
        reader.setContentHandler(pp);
        reader.parse(new InputSource(System.in));
        
    }
    
    
//
//
// delegation methods
//
//
    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws org.xml.sax.SAXException
     */
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        contentHandler.characters(arg0, arg1, arg2);
    }

    /**
     * @throws org.xml.sax.SAXException
     */
    public void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws org.xml.sax.SAXException
     */
    public void endElement(String arg0, String arg1, String arg2) throws SAXException {
        contentHandler.endElement(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @throws org.xml.sax.SAXException
     */
    public void endPrefixMapping(String arg0) throws SAXException {
        contentHandler.endPrefixMapping(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws org.xml.sax.SAXException
     */
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        contentHandler.ignorableWhitespace(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws org.xml.sax.SAXException
     */
    public void processingInstruction(String arg0, String arg1) throws SAXException {
        contentHandler.processingInstruction(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public void setDocumentLocator(Locator arg0) {
        contentHandler.setDocumentLocator(arg0);
    }

    /**
     * @param arg0
     * @throws org.xml.sax.SAXException
     */
    public void skippedEntity(String arg0) throws SAXException {
        contentHandler.skippedEntity(arg0);
    }

    /**
     * @throws org.xml.sax.SAXException
     */
    public void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    public void startElement(String uri, String local, String qname, Attributes arg3) throws SAXException {
        contentHandler.startElement(uri, local, qname, arg3);
    }

    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
        contentHandler.startPrefixMapping(arg0, arg1);
    }

    public void comment(char[] arg0, int arg1, int arg2) throws SAXException {
        lexicalHandler.comment(arg0, arg1, arg2);
    }

    public void endCDATA() throws SAXException {
        lexicalHandler.endCDATA();
    }

    public void endDTD() throws SAXException {
        lexicalHandler.endDTD();
    }

    public void endEntity(String arg0) throws SAXException {
        lexicalHandler.endEntity(arg0);
    }

    public void startCDATA() throws SAXException {
        lexicalHandler.startCDATA();
    }

    public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
        lexicalHandler.startDTD(arg0, arg1, arg2);
    }

    public void startEntity(String arg0) throws SAXException {
        lexicalHandler.startEntity(arg0);
    }

}
