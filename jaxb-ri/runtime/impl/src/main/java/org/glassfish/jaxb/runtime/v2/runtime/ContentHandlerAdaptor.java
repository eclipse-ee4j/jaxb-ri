/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import com.sun.istack.FinalArrayList;
import com.sun.istack.SAXException2;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * Receives SAX2 events and send the equivalent events to
 * {@link XMLSerializer}
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class ContentHandlerAdaptor extends DefaultHandler {

    /** Stores newly declared prefix-URI mapping. */
    private final FinalArrayList<String> prefixMap = new FinalArrayList<>();

    /** Events will be sent to this object. */
    private final XMLSerializer serializer;
    
    private final StringBuffer text = new StringBuffer();
    
    
    ContentHandlerAdaptor( XMLSerializer _serializer ) {
        this.serializer = _serializer;
    }
    
    @Override
    public void startDocument() {
        prefixMap.clear();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
        prefixMap.add(prefix);
        prefixMap.add(uri);
    }

    private boolean containsPrefixMapping(String prefix, String uri) {
        for( int i=0; i<prefixMap.size(); i+=2 ) {
            if(prefixMap.get(i).equals(prefix)
            && prefixMap.get(i+1).equals(uri))
                return true;
        }
        return false;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
        throws SAXException {
        try {
            flushText();

            int len = atts.getLength();

            String p = getPrefix(qName);

            // is this prefix going to be declared on this element?
            if(containsPrefixMapping(p,namespaceURI))
                serializer.startElementForce(namespaceURI,localName,p,null);
            else
                serializer.startElement(namespaceURI,localName, p,null);

            // declare namespace events
            for (int i = 0; i < prefixMap.size(); i += 2) {
                // forcibly set this binding, instead of using declareNsUri.
                // this guarantees that namespaces used in DOM will show up
                // as-is in the marshalled output (instead of reassigned to something else,
                // which may happen if you'd use declareNsUri.)
                serializer.getNamespaceContext().force(
                        prefixMap.get(i + 1), prefixMap.get(i));
            }

            // make sure namespaces needed by attributes are bound
            for( int i=0; i<len; i++ ) {
                String qname = atts.getQName(i);
                if(qname.startsWith("xmlns") || atts.getURI(i).length() == 0)
                    continue;
                String prefix = getPrefix(qname);

                serializer.getNamespaceContext().declareNamespace(
                    atts.getURI(i), prefix, true );
            }

            serializer.endNamespaceDecls(null);
            // fire attribute events
            for( int i=0; i<len; i++ ) {
                // be defensive.
                if(atts.getQName(i).startsWith("xmlns"))
                    continue;
                serializer.attribute( atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
            }
            prefixMap.clear();
            serializer.endAttributes();
        } catch (IOException | XMLStreamException e) {
            throw new SAXException2(e);
        }
    }

    private String getPrefix(String qname) {
        int idx = qname.indexOf(':');
        return (idx == -1) ? "" : qname.substring(0, idx);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            flushText();
            serializer.endElement();
        } catch (IOException | XMLStreamException e) {
            throw new SAXException2(e);
        }
    }
    
    private void flushText() throws SAXException, IOException, XMLStreamException {
        if( text.length()!=0 ) {
            serializer.text(text.toString(),null);
            text.setLength(0);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        text.append(ch,start,length);
    }
}
