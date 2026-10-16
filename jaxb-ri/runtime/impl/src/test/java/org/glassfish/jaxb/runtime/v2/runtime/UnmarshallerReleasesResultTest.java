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

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.stream.StreamSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallerImpl;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The unmarshaller does not keep a reference to the last unmarshalled object, whatever the
 * input (<a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/1100">#1100</a>).
 */
public class UnmarshallerReleasesResultTest {

    private static final String XML = "<item><name>first</name></item>";

    @XmlRootElement(name = "item")
    public static class Item {
        public String name;
    }

    @Test
    public void inputStreamResultIsReleased() throws Exception {
        UnmarshallerImpl unmarshaller = createUnmarshaller();
        Item item = (Item) unmarshaller.unmarshal(new ByteArrayInputStream(XML.getBytes(StandardCharsets.UTF_8)));
        assertEquals("first", item.name);
        assertNull(unmarshaller.getContext().getResult());
    }

    @Test
    public void inputSourceResultIsReleased() throws Exception {
        UnmarshallerImpl unmarshaller = createUnmarshaller();
        Item item = (Item) unmarshaller.unmarshal(new InputSource(new StringReader(XML)));
        assertEquals("first", item.name);
        assertNull(unmarshaller.getContext().getResult());
    }

    @Test
    public void xmlStreamReaderResultIsReleased() throws Exception {
        UnmarshallerImpl unmarshaller = createUnmarshaller();
        Item item = (Item) unmarshaller.unmarshal(
                XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(XML)));
        assertEquals("first", item.name);
        assertNull(unmarshaller.getContext().getResult());
    }

    @Test
    public void xmlEventReaderResultIsReleased() throws Exception {
        UnmarshallerImpl unmarshaller = createUnmarshaller();
        Item item = (Item) unmarshaller.unmarshal(
                XMLInputFactory.newFactory().createXMLEventReader(new StringReader(XML)));
        assertEquals("first", item.name);
        assertNull(unmarshaller.getContext().getResult());
    }

    @Test
    public void nodeResultIsReleased() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(XML)));
        UnmarshallerImpl unmarshaller = createUnmarshaller();
        Item item = (Item) unmarshaller.unmarshal(document);
        assertEquals("first", item.name);
        assertNull(unmarshaller.getContext().getResult());
    }

    @Test
    public void sourceWithExpectedTypeResultIsReleased() throws Exception {
        UnmarshallerImpl unmarshaller = createUnmarshaller();
        Item item = unmarshaller.unmarshal(new StreamSource(new StringReader(XML)), Item.class).getValue();
        assertEquals("first", item.name);
        assertNull(unmarshaller.getContext().getResult());
    }

    private static UnmarshallerImpl createUnmarshaller() throws Exception {
        return (UnmarshallerImpl) JAXBContext.newInstance(Item.class).createUnmarshaller();
    }
}
