/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader;

import org.glassfish.jaxb.core.v2.WellKnownNamespace;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Useful constant values.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Const {
    
    /** XML namespace URI. */
    public final static String XMLNS_URI =
        "http://www.w3.org/2000/xmlns/";

    /** JAXB customization URI old namespace. */
    public final static String JAVAX_JAXB_NSURI =
            "https://java.sun.com/xml/ns/jaxb";

    /** JAXB customization URI old namespace. */
    public final static String JAKARTA_JAXB_NSURI =
            "https://jakarta.ee/xml/ns/jaxb";

    /** XJC vendor extension namespace URI. */
    public final static String XJC_EXTENSION_URI =
        "http://java.sun.com/xml/ns/jaxb/xjc";
    
    /** RELAX NG namespace URI. */
    public static final String RELAXNG_URI =
        "http://relaxng.org/ns/structure/1.0";

    /** URI to represent DTD. */
    public static final String DTD = "DTD";

    /**
     * Attribute name of the expected media type.
     *
     * @see WellKnownNamespace#XML_MIME_URI
     * @see <a href="http://www.w3.org/TR/xml-media-types/">http://www.w3.org/TR/xml-media-types/</a>
     */
    public static final String EXPECTED_CONTENT_TYPES = "expectedContentTypes";

    public static void useOldNameSpace() {
        JAXB_NS_URI.add(JAVAX_JAXB_NSURI);
    }

    /** Set of valid Jaxb NS URI used during parsing */
    public static final Set<String> JAXB_NS_URI = new HashSet<>(Collections.singleton(JAKARTA_JAXB_NSURI));
}

