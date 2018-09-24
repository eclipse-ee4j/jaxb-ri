/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.sun.xml.bind.CycleRecoverable;

public class Main {
    public static void main(String[] args) throws JAXBException {
        // let's create an obvious cycle
        Person p = new Person();
        p.id = 5;
        p.name = "Joe Chin";
        p.parent = p;

        JAXBContext context = JAXBContext.newInstance(Person.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
        m.marshal(p,System.out);
    }
}
