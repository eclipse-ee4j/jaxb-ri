/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.File;
import java.io.FileOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import cardfile.BusinessCard;
import cardfile.Address;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.bind.ValidationEventLocator;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) throws Exception {
        final File f = new File("bcard.xml");

        // Illustrate two methods to create JAXBContext for j2s binding.
        // (1) by root classes newInstance(Class ...)
        JAXBContext context1 = JAXBContext.newInstance(BusinessCard.class);

        // (2) by package, requires jaxb.index file in package cardfile.
        //     newInstance(String packageNames)
        JAXBContext context2 = JAXBContext.newInstance("cardfile");
        
        Marshaller m = context1.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(getCard(), System.out);

        // illustrate optional unmarshal validation.
        Marshaller m2 = context1.createMarshaller();
        m2.marshal(getCard(), new FileOutputStream(f));
        Unmarshaller um = context2.createUnmarshaller();
        um.setSchema(getSchema("schema1.xsd"));
        Object bce = um.unmarshal(f);
        m.marshal(bce, System.out);
    }

    /** returns a JAXP 1.3 schema by parsing the specified resource. */
    static Schema getSchema(String schemaResourceName) throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        try {
            return sf.newSchema(Main.class.getResource(schemaResourceName));
        } catch (SAXException se) {
            // this can only happen if there's a deployment error and the resource is missing.
            throw se;
        }
    }

    private static BusinessCard getCard() {
        return new BusinessCard("John Doe", "Sr. Widget Designer", "Acme, Inc.",
                new Address(null, "123 Widget Way", "Anytown", "MA", (short) 12345), "123.456.7890",
                null, "123.456.7891", "John.Doe@Acme.ORG");
    }
}
