/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import org.glassfish.jaxb.runtime.api.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

public class NsRemapTest {

    @Test
    public void testNsRemap() throws Exception {
        TypeReference tr1 = new TypeReference(new QName("", "generalAddress"),
                GeneralAddress.class, GeneralAddress.class.getAnnotations());
        TypeReference tr2 = new TypeReference(new QName("", "address"),
                Address.class, Address.class.getAnnotations());

        JAXBRIContext c = JAXBRIContext.newInstance(new Class[] {GeneralAddress.class, Address.class},
                Arrays.asList(tr1,tr2), Collections.emptyMap(), "http://test.com/ns",false, null);

        Address ai = new Address();
        ai.setList("...list...");
        GeneralAddress ga = new GeneralAddress();
        ga.setAddress(ai);

        StringWriter sw = new StringWriter();
        c.createMarshaller().marshal(ga, sw);
        Assert.assertTrue(sw.toString().contains(
                "<ns2:generalAddress xmlns:ns2=\"http://test.com/ns\"><ns2:address><ns2:list>...list...</ns2:list></ns2:address></ns2:generalAddress>"));
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @XmlRootElement(name = "generalAddress")
    public static class GeneralAddress implements Serializable {

        private final static long serialVersionUID = 1L;
        protected Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address value) {
            this.address = value;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @XmlRootElement(name = "address")
    public static class Address implements Serializable {

        private final static long serialVersionUID = 1L;
        protected String list;

        public String getList() {
            return list;
        }

        public void setList(String value) {
            this.list = value;
        }

    }

}
