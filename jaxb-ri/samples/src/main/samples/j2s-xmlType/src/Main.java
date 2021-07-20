/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: Main.java,v 1.2 2009-11-11 14:17:30 pavel_bucek Exp $
 */


import java.io.File;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import address.USAddress;
import address.PurchaseOrderType;

public class Main {
    public static void main(String[] args) throws Exception {

        // Demonstates shipping and billing data printed in the property
        // order defined by the propOrder annotation element in class 
        // USAddress.        
        JAXBContext jc = JAXBContext.newInstance(PurchaseOrderType.class);
        Unmarshaller u = jc.createUnmarshaller();
        try {
            PurchaseOrderType poType = (PurchaseOrderType)u.unmarshal(new
            File("src/testData.xml"));
            System.out.println(poType.toString());
        } catch(jakarta.xml.bind.UnmarshalException e){
            System.out.println("Main: " + e);
        }    
    }
}

