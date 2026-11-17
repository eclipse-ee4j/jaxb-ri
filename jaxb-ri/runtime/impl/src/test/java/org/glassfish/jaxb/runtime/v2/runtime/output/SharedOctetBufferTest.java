/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.output;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SharedOctetBufferTest {

    @XmlRootElement
    public static class Doc {
        public String text;
        public int number;

        public Doc() {
        }

        Doc(String text, int number) {
            this.text = text;
            this.number = number;
        }
    }

    /** Records the identity of every array handed to {@code write(byte[], int, int)}. */
    private static final class RecordingStream extends OutputStream {
        final List<byte[]> arrays = new ArrayList<>();
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        @Override
        public void write(int b) {
            bytes.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            arrays.add(b);
            bytes.write(b, off, len);
        }
    }

    private static Marshaller marshaller(boolean formatted) throws Exception {
        Marshaller m = JAXBContext.newInstance(Doc.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
        return m;
    }

    @Test
    public void marshallerReusesSameBufferAcrossMarshals() throws Exception {
        for (boolean formatted : new boolean[] {false, true}) {
            Marshaller m = marshaller(formatted);
            RecordingStream first = new RecordingStream();
            RecordingStream second = new RecordingStream();
            m.marshal(new Doc("a", 1), first);
            m.marshal(new Doc("b", 2), second);
            assertFalse(first.arrays.isEmpty());
            assertFalse(second.arrays.isEmpty());
            assertSame(first.arrays.get(0), second.arrays.get(0));
        }
    }

    @Test
    public void differentMarshallersDoNotShareBuffer() throws Exception {
        RecordingStream first = new RecordingStream();
        RecordingStream second = new RecordingStream();
        marshaller(false).marshal(new Doc("a", 1), first);
        marshaller(false).marshal(new Doc("a", 1), second);
        assertNotSame(first.arrays.get(0), second.arrays.get(0));
    }

    @Test
    public void consecutiveDocumentsDoNotLeakIntoEachOther() throws Exception {
        for (boolean formatted : new boolean[] {false, true}) {
            Marshaller shared = marshaller(formatted);
            StringBuilder big = new StringBuilder();
            for (int i = 0; i < 50; i++) {
                big.append("filler");
            }
            big.append("LEAKMARKER");
            Doc small = new Doc("x", 7);

            ByteArrayOutputStream firstOut = new ByteArrayOutputStream();
            shared.marshal(new Doc(big.toString(), 123456), firstOut);
            ByteArrayOutputStream secondOut = new ByteArrayOutputStream();
            shared.marshal(small, secondOut);

            ByteArrayOutputStream expected = new ByteArrayOutputStream();
            marshaller(formatted).marshal(small, expected);

            assertTrue(new String(firstOut.toByteArray(), StandardCharsets.UTF_8).contains("LEAKMARKER"));
            assertArrayEquals(expected.toByteArray(), secondOut.toByteArray());
            assertFalse(new String(secondOut.toByteArray(), StandardCharsets.UTF_8).contains("LEAKMARKER"));
        }
    }

    private static final int[] VALUES = {
        0, 1, -1, 9, 10, -10, 99, 100, 12345, -12345, 999999999, 1000000000,
        Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE + 1
    };

    private static byte[] render(byte[] buffer, int padding, int value) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        UTF8XmlOutput out = new UTF8XmlOutput(bos, new Encoded[0], null, buffer);
        for (int i = 0; i < padding; i++) {
            out.write('x');
        }
        out.text(value);
        out.text(value);
        out.flushBuffer();
        return bos.toByteArray();
    }

    @Test
    public void integersAcrossBufferBoundaryMatchStandardBuffer() throws IOException {
        for (int size = 4; size <= 40; size++) {
            for (int padding = 0; padding <= size + 2; padding++) {
                for (int value : VALUES) {
                    byte[] expected = render(new byte[1024], padding, value);
                    assertEquals("x".repeat(padding) + value + value,
                            new String(expected, StandardCharsets.US_ASCII));
                    assertArrayEquals(expected, render(new byte[size], padding, value),
                            "size=" + size + " padding=" + padding + " value=" + value);
                }
            }
        }
    }

    @Test
    public void bufferShorterThanFourBytesIsRejected() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Encoded[] names = new Encoded[0];
        for (int len = 0; len < 4; len++) {
            final byte[] buf = new byte[len];
            assertThrows(IllegalArgumentException.class, () -> new UTF8XmlOutput(bos, names, null, buf));
            assertThrows(IllegalArgumentException.class, () -> new C14nXmlOutput(bos, names, true, null, buf));
            assertThrows(IllegalArgumentException.class, () -> new IndentingUTF8XmlOutput(bos, "  ", names, null, buf));
        }
        // four bytes is the minimum accepted
        new UTF8XmlOutput(bos, names, null, new byte[4]);
    }

    @Test
    public void nullBufferIsRejected() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Encoded[] names = new Encoded[0];
        NullPointerException e1 = assertThrows(NullPointerException.class,
                () -> new UTF8XmlOutput(bos, names, null, null));
        assertEquals("octetBuffer", e1.getMessage());
        NullPointerException e2 = assertThrows(NullPointerException.class,
                () -> new C14nXmlOutput(bos, names, true, null, null));
        assertEquals("octetBuffer", e2.getMessage());
        NullPointerException e3 = assertThrows(NullPointerException.class,
                () -> new IndentingUTF8XmlOutput(bos, "  ", names, null, null));
        assertEquals("octetBuffer", e3.getMessage());
    }
}
