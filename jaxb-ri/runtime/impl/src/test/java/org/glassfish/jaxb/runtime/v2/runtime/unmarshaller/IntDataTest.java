/*
 * Copyright (c) 2025, 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntDataTest {

    @Test
    public void testLength() {
        IntData data = new IntData();
        data.reset(54321);
        assertEquals(5, data.length());
        data.reset(-54321);
        assertEquals(6, data.length());
        data.reset(987654321);
        assertEquals(9, data.length());
        data.reset(-987654321);
        assertEquals(10, data.length());
    }
}
