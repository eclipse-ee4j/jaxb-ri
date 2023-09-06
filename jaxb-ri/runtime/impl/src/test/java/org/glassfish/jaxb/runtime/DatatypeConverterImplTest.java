/*
 * Copyright (c) 2023, 2023 Oracle and/or its affiliates. All rights reserved.
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
    public void testParseBoolean() {
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean(null));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean(""));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("11"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("1A"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("non"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("fals"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("falses"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("false s"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("falst"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("tru"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("trux"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("truu"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("truxx"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("truth"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("truelle"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("truec"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("true c"));
        Assert.assertEquals(null, DatatypeConverterImpl._parseBoolean("oui"));

        Assert.assertEquals(false, DatatypeConverterImpl._parseBoolean("0"));
        Assert.assertEquals(false, DatatypeConverterImpl._parseBoolean(" 0"));
        Assert.assertEquals(false, DatatypeConverterImpl._parseBoolean(" 0 "));
        Assert.assertEquals(false, DatatypeConverterImpl._parseBoolean("0 "));
        Assert.assertEquals(true, DatatypeConverterImpl._parseBoolean("1"));
        Assert.assertEquals(true, DatatypeConverterImpl._parseBoolean(" 1"));
        Assert.assertEquals(true, DatatypeConverterImpl._parseBoolean(" 1 "));
        Assert.assertEquals(true, DatatypeConverterImpl._parseBoolean("1 "));
        Assert.assertEquals(false, DatatypeConverterImpl._parseBoolean("false"));
        Assert.assertEquals(false, DatatypeConverterImpl._parseBoolean(" false"));
        Assert.assertEquals(false, DatatypeConverterImpl._parseBoolean("false "));
        Assert.assertEquals(false, DatatypeConverterImpl._parseBoolean(" false "));
        Assert.assertEquals(true, DatatypeConverterImpl._parseBoolean("true"));
        Assert.assertEquals(true, DatatypeConverterImpl._parseBoolean(" true"));
        Assert.assertEquals(true, DatatypeConverterImpl._parseBoolean("true "));
        Assert.assertEquals(true, DatatypeConverterImpl._parseBoolean(" true "));
    }
}
