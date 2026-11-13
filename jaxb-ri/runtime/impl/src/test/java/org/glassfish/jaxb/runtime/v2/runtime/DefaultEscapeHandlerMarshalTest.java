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
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

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
            assertEquals(new String(generic, StandardCharsets.UTF_8),
                    new String(fast, StandardCharsets.UTF_8));
            assertArrayEquals(generic, fast);
        }
    }

    private static Object outcome(Root root, boolean generic) {
        try {
            return marshal(root, generic, false);
        } catch (Throwable t) {
            return t.getClass();
        }
    }

    private static Object comparable(Object o) {
        return o instanceof byte[] ? new String((byte[]) o, StandardCharsets.ISO_8859_1) : o;
    }

    @Test
    public void isolatedSurrogatesBehaveTheSame() {
        String[] texts = {
            "\uD83D", "\uDE00", "a\uD83D", "\uDE00b", "\uD83Da", "a\uDE00\uD83Db",
            "\uDE00\uD83D", "&\uD83D", "\uD83D\uD83D", "\uDE00\uDE00", "\uD83D\uDE00\uD83D"
        };
        for (String t : texts) {
            Root root = new Root();
            root.att = t;
            assertEquals(comparable(outcome(root, true)), comparable(outcome(root, false)), "attribute " + t);
            root = new Root();
            root.item.add(t);
            assertEquals(comparable(outcome(root, true)), comparable(outcome(root, false)), "element " + t);
            root = new Root();
            root.leaf = new Leaf();
            root.leaf.a = t;
            root.leaf.v = t;
            assertEquals(comparable(outcome(root, true)), comparable(outcome(root, false)), "leaf " + t);
        }
    }

    private static final int ITEMS = 10_000;
    private static final int ITEM_LENGTH = 64;

    /**
     * Reflective access to {@code com.sun.management.ThreadMXBean}, which the module descriptor of
     * the runtime does not require. Null if it is not available.
     */
    private static Object allocationBean() {
        try {
            Object bean = Class.forName("java.lang.management.ManagementFactory")
                    .getMethod("getThreadMXBean").invoke(null);
            Class<?> sunBean = Class.forName("com.sun.management.ThreadMXBean");
            if (!sunBean.isInstance(bean)
                    || !(Boolean) sunBean.getMethod("isThreadAllocatedMemorySupported").invoke(bean))
                return null;
            sunBean.getMethod("setThreadAllocatedMemoryEnabled", boolean.class).invoke(bean, true);
            return bean;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            return null;
        }
    }

    private static long allocatedBy(Object bean, Marshaller m, Root root) throws Exception {
        Method allocated = Class.forName("com.sun.management.ThreadMXBean")
                .getMethod("getThreadAllocatedBytes", long.class);
        long id = Thread.currentThread().getId();
        OutputStream sink = new OutputStream() {
            @Override
            public void write(int b) {
            }

            @Override
            public void write(byte[] b, int off, int len) {
            }
        };
        long best = Long.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            long before = (Long) allocated.invoke(bean, id);
            m.marshal(root, sink);
            best = Math.min(best, (Long) allocated.invoke(bean, id) - before);
        }
        return best;
    }

    /**
     * The generic path allocates, per text, at least a {@code char[]} copy (2 bytes per char), a
     * {@code StringBuilder} and a {@code String}; the default path allocates none of them.
     * Both are measured in the same JVM on the same input, so only the difference counts.
     */
    @Test
    public void defaultHandlerDoesNotAllocateTheIntermediateCopies() throws Exception {
        Object bean = allocationBean();
        assumeTrue(bean != null, "thread allocation counters are not available");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ITEM_LENGTH; i++)
            sb.append(i % 16 == 0 ? '&' : (char) ('a' + i % 26));
        Root root = new Root();
        for (int i = 0; i < ITEMS; i++)
            root.item.add(sb.toString());

        JAXBContext ctx = JAXBContext.newInstance(Root.class);
        Marshaller fast = ctx.createMarshaller();
        Marshaller generic = ctx.createMarshaller();
        generic.setProperty(CharacterEscapeHandler.class.getName(), DELEGATE);

        long fastBytes = allocatedBy(bean, fast, root);
        long genericBytes = allocatedBy(bean, generic, root);
        // at least one byte per character, whereas the intermediate copies take about five
        long expectedSaving = (long) ITEMS * ITEM_LENGTH;
        assertTrue(genericBytes - fastBytes > expectedSaving,
                "default=" + fastBytes + " generic=" + genericBytes);
    }
}
