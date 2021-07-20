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
 * $Id: Main.java,v 1.1 2007-12-05 00:49:28 kohsuke Exp $
 */


import java.io.File;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import shoppingCart.KitchenWorldBasket;

public class Main {
    public static void main(String[] args) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(KitchenWorldBasket.class);
        Unmarshaller u = jc.createUnmarshaller();
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try {
            KitchenWorldBasket kwBasket = (KitchenWorldBasket)u.unmarshal(new File("src/shoppingCartData.xml"));
            
            // Demonstrate adapter's unmarshal integrated data into HashMap properly
            System.out.println(kwBasket.toString());
            
            // Demonstate adapter's marshal writes the data properly
            m.marshal(kwBasket, System.out);
        } catch(jakarta.xml.bind.UnmarshalException e){
            System.out.println("Main: " + e);
        }    
    }
}

