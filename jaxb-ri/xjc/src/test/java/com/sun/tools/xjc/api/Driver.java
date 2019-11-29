/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import com.sun.tools.xjc.ConsoleErrorReporter;
import com.sun.xml.bind.v2.WellKnownNamespace;

import com.sun.xml.bind.v2.util.XmlFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Tests the XJC API.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Driver {

    private static class ErrorReceiverImpl extends ConsoleErrorReporter implements ErrorListener {}

    /** Use StAX? Otherwise use SAX. */
    private boolean stax;
    /** Generate code and dump it to screen? */
    private boolean code;

    private File[] files;

    private Driver( String[] args ) {
        List<String> files = new ArrayList<>();
        for (String arg : args) {
            if ("-stax".equals(arg)) {
                stax = true;
                continue;
            }
            if ("-code".equals(arg)) {
                code = true;
                continue;
            }
            files.add(arg);
        }

        this.files = new File[files.size()];
        int i=0;
        for (String f : files) {
            this.files[i++] = new File(f);
        }
    }

    public static void main(String[] args) throws Exception {
        new Driver(args).run();
    }

    private void run() throws Exception {
        SchemaCompiler compiler = XJC.createSchemaCompiler();

        ErrorReceiverImpl er = new ErrorReceiverImpl();
        compiler.setErrorListener(er);

        XMLInputFactory xif = XMLInputFactory.newInstance();

        for (File value : files) {
            String url = value.toURI().toURL().toString();

            if (value.getName().toLowerCase().endsWith(".wsdl")) {
                // read it as WSDL.
                DocumentBuilderFactory dbf = XmlFactory.createDocumentBuilderFactory(false);
                Document dom = dbf.newDocumentBuilder().parse(value);
                compiler.parseSchema(url,
                        findSchemas(dom.getDocumentElement()));
            } else if (stax) {
                XMLStreamReader r = xif.createXMLStreamReader(new FileInputStream(value));

                // workaround for bug 5030916 in Zephyr
                if (r.getEventType() != XMLStreamConstants.START_DOCUMENT)
                    r.next();

                System.err.println("parsing " + value);
                compiler.parseSchema(url, r);
            } else {
                compiler.parseSchema(new InputSource(url));
            }
        }

        S2JJAXBModel model = compiler.bind();
        if(model==null) {
            System.out.println("failed to compile.");
            return;
        }

        dumpModel(model);

        // build the code, just to see if there's any error
        JCodeModel cm = model.generateCode(null, er);

        if(code) {
            cm.build(new SingleStreamCodeWriter(System.out));
        }
    }

    private Element findSchemas(Element e) {
        NodeList children = e.getChildNodes();
        for( int i=0; i<children.getLength(); i++ ) {
            Node n = children.item(i);
            if( n.getNodeType()==Node.ELEMENT_NODE ) {
                Element x = (Element)n;
                if(x.getLocalName().equals("schema")
                && x.getNamespaceURI().equals(WellKnownNamespace.XML_SCHEMA))
                    return x;

                x = findSchemas(x);
                if(x!=null) return x;
            }
        }
        return null;
    }

    /**
     * Dumps a {@link JAXBModel}.
     */
    public static void dumpModel(S2JJAXBModel model) {
        System.out.println("--- class list ---");
        for( String s : model.getClassList() )
            System.out.println("  "+s);
        System.out.println();

        for( Mapping m  : model.getMappings() ) {
            System.out.println(m.getElement()+"<->"+m.getType());

            List<? extends Property> detail = m.getWrapperStyleDrilldown();
            if(detail==null) {
                System.out.println("(not a wrapper-style element)");
            } else {
                for(Property p : detail ) {
                    System.out.println("  "+p.name()+'\t'+p.type()+'\t'+p.elementName());
                }
            }

            System.out.println();
        }
    }
}
