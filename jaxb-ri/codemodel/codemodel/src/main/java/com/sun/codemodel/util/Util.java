/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * This class aims to provide some util methods used in codemodel and dependencies project.
 * <br><br>
 * For now, it allows user to escape some dangerous characters for javadoc generation processing
 */
public class Util {

    private static final List<AbstractMap.SimpleImmutableEntry<String, String>> ESCAPED_XML_JAVADOC = new ArrayList<>();
    static {
        ESCAPED_XML_JAVADOC.add(new AbstractMap.SimpleImmutableEntry<>("&", "&amp;"));
        ESCAPED_XML_JAVADOC.add(new AbstractMap.SimpleImmutableEntry<>("<", "&lt;"));
        ESCAPED_XML_JAVADOC.add(new AbstractMap.SimpleImmutableEntry<>(">", "&gt;"));
        ESCAPED_XML_JAVADOC.add(new AbstractMap.SimpleImmutableEntry<>("@", "&#064;"));
    }

    private Util() {}

    /**
     * Escapes the XML tags for Javadoc compatibility
     */
    public static String escapeXML(String s) {
        if (s == null) {
            return s;
        }
        for (AbstractMap.SimpleImmutableEntry<String, String> entry : ESCAPED_XML_JAVADOC) {
            int entryKeyLength = entry.getKey().length();
            int entryValueLength = entry.getValue().length();
            int idx = -1;
            while (true) {
                idx = s.indexOf(entry.getKey(), idx);
                if (idx < 0) {
                    break;
                }
                s = s.substring(0, idx) + entry.getValue() + s.substring(idx + entryKeyLength);
                idx += entryValueLength;
            }
        }
        return s;
    }
}
