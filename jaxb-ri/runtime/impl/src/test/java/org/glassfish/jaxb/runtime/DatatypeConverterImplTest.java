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

import org.junit.Assert;
import org.junit.Test;

public class DatatypeConverterImplTest {

    @Test
    @SuppressWarnings({"deprecation"})
    public void testParseBoolean() {
        Assert.assertNull(DatatypeConverterImpl._parseBoolean(null));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean(""));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("11"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("1A"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("non"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("fals"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("falses"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("false s"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("falst"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("tru"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("trux"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("truu"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("truxx"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("truth"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("truelle"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("truec"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("true c"));
        Assert.assertNull(DatatypeConverterImpl._parseBoolean("oui"));

        Assert.assertFalse(DatatypeConverterImpl._parseBoolean("0"));
        Assert.assertFalse(DatatypeConverterImpl._parseBoolean(" 0"));
        Assert.assertFalse(DatatypeConverterImpl._parseBoolean(" 0 "));
        Assert.assertFalse(DatatypeConverterImpl._parseBoolean("0 "));
        Assert.assertTrue(DatatypeConverterImpl._parseBoolean("1"));
        Assert.assertTrue(DatatypeConverterImpl._parseBoolean(" 1"));
        Assert.assertTrue(DatatypeConverterImpl._parseBoolean(" 1 "));
        Assert.assertTrue(DatatypeConverterImpl._parseBoolean("1 "));
        Assert.assertFalse(DatatypeConverterImpl._parseBoolean("false"));
        Assert.assertFalse(DatatypeConverterImpl._parseBoolean(" false"));
        Assert.assertFalse(DatatypeConverterImpl._parseBoolean("false "));
        Assert.assertFalse(DatatypeConverterImpl._parseBoolean(" false "));
        Assert.assertTrue(DatatypeConverterImpl._parseBoolean("true"));
        Assert.assertTrue(DatatypeConverterImpl._parseBoolean(" true"));
        Assert.assertTrue(DatatypeConverterImpl._parseBoolean("true "));
        Assert.assertTrue(DatatypeConverterImpl._parseBoolean(" true "));
    }
}
