/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.marshaller;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Manual test of XMLWriter.
 */
public class XMLWriterTest extends TestCase {
    
    public XMLWriterTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(XMLWriterTest.class);
    }

    public void testBasicOps() throws Exception {
        StringWriter sw = new StringWriter();
        XMLWriter w = new XMLWriter(sw, "US-ASCII", DumbEscapeHandler.theInstance );
        w.startDocument();
        w.startElement("","root","root",new AttributesImpl());
        
        w.startPrefixMapping("ns1","aaa");
        w.startPrefixMapping("ns2","bbb");
        w.startPrefixMapping("ns3","ccc");
        
        w.startElement("ccc","child","ns3:child",new AttributesImpl());
        w.endElement("ccc","child","ns3:child");

        w.endPrefixMapping("ns2");
        w.endPrefixMapping("ns1");
        
        w.endElement("","root","root");
        w.endDocument();
        
        checkWellformedness(sw.toString());
    }
    
    // test if the empty tag optimization is happening.
    public void testEmptyTag() throws Exception { 
        StringWriter sw = new StringWriter();
        XMLWriter w = new XMLWriter(sw, "US-ASCII", DumbEscapeHandler.theInstance );
        w.startDocument();
        w.startElement("","root","root",new AttributesImpl());
        w.startElement("","child","child",new AttributesImpl());
        w.endElement("","child","child");
        w.startElement("","kid","kid",new AttributesImpl());
        w.endElement("","kid","kid");
        w.endElement("","root","root");
        w.endDocument();

        String body = sw.toString();
        
        checkWellformedness(body);

        // cut XML header
        body = body.substring(body.indexOf("?>")+2);
        assertEquals( "<root><child/><kid/></root>", body);
        
    }

    /** Checks the well-formedness of XML. */
    private void checkWellformedness(String xml) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.newSAXParser().getXMLReader().parse(
            new InputSource(new StringReader(xml))); 
    }
}
