/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.marshaller;

/**
 * How the marshaller should treat characters that are legal in a Java
 * {@link String} but illegal in serialized XML 1.0.
 *
 * <p>
 * The XML 1.0 {@code Char} production (see
 * <a href="https://www.w3.org/TR/xml/#charsets">XML 1.0 &sect;2.2</a>) forbids
 * most C0 control characters (every code point below {@code 0x20} other than
 * {@code \t}, {@code \n} and {@code \r}), as well as {@code U+FFFE} and
 * {@code U+FFFF}. Crucially, XML 1.0 does not allow these characters even as
 * numeric character references, so there is no lossless escaping that keeps the
 * output parseable. Historically the JAXB RI marshalled such characters
 * verbatim, producing XML that fails to parse on the way back in (see
 * <a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/614">JAXB-614</a>).
 *
 * <p>
 * The behaviour is selected through the
 * {@value #PROPERTY_NAME} marshaller property or the system property of the
 * same name. The default is {@link #WRITE}, which preserves the historical
 * (non-compliant) behaviour for backward compatibility.
 */
public enum InvalidXmlCharacterPolicy {

    /**
     * Write the character to the output unchanged. This reproduces the
     * historical behaviour and can yield XML that is not well-formed.
     */
    WRITE,

    /**
     * Silently drop the offending character from the output. The resulting XML
     * is well-formed but the character is lost.
     */
    STRIP,

    /**
     * Replace the offending character with the Unicode replacement character
     * ({@code U+FFFD}), which is itself a legal XML 1.0 character. The resulting
     * XML is well-formed and round-trips.
     */
    REPLACE;

    /**
     * Name of the marshaller property and system property that selects the
     * policy. Accepted values are the (case-insensitive) enum constant names.
     */
    public static final String PROPERTY_NAME = "org.glassfish.jaxb.invalidXmlCharacterPolicy";

    /**
     * The character substituted for an illegal one under {@link #REPLACE}.
     */
    public static final char REPLACEMENT_CHAR = '\uFFFD';

    /**
     * Returns whether the given character is illegal in serialized XML 1.0
     * character data.
     */
    public static boolean isInvalid(char c) {
        if (c < 0x20) {
            return c != '\t' && c != '\n' && c != '\r';
        }
        return c == '\uFFFE' || c == '\uFFFF';
    }

    /**
     * Parses a policy from its name, case-insensitively. Returns {@link #WRITE}
     * for a {@code null}, blank, or unrecognized value so that a stray
     * configuration value can never make marshalling fail.
     */
    public static InvalidXmlCharacterPolicy parse(String value) {
        if (value == null) {
            return WRITE;
        }
        for (InvalidXmlCharacterPolicy policy : values()) {
            if (policy.name().equalsIgnoreCase(value.trim())) {
                return policy;
            }
        }
        return WRITE;
    }

    /**
     * Resolves the default policy from the {@value #PROPERTY_NAME} system
     * property, falling back to {@link #WRITE} when unset.
     */
    public static InvalidXmlCharacterPolicy resolveDefault() {
        return parse(System.getProperty(PROPERTY_NAME));
    }
}
