/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.util;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringBuilderWriterTest {

    @Test
    void testDefaultConstructor () {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            assertNotNull (writer.getBuilder ());
            assertEquals ("", writer.toString ());
        }
    }

    @Test
    void testConstructorWithInitialSize () {
        try (final StringBuilderWriter writer = new StringBuilderWriter (64)) {
            assertNotNull (writer.getBuilder ());
            assertEquals ("", writer.toString ());
        }
    }

    @Test
    void testConstructorWithNegativeSize () {
        assertThrows (IllegalArgumentException.class, () -> new StringBuilderWriter (-1));
    }

    @Test
    void testConstructorWithStringBuilder () throws IOException {
      final StringBuilder sb = new StringBuilder ("existing");
        try (final StringBuilderWriter writer = new StringBuilderWriter (sb)) {
            assertSame (sb, writer.getBuilder ());
            assertEquals ("existing", writer.toString ());
            writer.write ("More");
            assertEquals ("existingMore", writer.toString ());
            assertEquals ("existingMore", sb.toString ());
        }
    }

    @Test
    void testConstructorWithNullStringBuilder () {
        try (final StringBuilderWriter writer = new StringBuilderWriter (null)) {
            assertNotNull (writer.getBuilder ());
            assertEquals ("", writer.toString ());
        }
    }

    @Test
    void testWriteChar () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.write ('\t');
            writer.write ('\n');
            assertEquals ("\t\n", writer.toString ());
        }
    }

    @Test
    void testWriteIntCastsToChar () throws IOException {
        // Verify that write(int) treats the value as a character code point,
        // not as a numeric string representation
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.write (65);  // 'A'
            writer.write (0x20AC);  // Euro sign
            assertEquals ("A\u20AC", writer.toString ());
        }
    }

    @Test
    void testWriteCharArray () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.write (new char [] { 'H', 'e', 'l', 'l', 'o' }, 0, 5);
            assertEquals ("Hello", writer.toString ());
        }
    }

    @Test
    void testWriteCharArrayWithOffset () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.write (new char [] { 'H', 'e', 'l', 'l', 'o' }, 1, 3);
            assertEquals ("ell", writer.toString ());
        }
    }

    @Test
    void testWriteString () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.write ("Hello");
            assertEquals ("Hello", writer.toString ());
        }
    }

    @Test
    void testWriteStringWithOffset () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.write ("Hello World", 6, 5);
            assertEquals ("World", writer.toString ());
        }
    }

    @Test
    void testAppendChar () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.append ('\t');
            writer.append ('\n');
            assertEquals ("\t\n", writer.toString ());
        }
    }

    @Test
    void testAppendCharSequence () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.append ("Hello");
            assertEquals ("Hello", writer.toString ());
        }
    }

    @Test
    void testAppendCharSequenceWithRange () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.append ("Hello World", 6, 11);
            assertEquals ("World", writer.toString ());
        }
    }

    @Test
    void testFlushAndCloseAreNoOps () throws IOException {
        @SuppressWarnings ("resource")
        final StringBuilderWriter writer = new StringBuilderWriter ();
        writer.write ("before");
        writer.flush ();
        writer.write ("after");
        writer.close ();
        writer.write ("afterClose");
        assertEquals ("beforeafterafterClose", writer.toString ());
    }

    @Test
    void testToString () throws IOException {
        try (final StringBuilderWriter writer = new StringBuilderWriter ()) {
            writer.write ("test");
            assertEquals ("test", writer.toString ());
            assertEquals (writer.getBuilder ().toString (), writer.toString ());
        }
    }
}
