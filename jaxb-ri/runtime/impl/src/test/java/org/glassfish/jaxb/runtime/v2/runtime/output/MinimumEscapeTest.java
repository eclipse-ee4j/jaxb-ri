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
import java.nio.charset.StandardCharsets;
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
        assertEquals("a&#13;&#10;&quot;", new String(e.buf, 0, e.len, StandardCharsets.UTF_8));
        assertTrue(e.setMinimumEscape("a\r\n\"\t", false));
        assertEquals("a&#13;\n\"\t", new String(e.buf, 0, e.len, StandardCharsets.UTF_8));
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

    /**
     * Checks {@code exceedsMinimumEscapeCapacity} itself, not that {@code setMinimumEscape}
     * calls it: a test of that would need a text of more than 357M characters.
     */
    @Test
    public void overflowGuard() {
        int attMax = (Integer.MAX_VALUE - 1) / 6;
        int textMax = (Integer.MAX_VALUE - 1) / 5;
        assertFalse(Encoded.exceedsMinimumEscapeCapacity(0, true));
        assertFalse(Encoded.exceedsMinimumEscapeCapacity(0, false));
        assertFalse(Encoded.exceedsMinimumEscapeCapacity(attMax, true));
        assertTrue(Encoded.exceedsMinimumEscapeCapacity(attMax + 1, true));
        assertFalse(Encoded.exceedsMinimumEscapeCapacity(textMax, false));
        assertTrue(Encoded.exceedsMinimumEscapeCapacity(textMax + 1, false));
        // the largest accepted sizes, as computed by setMinimumEscape, still fit in an int
        assertTrue(attMax * 6L + 1 <= Integer.MAX_VALUE);
        assertTrue(textMax * 5L + 1 <= Integer.MAX_VALUE);
        assertTrue((attMax + 1) * 6L + 1 > Integer.MAX_VALUE);
        assertTrue((textMax + 1) * 5L + 1 > Integer.MAX_VALUE);
        // beyond 357M chars an attribute value must not be sized with an overflowing int
        assertTrue(Encoded.exceedsMinimumEscapeCapacity(Integer.MAX_VALUE, true));
        assertTrue(Encoded.exceedsMinimumEscapeCapacity(400_000_000, true));
        assertFalse(Encoded.exceedsMinimumEscapeCapacity(400_000_000, false));
    }

    /**
     * Repeated characters on a fresh {@link Encoded}, so that the buffer is exactly as large as
     * {@code setMinimumEscape} sized it: a multiplier that is too small throws
     * {@link ArrayIndexOutOfBoundsException}.
     */
    @Test
    public void worstCaseBytesPerCharacter() {
        int n = 16;
        assertWorstCase("\"".repeat(n), true, 6 * n);
        assertWorstCase("\n".repeat(n), true, 5 * n);
        assertWorstCase("&".repeat(n), true, 5 * n);
        assertWorstCase("&".repeat(n), false, 5 * n);
        assertWorstCase("\r".repeat(n), false, 5 * n);
        assertWorstCase("\u20AC".repeat(n), false, 3 * n);
        int max = 0;
        for (char c = 0; c < 0x800; c++) {
            for (boolean attribute : new boolean[] {false, true}) {
                Encoded e = new Encoded();
                assertTrue(e.setMinimumEscape(String.valueOf(c), attribute));
                max = Math.max(max, e.len);
            }
        }
        assertEquals(6, max);
    }

    private static void assertWorstCase(String text, boolean attribute, int expectedLength) {
        Encoded e = new Encoded();
        assertTrue(e.setMinimumEscape(text, attribute));
        assertEquals(expectedLength, e.len);
    }
}
