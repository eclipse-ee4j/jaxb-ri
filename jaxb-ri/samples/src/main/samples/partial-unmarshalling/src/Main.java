/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

/*
 * @(#)$Id: Main.java,v 1.1 2007-12-05 00:49:34 kohsuke Exp $
 */

public class Main {
    public static void main( String[] args ) throws Exception {
        
        // create JAXBContext for the primer.xsd
        JAXBContext context = JAXBContext.newInstance("primer");
        
        // create a new XML parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        
        // prepare a Splitter
        Splitter splitter = new Splitter(context);
        
        // connect two components
        reader.setContentHandler(splitter);
        
        for( int i=0; i<args.length; i++ ) {
            // parse all the documents specified via the command line.
            // note that XMLReader expects an URL, not a file name.
            // so we need conversion.
            reader.parse(new File(args[i]).toURI().toURL().toExternalForm());
        }
    }

}
