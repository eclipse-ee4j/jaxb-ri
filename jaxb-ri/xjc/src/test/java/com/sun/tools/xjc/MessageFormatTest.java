/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.sun.tools.xjc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Test for MessageFormat errors.
 */
public class MessageFormatTest {

    private static Locale initialLocale;
    private Logger logger = Logger.getLogger(MessageFormatTest.class.getName());

    @BeforeAll
    static void setUp() {
        initialLocale = Locale.getDefault();
    }

    public static Stream<Arguments> langs() {
        return Stream.of(
                Arguments.of("de", ""),
                Arguments.of("fr", ""),
                Arguments.of("en", ""),
                Arguments.of("es", ""),
                Arguments.of("it", ""),
                Arguments.of("ja", ""),
                Arguments.of("ko", ""),
                Arguments.of("zh", "CN"),
                Arguments.of("zh", "TW")
        );
    }

    @ParameterizedTest
    @MethodSource("langs")
    public void testFormatNotABindingFile(String lang, String country) {
        Locale.setDefault(new Locale(lang, country));
        String message = Messages.format(Messages.ERR_NOT_A_BINDING_FILE, "aNamespace", "aLocalName");
        logger.info(Messages.ERR_NOT_A_BINDING_FILE + " produces for locale " + Locale.getDefault() + " " + message);
        Assertions.assertNotNull(message, "Message should not be null");
    }

    @AfterAll
    static void tearDown() {
        Locale.setDefault(initialLocale);
    }
}
