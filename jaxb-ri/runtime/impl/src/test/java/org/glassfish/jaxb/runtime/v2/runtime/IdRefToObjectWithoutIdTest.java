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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.MarshalException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Marshalling an IDREF to an object whose ID is null reports a validation event
 * instead of failing with a NullPointerException
 * (<a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/1007">#1007</a>).
 */
public class IdRefToObjectWithoutIdTest {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AttributeIdItem {
        @XmlID
        @XmlAttribute
        String id;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ElementIdItem {
        @XmlID
        @XmlElement
        String key;
    }

    @XmlRootElement(name = "root")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Root {
        @XmlElement(name = "attributeIdItem")
        List<AttributeIdItem> attributeIdItems = new ArrayList<>();

        @XmlElement(name = "elementIdItem")
        List<ElementIdItem> elementIdItems = new ArrayList<>();

        @XmlIDREF
        @XmlAttribute
        AttributeIdItem attributeIdRef;

        @XmlIDREF
        @XmlElement
        ElementIdItem elementIdRef;
    }

    @Test
    public void referenceToAttributeIdWithoutValueIsReported() throws Exception {
        Root root = new Root();
        AttributeIdItem item = new AttributeIdItem();
        root.attributeIdItems.add(item);
        root.attributeIdRef = item;

        List<ValidationEvent> events = marshalCollectingEvents(root);

        assertEquals(1, events.size());
        assertEquals(ValidationEvent.ERROR, events.get(0).getSeverity());
        assertTrue(events.get(0).getMessage().contains("doesn't have an ID"), events.get(0).getMessage());
    }

    @Test
    public void referenceToElementIdWithoutValueIsReported() throws Exception {
        Root root = new Root();
        ElementIdItem item = new ElementIdItem();
        root.elementIdItems.add(item);
        root.elementIdRef = item;

        List<ValidationEvent> events = marshalCollectingEvents(root);

        assertEquals(1, events.size());
        assertTrue(events.get(0).getMessage().contains("doesn't have an ID"), events.get(0).getMessage());
    }

    @Test
    public void referenceToObjectWithoutIdFailsWithMarshalExceptionByDefault() throws Exception {
        Root root = new Root();
        AttributeIdItem item = new AttributeIdItem();
        root.attributeIdItems.add(item);
        root.attributeIdRef = item;

        Marshaller marshaller = JAXBContext.newInstance(Root.class).createMarshaller();

        assertThrows(MarshalException.class, () -> marshaller.marshal(root, new StringWriter()));
    }

    private static List<ValidationEvent> marshalCollectingEvents(Root root) throws Exception {
        List<ValidationEvent> events = new ArrayList<>();
        Marshaller marshaller = JAXBContext.newInstance(Root.class).createMarshaller();
        marshaller.setEventHandler(event -> {
            events.add(event);
            return true;
        });
        marshaller.marshal(root, new StringWriter());
        return events;
    }
}
