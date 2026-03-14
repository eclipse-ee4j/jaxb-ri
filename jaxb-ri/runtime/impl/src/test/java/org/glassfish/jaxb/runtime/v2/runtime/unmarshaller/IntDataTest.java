/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.junit.Test;
import org.junit.Assert;

public class IntDataTest {

    @Test
    public void testLength() {
        IntData data = new IntData();
        data.reset(54321);
        Assert.assertEquals(5, data.length());
        data.reset(-54321);
        Assert.assertEquals(6, data.length());
        data.reset(987654321);
        Assert.assertEquals(9, data.length());
        data.reset(-987654321);
        Assert.assertEquals(10, data.length());
    }
}
