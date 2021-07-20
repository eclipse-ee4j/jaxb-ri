/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.FileReader;

import javax.xml.stream.*;
import static javax.xml.stream.XMLStreamConstants.*;
import contact.Contact;

/*
 * Use is subject to the license terms.
 */

/**
 * 
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Main {

    public static void main(String[] args) throws Exception {
        
        String nameToLookFor = args[0];
        
        JAXBContext jaxbContext = JAXBContext.newInstance("contact"); 
        Unmarshaller um = jaxbContext.createUnmarshaller();

        // set up a parser
	XMLInputFactory xmlif = XMLInputFactory.newInstance();
	XMLStreamReader xmlr = 
	    xmlif.createXMLStreamReader(new FileReader("contact.xml"));
 
	// move to the root element and check its name.
        xmlr.nextTag(); 
        xmlr.require(START_ELEMENT, null, "addressBook");

        xmlr.nextTag(); // move to the first <contact> element.
        while (xmlr.getEventType() == START_ELEMENT) {

            // unmarshall one <contact> element into a JAXB Contact object
	    xmlr.require(START_ELEMENT, null, "contact");
            Contact contact = (Contact) um.unmarshal(xmlr);
            if( contact.getName().equals(nameToLookFor)) {
                // we found what we wanted to find. show it and quit now.
                System.out.println("the e-mail address is "+contact.getEmail());
                return;
            }
            if (xmlr.getEventType() == CHARACTERS) {
                xmlr.next(); // skip the whitespace between <contact>s.
	    }
        }
        System.out.println("Unable to find "+nameToLookFor);
    }
}
