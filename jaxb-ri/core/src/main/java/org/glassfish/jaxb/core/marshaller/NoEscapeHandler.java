/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
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
 * Performs no character escaping.
 *
 * @author
 *     Roman Grigoriadi (roman.grigoriadi@oracle.com)
 */
public class NoEscapeHandler implements CharacterEscapeHandler {

    public static final NoEscapeHandler theInstance = new NoEscapeHandler();

    @Override
    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        out.write(ch, start, length);
    }
}
