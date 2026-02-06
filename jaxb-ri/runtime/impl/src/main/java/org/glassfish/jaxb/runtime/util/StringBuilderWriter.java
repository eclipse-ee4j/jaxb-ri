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
import java.io.Writer;

/**
 * A character stream that collects its output in a string builder, which can
 * then be used to construct a string.
 *
 * <p>
 * Closing a {@code StringBuilderWriter} has no effect. The methods in this class
 * can be called after the stream has been closed without generating an
 * {@code IOException}.
 *
 * <p>This class is an alternative of JDK {@code java.io.StringWriter}.
 * It is backed up by {@link StringBuilder} for better performance without the
 * need of synchronization of a {@link StringBuffer}.
 *
 * <p>Instances of {@code StringBuilderWriter} are not safe for
 * use by multiple threads. If such synchronization is required then it is
 * recommended that {@link java.io.StringWriter} be used.
 *
 * @author      Laurent Schoelens
 * @since       4.0.7
 */
public class StringBuilderWriter extends Writer {

    private final StringBuilder sb;

    /**
     * Create a new string-builder writer using the default initial string-builder size.
     */
    public StringBuilderWriter() {
        this(null);
    }

    /**
     * Create a new string-builder writer using the specified initial string-builder size.
     *
     * @param initialSize
     *        The number of {@code char} values that will fit into this StringBuilder
     *        before it is automatically expanded
     *
     * @throws IllegalArgumentException
     *         If {@code initialSize} is negative
     */
    public StringBuilderWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative buffer size");
        }
        this.sb = new StringBuilder(initialSize);
    }

    /**
     * Create a new string-builder writer using the specified string-builder.
     *
     * @param sb
     *        The string-builder to use. could be null and default-sized
     *        string-builder will be created
     */
    public StringBuilderWriter(StringBuilder sb) {
        this.sb = sb == null ? new StringBuilder() : sb;
    }

    /**
     * Return the string builder itself.
     *
     * @return StringBuilder holding the current string-builder value.
     */
    public StringBuilder getBuilder() {
        return sb;
    }

    /**
     * Writes a single character.
     *
     * @param c
     *         int specifying a character to be written
     *
     * @throws IOException
     *          If an I/O error occurs
     */
    @Override
    public void write(int c) throws IOException {
        sb.append(c);
    }

    /**
     * Writes an array of characters.
     *
     * @param cbuf
     *         Array of characters to be written
     *
     * @throws IOException
     *          If an I/O error occurs
     */
    @Override
    public void write(char[] cbuf) throws IOException {
        sb.append(cbuf);
    }

    /**
     * Writes a portion of an array of characters.
     *
     * @param cbuf
     *         Array of characters
     * @param off
     *         Offset from which to start writing characters
     * @param len
     *         Number of characters to write
     *
     * @throws IOException
     *          If an I/O error occurs
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        sb.append(cbuf, off, len);
    }

    /**
     * Writes a string.
     *
     * @param str
     *         String to be written
     *
     * @throws IOException
     *          If an I/O error occurs
     */
    @Override
    public void write(String str) throws IOException {
        sb.append(str);
    }

    /**
     * Writes a portion of a string.
     *
     * @param str
     *         A String
     * @param off
     *         Offset from which to start writing characters
     * @param len
     *         Number of characters to write
     *
     * @throws IOException
     *          If an I/O error occurs
     */
    @Override
    public void write(String str, int off, int len) throws IOException {
        sb.append(str, off, len);
    }

    /**
     * Appends the specified character sequence to this writer.
     *
     * @param csq
     *         The character sequence to append.  If {@code csq} is
     *         {@code null}, then the four characters {@code "null"} are
     *         appended to this writer.
     *
     * @return This writer
     * @throws IOException
     *          If an I/O error occurs
     */
    @Override
    public Writer append(CharSequence csq) throws IOException {
        sb.append(csq);
        return this;
    }

    /**
     * Return the buffer's current value as a string.
     */
    public String toString() {
        return sb.toString();
    }

    /**
     * Flush the stream.
     *
     * <p> The {@code flush} method of {@code StringBuilderWriter} does nothing.
     */
    @Override
    public void flush() {
        // no-op
    }

    /**
     * Closing a {@code StringBuilderWriter} has no effect. The methods in this
     * class can be called after the stream has been closed without generating
     * an {@code IOException}.
     */
    @Override
    public void close() {
        // no-op
    }
}
