/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 *  Author: Sekhar Vajjhala
 *
 *  $Id: Main.java,v 1.1 2007-12-05 00:49:36 kohsuke Exp $
 */  

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class Main {
    
    public static void main( String[] args ) {
        try {
            /**
             * Create a JAXBContext passing it a list of classes to be
             * marshalled.
             */
            JAXBContext jc = JAXBContext.newInstance(PurchaseOrder.class, USAddress.class, Address.class);
            
            // create a USAddress 
            USAddress addr = new USAddress();
            addr.name   = "Alice Smith";
            addr.street = "123 Maple Street";
            addr.city   = "Mill Valley";
            addr.state  = "CA";
            addr.zip    = 90952;

            // create a po type;
            PurchaseOrder po = new PurchaseOrder();
            po.shipTo = addr;

            // create a Marshaller
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

            // marshall the address
            System.out.println(".... Marshal USAddress ....");
            m.marshal(addr, System.out );
            /**
             * OBSERVATIONS:
             * a. the following print statement needed since Marshaller
             *    output does not contain a terminating new line
             *    character.
             */
            System.out.println("");

            /**
             * Marshall the purchase order. 
             *
             * OBSERVATIONS:
             * a. Initially, I forgot to add the PurchaseOrder to
             *    the list of classes in JAXBContext. So, when passed
             *    to the marshal method, this resulted in an
             *    exception.
             *         Exception in thread "main"
             *             java.lang.IllegalArgumentException: PurchaseOrder nor
             *             any of its super class is known to this context
             *
             *    I knew "not being known to this context" means that
             *    the class has to be specified in the
             *    JAXBContext. Would a newbie JAXB 2.0 be able to
             *    figure that out ?
             *
             * b. If a type is not known at runtime, then an
             *    alternative to throwing an exception is to
             *    use reflection to marshal the type. Castor uses this
             *    approach. 
             */
	    
            System.out.println(".... Marshal PurchaseOrder (USAddress type with xsi type)....");
            m.marshal(po, System.out );
            // following print statement needed to insert a new line character.
            System.out.println("");
         } catch( JAXBException je ) {
            je.printStackTrace();
         }
    }
}    
