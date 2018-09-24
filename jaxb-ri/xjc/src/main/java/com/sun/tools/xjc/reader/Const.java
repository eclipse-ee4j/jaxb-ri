/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader;

import com.sun.xml.bind.v2.WellKnownNamespace;


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
    
    /** JAXB customization URI. */
    public final static String JAXB_NSURI =
        "http://java.sun.com/xml/ns/jaxb";
    
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
}

