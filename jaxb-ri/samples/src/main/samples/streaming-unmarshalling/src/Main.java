/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import org.xml.sax.XMLReader;
import primer.PurchaseOrderType;
import primer.PurchaseOrders;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

/*
 * @(#)$Id: Main.java,v 1.1 2007-12-05 00:49:38 kohsuke Exp $
 */

public class Main {
    public static void main( String[] args ) throws Exception {
        
        // create JAXBContext for the primer.xsd
        JAXBContext context = JAXBContext.newInstance("primer");

        Unmarshaller unmarshaller = context.createUnmarshaller();

        // purchase order notification callback
        final PurchaseOrders.Listener orderListener = new PurchaseOrders.Listener() {
            public void handlePurchaseOrder(PurchaseOrders purchaseOrders, PurchaseOrderType purchaseOrder) {
                System.out.println("this order will be shipped to "
                        + purchaseOrder.getShipTo().getName());
            }
        };

        // install the callback on all PurchaseOrders instances
        unmarshaller.setListener(new Unmarshaller.Listener() {
            public void beforeUnmarshal(Object target, Object parent) {
                if(target instanceof PurchaseOrders) {
                    ((PurchaseOrders)target).setPurchaseOrderListener(orderListener);
                }
            }

            public void afterUnmarshal(Object target, Object parent) {
                if(target instanceof PurchaseOrders) {
                    ((PurchaseOrders)target).setPurchaseOrderListener(null);
                }
            }
        });

        // create a new XML parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setContentHandler(unmarshaller.getUnmarshallerHandler());

        for (String arg : args) {
            // parse all the documents specified via the command line.
            // note that XMLReader expects an URL, not a file name.
            // so we need conversion.
            reader.parse(new File(arg).toURI().toString());
        }
    }
}
