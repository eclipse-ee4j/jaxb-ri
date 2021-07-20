/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.File;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

public class JaxbCDATASample {

    public static void main(String[] args) throws Exception {
        // unmarshal a doc
        JAXBContext jc = JAXBContext.newInstance("...");
        Unmarshaller u = jc.createUnmarshaller();
        Object o = u.unmarshal(...);

        // create a JAXB marshaller
        Marshaller m = jc.createMarshaller();

        // get an Apache XMLSerializer configured to generate CDATA
        XMLSerializer serializer = getXMLSerializer();

        // marshal using the Apache XMLSerializer
        m.marshal(o, serializer.asContentHandler());
    }

    private static XMLSerializer getXMLSerializer() {
        // configure an OutputFormat to handle CDATA
        OutputFormat of = new OutputFormat();

        // specify which of your elements you want to be handled as CDATA.
        // The use of the '^' between the namespaceURI and the localname
        // seems to be an implementation detail of the xerces code.
	// When processing xml that doesn't use namespaces, simply omit the
	// namespace prefix as shown in the third CDataElement below.
        of.setCDataElements(
			    new String[] { "ns1^foo",   // <ns1:foo>
					   "ns2^bar",   // <ns2:bar>
					   "^baz" });   // <baz>

        // set any other options you'd like
        of.setPreserveSpace(true);
        of.setIndenting(true);

        // create the serializer
        XMLSerializer serializer = new XMLSerializer(of);
        serializer.setOutputByteStream(System.out);

        return serializer;
    }

}
