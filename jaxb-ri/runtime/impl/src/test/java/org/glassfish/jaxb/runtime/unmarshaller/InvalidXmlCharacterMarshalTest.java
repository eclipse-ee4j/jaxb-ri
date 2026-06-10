/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
import org.glassfish.jaxb.core.marshaller.InvalidXmlCharacterPolicy;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reproduces <a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/614">JAXB-614</a>:
 * characters legal in a Java String but illegal in XML 1.0 were marshalled
 * verbatim, producing XML that cannot be parsed back in. Verifies the
 * {@link InvalidXmlCharacterPolicy} property fixes it.
 */
public class InvalidXmlCharacterMarshalTest {

    // A legal value plus stray C0 control characters (ENQ 0x05, ACK 0x06,
    // UNIT SEPARATOR 0x1f) of the kind seen in the INC7990877 payloads.
    private static final String DIRTY = "abc\u0005\u0006d\u001fe";
    private static final String CLEAN = "abcde";

    private Marshaller marshaller(InvalidXmlCharacterPolicy policy) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Bean.class);
        Marshaller m = ctx.createMarshaller();
        if (policy != null) {
            m.setProperty(InvalidXmlCharacterPolicy.PROPERTY_NAME, policy.name());
        }
        return m;
    }

    private Bean newBean() {
        Bean b = new Bean();
        b.setElementString(DIRTY);
        b.setAttributeString(DIRTY);
        return b;
    }

    /** Marshal to an OutputStream (UTF-8) — the path api-trips uses for HTTP responses. */
    private String marshalToUtf8(Marshaller m, Bean bean) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        m.marshal(bean, bos);
        return bos.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void defaultPolicyReproducesBug() throws Exception {
        String xml = marshalToUtf8(marshaller(null), newBean());
        // The illegal bytes are emitted verbatim ...
        assertTrue(xml.indexOf('\u0005') >= 0, "default WRITE should emit illegal chars verbatim");
        // ... and the result is not parseable, which is the actual incident symptom.
        assertThrows(Exception.class, () -> unmarshal(xml));
    }

    @Test
    public void stripProducesParseableXmlOnOutputStream() throws Exception {
        String xml = marshalToUtf8(marshaller(InvalidXmlCharacterPolicy.STRIP), newBean());
        assertNoControlChars(xml);
        Bean out = (Bean) unmarshal(xml);
        assertEquals(CLEAN, out.getElementString());
        assertEquals(CLEAN, out.getAttributeString());
    }

    @Test
    public void stripProducesParseableXmlOnWriter() throws Exception {
        StringWriter sw = new StringWriter();
        marshaller(InvalidXmlCharacterPolicy.STRIP).marshal(newBean(), sw);
        String xml = sw.toString();
        assertNoControlChars(xml);
        Bean out = (Bean) unmarshal(xml);
        assertEquals(CLEAN, out.getElementString());
        assertEquals(CLEAN, out.getAttributeString());
    }

    @Test
    public void replaceSubstitutesReplacementCharacter() throws Exception {
        String xml = marshalToUtf8(marshaller(InvalidXmlCharacterPolicy.REPLACE), newBean());
        assertNoControlChars(xml);
        Bean out = (Bean) unmarshal(xml);
        // Each of the three illegal chars becomes U+FFFD.
        assertEquals("abc\uFFFD\uFFFDd\uFFFDe", out.getElementString());
        assertEquals("abc\uFFFD\uFFFDd\uFFFDe", out.getAttributeString());
    }

    @Test
    public void legalControlCharsAreNotStripped() throws Exception {
        Bean b = new Bean();
        b.setElementString("line1\nline2\twith tab");
        String xml = marshalToUtf8(marshaller(InvalidXmlCharacterPolicy.STRIP), b);
        Bean out = (Bean) unmarshal(xml);
        // Tab/newline are legal XML chars and must survive (newline round-trips;
        // we assert the tab and text are intact).
        assertTrue(out.getElementString().contains("\twith tab"));
        assertTrue(out.getElementString().contains("line1"));
        assertTrue(out.getElementString().contains("line2"));
    }

    private void assertNoControlChars(String xml) {
        for (int i = 0; i < xml.length(); i++) {
            assertFalse(InvalidXmlCharacterPolicy.isInvalid(xml.charAt(i)),
                    "unexpected illegal XML char U+" + Integer.toHexString(xml.charAt(i)) + " at " + i);
        }
    }

    private Object unmarshal(String xml) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Bean.class);
        Unmarshaller u = ctx.createUnmarshaller();
        return u.unmarshal(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
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
