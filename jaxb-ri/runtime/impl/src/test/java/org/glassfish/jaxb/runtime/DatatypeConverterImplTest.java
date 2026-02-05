/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatatypeConverterImplTest {

    @Test
    @SuppressWarnings({"deprecation"})
    public void testParseBoolean() {
        assertNull(DatatypeConverterImpl._parseBoolean(null));
        assertNull(DatatypeConverterImpl._parseBoolean(""));
        assertNull(DatatypeConverterImpl._parseBoolean("11"));
        assertNull(DatatypeConverterImpl._parseBoolean("1A"));
        assertNull(DatatypeConverterImpl._parseBoolean("non"));
        assertNull(DatatypeConverterImpl._parseBoolean("fals"));
        assertNull(DatatypeConverterImpl._parseBoolean("falses"));
        assertNull(DatatypeConverterImpl._parseBoolean("false s"));
        assertNull(DatatypeConverterImpl._parseBoolean("falst"));
        assertNull(DatatypeConverterImpl._parseBoolean("tru"));
        assertNull(DatatypeConverterImpl._parseBoolean("trux"));
        assertNull(DatatypeConverterImpl._parseBoolean("truu"));
        assertNull(DatatypeConverterImpl._parseBoolean("truxx"));
        assertNull(DatatypeConverterImpl._parseBoolean("truth"));
        assertNull(DatatypeConverterImpl._parseBoolean("truelle"));
        assertNull(DatatypeConverterImpl._parseBoolean("truec"));
        assertNull(DatatypeConverterImpl._parseBoolean("true c"));
        assertNull(DatatypeConverterImpl._parseBoolean("oui"));

        assertFalse(DatatypeConverterImpl._parseBoolean("0"));
        assertFalse(DatatypeConverterImpl._parseBoolean(" 0"));
        assertFalse(DatatypeConverterImpl._parseBoolean(" 0 "));
        assertFalse(DatatypeConverterImpl._parseBoolean("0 "));
        assertTrue(DatatypeConverterImpl._parseBoolean("1"));
        assertTrue(DatatypeConverterImpl._parseBoolean(" 1"));
        assertTrue(DatatypeConverterImpl._parseBoolean(" 1 "));
        assertTrue(DatatypeConverterImpl._parseBoolean("1 "));
        assertFalse(DatatypeConverterImpl._parseBoolean("false"));
        assertFalse(DatatypeConverterImpl._parseBoolean(" false"));
        assertFalse(DatatypeConverterImpl._parseBoolean("false "));
        assertFalse(DatatypeConverterImpl._parseBoolean(" false "));
        assertTrue(DatatypeConverterImpl._parseBoolean("true"));
        assertTrue(DatatypeConverterImpl._parseBoolean(" true"));
        assertTrue(DatatypeConverterImpl._parseBoolean("true "));
        assertTrue(DatatypeConverterImpl._parseBoolean(" true "));
    }
}
