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

import java.io.IOException;
import java.io.Writer;

/**
 * A {@link CharacterEscapeHandler} decorator that removes or replaces
 * characters illegal in serialized XML 1.0 before delegating to another
 * handler.
 *
 * <p>
 * The JAXB RI historically marshalled C0 control characters (and a couple of
 * other code points forbidden by the XML 1.0 {@code Char} production) verbatim,
 * producing output that cannot be parsed back in &mdash; see
 * <a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/614">JAXB-614</a>.
 * Because XML 1.0 does not permit these characters even as numeric character
 * references, the only way to keep the output well-formed is to drop them
 * ({@link InvalidXmlCharacterPolicy#STRIP}) or substitute a legal character
 * ({@link InvalidXmlCharacterPolicy#REPLACE}).
 *
 * <p>
 * This handler only filters the offending characters; it leaves all normal XML
 * escaping (of {@code &}, {@code <}, {@code >}, quotes, CR/LF, etc.) to the
 * wrapped handler so behaviour is otherwise unchanged.
 */
public class InvalidXmlCharacterEscapeHandler implements CharacterEscapeHandler {

    private final CharacterEscapeHandler core;
    private final InvalidXmlCharacterPolicy policy;

    /**
     * @param core   the handler that performs the normal XML escaping; must not
     *               be {@code null}
     * @param policy how to treat illegal XML 1.0 characters; must not be
     *               {@code null} and is expected to be
     *               {@link InvalidXmlCharacterPolicy#STRIP} or
     *               {@link InvalidXmlCharacterPolicy#REPLACE}
     */
    public InvalidXmlCharacterEscapeHandler(CharacterEscapeHandler core, InvalidXmlCharacterPolicy policy) {
        this.core = core;
        this.policy = policy;
    }

    /**
     * Wraps {@code core} only when {@code policy} actually changes behaviour.
     * For {@link InvalidXmlCharacterPolicy#WRITE} (or a {@code null} policy) the
     * original handler is returned unchanged, so the common path pays nothing.
     */
    public static CharacterEscapeHandler wrap(CharacterEscapeHandler core, InvalidXmlCharacterPolicy policy) {
        if (policy == null || policy == InvalidXmlCharacterPolicy.WRITE) {
            return core;
        }
        return new InvalidXmlCharacterEscapeHandler(core, policy);
    }

    @Override
    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        int limit = start + length;

        // Fast path: scan for the first illegal character. The overwhelming
        // majority of payloads contain none, in which case we delegate the
        // original array range directly and allocate nothing.
        int firstBad = start;
        while (firstBad < limit && !InvalidXmlCharacterPolicy.isInvalid(ch[firstBad])) {
            firstBad++;
        }
        if (firstBad == limit) {
            core.escape(ch, start, length, isAttVal, out);
            return;
        }

        // Slow path: build a filtered copy, preserving everything legal.
        char[] filtered = new char[length];
        int len = 0;
        for (int i = start; i < limit; i++) {
            char c = ch[i];
            if (InvalidXmlCharacterPolicy.isInvalid(c)) {
                if (policy == InvalidXmlCharacterPolicy.REPLACE) {
                    filtered[len++] = InvalidXmlCharacterPolicy.REPLACEMENT_CHAR;
                }
                // STRIP: drop the character entirely.
            } else {
                filtered[len++] = c;
            }
        }
        core.escape(filtered, 0, len, isAttVal, out);
    }
}
