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
    void testAppendChar() throws IOException {
        try (StringBuilderWriter writer = new StringBuilderWriter()) {
            writer.append('\t');
            writer.append('\n');
            assertEquals("\t\n", writer.toString());
        }
    }

    @Test
    void testWriteChar() throws IOException {
        try (StringBuilderWriter writer = new StringBuilderWriter()) {
            writer.write('\t');
            writer.write('\n');
            assertEquals("\t\n", writer.toString());
        }
    }
}