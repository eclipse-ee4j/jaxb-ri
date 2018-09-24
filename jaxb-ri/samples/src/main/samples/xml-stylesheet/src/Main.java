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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import primer.PurchaseOrderType;

/*
 * @(#)$Id: Main.java,v 1.2 2009-11-11 14:17:28 pavel_bucek Exp $
 */

public class Main {
    public static void main( String[] args ) throws Exception {
        
        // create JAXBContext for the primer.xsd
        JAXBContext context = JAXBContext.newInstance("primer");
        
        // unmarshal a document, just to marshal it back again.
        JAXBElement poe = (JAXBElement)context.createUnmarshaller().unmarshal(
            new File(args[0]));
        // we don't need to check the return value, because the unmarshal
        // method should haven thrown an exception if anything went wrong.
	PurchaseOrderType po = (PurchaseOrderType)poe.getValue();
        
        
        // Here's the real meat.
        // we configure marshaller not to print out xml decl,
        // we then print out XML decl plus stylesheet header on our own,
        // then have the marshaller print the real meat.
        
        System.out.println("<?xml version='1.0'?>");
        System.out.println("<?xml-stylesheet type='text/xsl' href='foobar.xsl' ?>");
        // if you need to put DOCTYPE decl, it can be easily done here.
        
        // create JAXB marshaller.
        Marshaller m = context.createMarshaller();
        // configure it
        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        // marshal
        m.marshal(poe,System.out);
    }

}
