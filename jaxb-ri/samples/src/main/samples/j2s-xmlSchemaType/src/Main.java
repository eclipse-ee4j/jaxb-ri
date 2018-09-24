/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: Main.java,v 1.1 2007-12-05 00:49:30 kohsuke Exp $
 */


import java.io.File;
import java.io.FileOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import address.TrackingOrder;

public class Main {
    public static void main(String[] args) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(TrackingOrder.class);
        Unmarshaller u = jc.createUnmarshaller();
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try {
            TrackingOrder tOrder = (TrackingOrder)u.unmarshal(new File("src/trackingData.xml"));
            m.marshal(tOrder, System.out);
        } catch(javax.xml.bind.UnmarshalException e){
            System.out.println("Main: " + e);
        }
    } 
}

