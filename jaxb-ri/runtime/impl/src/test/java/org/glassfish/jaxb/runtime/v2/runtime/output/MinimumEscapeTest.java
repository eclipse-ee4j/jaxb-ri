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

import org.glassfish.jaxb.core.marshaller.MinimumEscapeHandler;
import org.glassfish.jaxb.runtime.util.StringBuilderWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The single-pass escape used for the default UTF-8 handler must produce exactly the bytes of
 * {@link MinimumEscapeHandler} followed by {@link Encoded#set(String)}.
 */
public class MinimumEscapeTest {

    private static byte[] reference(String text, boolean attribute) throws IOException {
        StringBuilderWriter w = new StringBuilderWriter(text.length());
        MinimumEscapeHandler.theInstance.escape(text.toCharArray(), 0, text.length(), attribute, w);
        Encoded e = new Encoded();
        e.set(w.toString());
        return Arrays.copyOf(e.buf, e.len);
    }

    private static void assertSame(String text) throws IOException {
        for (boolean attribute : new boolean[] {false, true}) {
            Encoded e = new Encoded();
            assertTrue(e.setMinimumEscape(text, attribute));
            assertArrayEquals(reference(text, attribute), Arrays.copyOf(e.buf, e.len),
                    () -> "attribute=" + attribute + " text=" + text);
        }
    }

    @Test
    public void everyAsciiCharacter() throws IOException {
        for (char c = 0; c < 0x80; c++) {
            assertSame(String.valueOf(c));
            assertSame("a" + c + "b" + c + c);
        }
    }

    @Test
    public void knownForms() throws IOException {
        assertSame("");
        assertSame("plain text");
        assertSame("a&b<c>d\"e'f\tg\nh\ri");
        assertSame("&&&&\"\"\"\"");
        assertSame("caf\u00E9 \u00FF \u0100 \u07FF \u0800 \u20AC \uFFFF \uFFFD");
        Encoded e = new Encoded();
        assertTrue(e.setMinimumEscape("a\r\n\"", true));
        assertEquals("a&#13;&#10;&quot;", new String(e.buf, 0, e.len, java.nio.charset.StandardCharsets.UTF_8));
        assertTrue(e.setMinimumEscape("a\r\n\"\t", false));
        assertEquals("a&#13;\n\"\t", new String(e.buf, 0, e.len, java.nio.charset.StandardCharsets.UTF_8));
    }

    @Test
    public void randomStrings() throws IOException {
        Random random = new Random(42);
        char[] interesting = {'&', '<', '>', '"', '\'', '\r', '\n', '\t', ' ', 'a', 'Z', '0', '\u00E9', '\u07FF', '\u0800', '\u20AC', '\uFFFF'};
        for (int n = 0; n < 5000; n++) {
            char[] chars = new char[random.nextInt(40)];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = random.nextBoolean()
                        ? interesting[random.nextInt(interesting.length)]
                        : (char) random.nextInt(0xD800);
            }
            assertSame(new String(chars));
        }
    }

    @Test
    public void surrogatesAreLeftToTheGenericPath() {
        Encoded e = new Encoded();
        assertFalse(e.setMinimumEscape("x\uD83D\uDE00y", false));
        assertFalse(e.setMinimumEscape("\uDE00", true));
        assertFalse(e.setMinimumEscape("&\uD800", true));
    }
}
