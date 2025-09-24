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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class UnmarshallingXmlAnyElementMixedWithWhitespacesTest {

    private static final String BASIC_BODY_SAMPLE =
            "<body><ul><li> <span>l</span></li> <li> <span> l</span></li><li> <span> </span> </li></ul></body>";

    private static final String BODY_WITH_NEWLINE_SAMPLE =
            "<body><ul><li>\n<span>l</span></li>\n<li>\n<span>\nl</span></li><li>\n<span>\n</span>\n</li></ul></body>";

    private static final String DOCUMENT_SAMPLE = "<document> "
            + "<metadata key=\"name\"> testing document</metadata>"
            + " <metadata key=\"timestamp\">123456789</metadata>"
            + " <content>"
            + " <header>header</header> "
            + "<body><ul><li> <span>l</span></li> <li> <span> l</span></li><li> <span> </span> </li></ul></body>"
            + " <footer>footer</footer>"
            + " </content> "
            + "</document>";

    private static final String DOCUMENT_EXPECTED = "<document>"
            + "<metadata key=\"name\"> testing document</metadata>"
            + "<metadata key=\"timestamp\">123456789</metadata>"
            + "<content>"
            + "<header>header</header>"
            + "<body><ul><li> <span>l</span></li> <li> <span> l</span></li><li> <span> </span> </li></ul></body>"
            + "<footer>footer</footer>"
            + "</content>"
            + "</document>";

    private static final String MIXED_CONTENT_SAMPLE = "<composition>Some text before, " + DOCUMENT_SAMPLE + "\n"
            + " between " + DOCUMENT_SAMPLE + " and after</composition>";

    private static final String MIXED_CONTENT_EXPECTED = "<composition>Some text before, " + DOCUMENT_EXPECTED + "\n"
            + " between " + DOCUMENT_EXPECTED + " and after</composition>";

    private static JAXBContext context;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        context = JAXBContext.newInstance(MixedContent.class);
    }

    @Test
    public void shouldParseAndSerializeKeepingWhitespacesWithSingleMixed() throws JAXBException {
        // given
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = new ByteArrayInputStream(BASIC_BODY_SAMPLE.getBytes(StandardCharsets.UTF_8));
        // when
        JAXBElement<Body> element = unmarshaller.unmarshal(new StreamSource(inputStream), Body.class);
        String actual = serializeObject(element);
        // then
        assertEquals(BASIC_BODY_SAMPLE, actual);
    }

    @Test
    public void shouldParseAndSerializeKeepingLineBreaksWithSingleMixed() throws JAXBException {
        // given
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = new ByteArrayInputStream(BODY_WITH_NEWLINE_SAMPLE.getBytes(StandardCharsets.UTF_8));
        // when
        JAXBElement<Body> element = unmarshaller.unmarshal(new StreamSource(inputStream), Body.class);
        String actual = serializeObject(element);
        // then
        assertEquals(BODY_WITH_NEWLINE_SAMPLE, actual);
    }

    @Test
    public void shouldParseAndSerializeKeepingWhitespacesOnMixedWithSiblings() throws JAXBException {
        // given
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = new ByteArrayInputStream(DOCUMENT_SAMPLE.getBytes(StandardCharsets.UTF_8));
        // when
        JAXBElement<Document> element = unmarshaller.unmarshal(new StreamSource(inputStream), Document.class);
        String actual = serializeObject(element);
        // then
        assertEquals(DOCUMENT_EXPECTED, actual);
    }

    @Test
    public void shouldParseAndSerializeKeepingWhitespacesOnMixedWithElementRefs() throws JAXBException {
        // given
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = new ByteArrayInputStream(MIXED_CONTENT_SAMPLE.getBytes(StandardCharsets.UTF_8));
        // when
        JAXBElement<MixedContent> element = unmarshaller.unmarshal(new StreamSource(inputStream), MixedContent.class);
        String actual = serializeObject(element);
        // then
        assertEquals(MIXED_CONTENT_EXPECTED, actual);
        assertInstanceOf(String.class, element.getValue().elements.get(0));
        assertInstanceOf(Document.class, element.getValue().elements.get(1));
    }

    private String serializeObject(JAXBElement<?> element) throws JAXBException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.marshal(element.getValue(), outputStream);
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "document")
    private static class Document {

        @XmlElement
        private List<Metadata> metadata;

        @XmlElement
        private Content content;

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlRootElement(name = "metadata")
        private static class Metadata {

            @XmlAttribute
            private String key;

            @XmlValue
            private String value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "content")
    private static class Content {

        @XmlElement
        private String header;

        @XmlElement
        private Body body;

        @XmlElement
        private String footer;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "body")
    private static class Body {

        @XmlAnyElement
        @XmlMixed
        private List<Element> nodes;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "composition")
    private static class MixedContent {

        @XmlAnyElement
        @XmlElementRefs({@XmlElementRef(name = "document", type = Document.class)})
        @XmlMixed
        private List<Object> elements;
    }

}
