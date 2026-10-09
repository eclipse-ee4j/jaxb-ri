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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The unmarshaller keeps its per-depth state objects between sibling subtrees. These documents
 * alternate mixed and element-only content, nil and non-nil values, and JAXBElement-wrapped
 * values at the same depths, so any state leaking from one subtree into the next shows up.
 */
public class StateReuseTest {

    @XmlRootElement(name = "root")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Root {
        @XmlElement(name = "node")
        public List<Node> nodes = new ArrayList<>();
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Node {
        @XmlElement public Text text;
        @XmlElement public Plain plain;
        @XmlElement(nillable = true) public Integer n;
        @XmlElementRef(name = "wrapped", type = JAXBElement.class) public JAXBElement<String> wrapped;
    }

    /** Mixed content: whitespace between children is significant. */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Text {
        @XmlMixed @XmlAnyElement public List<Object> content = new ArrayList<>();
    }

    /** Element-only content: whitespace between children is ignored. */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Plain {
        @XmlElement public List<String> v = new ArrayList<>();
    }

    @XmlRegistry
    public static class ObjectFactory {
        @XmlElementDecl(name = "wrapped")
        public JAXBElement<String> createWrapped(String value) {
            return new JAXBElement<>(new QName("wrapped"), String.class, value);
        }
    }

    private static final String DOC = "<root>"
            + "<node><text>a <b>x</b> c</text><plain> <v>1</v> <v>2</v> </plain><n>5</n><wrapped>w1</wrapped></node>"
            + "<node><plain> <v>3</v> </plain><text> <i/> </text><n xsi:nil='true' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'/></node>"
            + "<node><text>t</text><n>6</n><wrapped>w3</wrapped></node>"
            + "<node><plain><v>4</v></plain></node>"
            + "</root>";

    private static void check(Root root) {
        assertEquals(4, root.nodes.size());
        Node a = root.nodes.get(0), b = root.nodes.get(1), c = root.nodes.get(2), d = root.nodes.get(3);

        assertEquals(3, a.text.content.size());
        assertEquals("a ", a.text.content.get(0));
        assertEquals(" c", a.text.content.get(2));
        assertEquals(List.of("1", "2"), a.plain.v);
        assertEquals(5, a.n);
        assertEquals("w1", a.wrapped.getValue());

        assertEquals(List.of("3"), b.plain.v);
        assertEquals(3, b.text.content.size());
        assertEquals(" ", b.text.content.get(0));
        assertEquals(" ", b.text.content.get(2));
        assertNull(b.n);
        assertNull(b.wrapped);

        assertEquals(List.of("t"), c.text.content);
        assertNull(c.plain);
        assertEquals(6, c.n);
        assertEquals("w3", c.wrapped.getValue());

        assertEquals(List.of("4"), d.plain.v);
        assertNull(d.text);
        assertNull(d.n);
        assertNull(d.wrapped);
    }

    @Test
    public void siblingSubtreesDoNotShareState() throws Exception {
        Unmarshaller u = JAXBContext.newInstance(Root.class, ObjectFactory.class).createUnmarshaller();
        // twice on the same unmarshaller, through SAX and StAX
        for (int i = 0; i < 2; i++) {
            check((Root) u.unmarshal(new StringReader(DOC)));
            check((Root) u.unmarshal(XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(DOC))));
        }
    }

    @Test
    public void rootJaxbElementAfterAFailedDocument() throws Exception {
        Unmarshaller u = JAXBContext.newInstance(Root.class, ObjectFactory.class).createUnmarshaller();
        try {
            u.unmarshal(new StringReader("<root><node><text>a<b>"));
        } catch (Exception expected) {
            // truncated document
        }
        Object o = u.unmarshal(new StringReader("<wrapped>v</wrapped>"));
        assertTrue(o instanceof JAXBElement);
        assertEquals("v", ((JAXBElement<?>) o).getValue());
        check((Root) u.unmarshal(new StringReader(DOC)));
    }
}
