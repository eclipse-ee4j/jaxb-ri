/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.junit.Assert;
import org.junit.Test;

public class InheritanceTest {

    @Test
    public void aTest() throws Throwable {
        ChildDTO child = new ChildDTO();
        child.setName("aaa");

        final Marshaller marshaller = JAXBContext.newInstance(ChildDTO.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(child, stringWriter);
        String xmlAsString = stringWriter.toString();
        Assert.assertFalse(xmlAsString.contains("parent"));
    }
}
