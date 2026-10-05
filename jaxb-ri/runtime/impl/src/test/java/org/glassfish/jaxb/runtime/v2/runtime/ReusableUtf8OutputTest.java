/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.transform.stream.StreamSource;
import org.glassfish.jaxb.runtime.v2.runtime.output.XmlOutput;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReusableUtf8OutputTest {
    @Test
    void reusesOutputForSequentialStreamsAndResetsItsState() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Entry.class);
        MarshallerImpl marshaller = (MarshallerImpl) context.createMarshaller();
        ByteArrayOutputStream firstStream = new ByteArrayOutputStream();
        ByteArrayOutputStream secondStream = new ByteArrayOutputStream();

        XmlOutput firstOutput = marshaller.createWriter(firstStream);
        XmlOutput secondOutput = marshaller.createWriter(secondStream);
        assertSame(firstOutput, secondOutput);

        Entry first = new Entry("first", "value-one");
        Entry second = new Entry("a substantially longer value", "value-two");
        marshaller.marshal(first, firstStream);
        byte[] firstXml = firstStream.toByteArray();
        marshaller.marshal(second, secondStream);
        marshaller.marshal(first, firstStream);

        assertArrayEquals(firstXml, firstStream.toByteArray());
        assertEquals("value-two", context.createUnmarshaller().unmarshal(
                new StreamSource(new java.io.ByteArrayInputStream(secondStream.toByteArray())), Entry.class).getValue());
        assertTrue(new String(secondStream.toByteArray(), StandardCharsets.UTF_8)
                .contains("a substantially longer value"));
    }

    @XmlRootElement(name = "entry", namespace = "urn:reuse:test")
    @XmlType(propOrder = {"code", "value"})
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Entry {
        private String code;
        @XmlElement(namespace = "urn:reuse:value")
        private String value;

        public Entry() {}
        Entry(String code, String value) { this.code = code; this.value = value; }
        public String getCode() { return code; }
        public String getValue() { return value; }
    }
}
