/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.unmarshaller;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrLfMarshalUnmarshalTest {

    private static final String VALUE = "abc\r\nd\re\nf";
    private Bean fElem;
    private Marshaller fMarshaller;
    private Unmarshaller fUnmarshaller;

    @BeforeEach
    public void setUp() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Bean.class);
        fMarshaller = ctx.createMarshaller();
        fUnmarshaller = ctx.createUnmarshaller();

        fElem = new Bean();
        fElem.setAttributeString(VALUE);
        fElem.setElementString(VALUE);
    }

    @Test
    public void testWriter() throws Exception {
        StringWriter sw = new StringWriter();
        fMarshaller.marshal(fElem, sw);
        String resultXml = sw.toString();
        System.out.println(resultXml);
        Bean resultElem = (Bean) fUnmarshaller.unmarshal(new StringReader(resultXml));
        assertEquals(VALUE, resultElem.getAttributeString());
        assertEquals(VALUE, resultElem.getElementString());
    }

    @Test
    public void testDomAndTransform() throws Exception {
        StringWriter sw = new StringWriter();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document d = db.newDocument();
        DOMResult result = new DOMResult(d);
        fMarshaller.marshal(fElem, result);
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.transform(new DOMSource(d), new StreamResult(sw));
        String resultXml = sw.toString();
        System.out.println(resultXml);
        Bean resultElem = (Bean) fUnmarshaller.unmarshal(new StringReader(resultXml));
        assertEquals(VALUE, resultElem.getAttributeString());
        assertEquals(VALUE, resultElem.getElementString());
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"fElementString", "fAttributeString"})
    @XmlRootElement(name = "Bean")
    public static class Bean {

        @XmlElement(name = "ElementString")
        protected String fElementString;

        @XmlAttribute(name = "AttributeString")
        protected String fAttributeString;

        public String getElementString() {
            return fElementString;
        }

        public void setElementString(String elementString) {
            fElementString = elementString;
        }

        public String getAttributeString() {
            return fAttributeString;
        }

        public void setAttributeString(String attributeString) {
            fAttributeString = attributeString;
        }
    }
}
