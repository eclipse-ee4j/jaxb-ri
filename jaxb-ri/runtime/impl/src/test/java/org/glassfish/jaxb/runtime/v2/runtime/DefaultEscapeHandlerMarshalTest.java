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
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import org.glassfish.jaxb.core.marshaller.CharacterEscapeHandler;
import org.glassfish.jaxb.core.marshaller.MinimumEscapeHandler;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Marshalling to an {@code OutputStream} with the default UTF-8 escape handler must write the
 * same bytes as when an equivalent handler is installed explicitly, which forces the generic
 * {@code CharacterEscapeHandler} path of {@code UTF8XmlOutput}.
 */
public class DefaultEscapeHandlerMarshalTest {

    @XmlRootElement(name = "root", namespace = "urn:a&b")
    public static class Root {
        @XmlAttribute
        public String att;
        @XmlElement(namespace = "urn:a&b")
        public List<String> item = new ArrayList<>();
        @XmlElement(namespace = "urn:a&b")
        public Leaf leaf;
    }

    public static class Leaf {
        @XmlAttribute
        public String a;
        @XmlValue
        public String v;
    }

    /** Same escaping, but not the shared instance, so the marshaller takes the generic path. */
    private static final CharacterEscapeHandler DELEGATE = new CharacterEscapeHandler() {
        @Override
        public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
            MinimumEscapeHandler.theInstance.escape(ch, start, length, isAttVal, out);
        }
    };

    private static byte[] marshal(Object o, boolean generic, boolean formatted) throws Exception {
        Marshaller m = JAXBContext.newInstance(Root.class).createMarshaller();
        if (generic)
            m.setProperty(CharacterEscapeHandler.class.getName(), DELEGATE);
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.marshal(o, out);
        return out.toByteArray();
    }

    @Test
    public void marshalledBytesAreUnchanged() throws Exception {
        Root root = new Root();
        root.att = "a\"b\r\n\tc&<>' \u00E9\u20AC\uD83D\uDE00";
        root.item.add("x & y < z > w \" ' \r\n\t");
        root.item.add("\uD83D\uDE00 smile, \u00E9t\u00E9");
        root.item.add("");
        root.leaf = new Leaf();
        root.leaf.a = "]]> & \"";
        root.leaf.v = "text with ]]> and &amp; already";
        for (boolean formatted : new boolean[] {false, true}) {
            byte[] fast = marshal(root, false, formatted);
            byte[] generic = marshal(root, true, formatted);
            assertEquals(new String(generic, java.nio.charset.StandardCharsets.UTF_8),
                    new String(fast, java.nio.charset.StandardCharsets.UTF_8));
            assertArrayEquals(generic, fast);
        }
    }
}
