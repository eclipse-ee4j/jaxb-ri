/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * An {@link XmlElementRef} to a root element class with simple content ({@link XmlValue})
 * (<a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/1027">#1027</a>).
 */
public class ElementRefToSimpleContentTest {

    @XmlRootElement(name = "value")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Value {
        @XmlValue
        String text;

        public Value() {
        }

        Value(String text) {
            this.text = text;
        }
    }

    @XmlRootElement(name = "single")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Single {
        @XmlElementRef
        Value value;
    }

    @XmlRootElement(name = "multiple")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Multiple {
        @XmlElementRef
        List<Value> values = new ArrayList<>();
    }

    @Test
    public void singleReference() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Single.class);
        Single single = new Single();
        single.value = new Value("v");

        String xml = marshal(context, single);

        assertEquals("<single><value>v</value></single>", xml);
        Single unmarshalled = (Single) context.createUnmarshaller().unmarshal(new StringReader(xml));
        assertEquals("v", unmarshalled.value.text);
    }

    @Test
    public void referenceCollection() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Multiple.class);
        Multiple multiple = new Multiple();
        multiple.values.add(new Value("a"));
        multiple.values.add(new Value("b"));

        String xml = marshal(context, multiple);

        assertEquals("<multiple><value>a</value><value>b</value></multiple>", xml);
        Multiple unmarshalled = (Multiple) context.createUnmarshaller().unmarshal(new StringReader(xml));
        assertEquals(2, unmarshalled.values.size());
        assertEquals("b", unmarshalled.values.get(1).text);
    }

    private static String marshal(JAXBContext context, Object value) throws Exception {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(value, writer);
        return writer.toString();
    }
}
