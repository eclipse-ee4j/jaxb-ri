/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom;

/**
 * Facet for a simple type.
 * 
 * @author
 *  Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSFacet extends XSComponent
{
    /** Gets the name of the facet, such as "length". */
    String getName();
    
    /** Gets the value of the facet. */
    XmlString getValue();
    
    /** Returns true if this facet is "fixed". */
    boolean isFixed();
    
    
    // well-known facet name constants
    final static String FACET_LENGTH            = "length";
    final static String FACET_MINLENGTH         = "minLength";
    final static String FACET_MAXLENGTH         = "maxLength";
    final static String FACET_PATTERN           = "pattern";
    final static String FACET_ENUMERATION       = "enumeration";
    final static String FACET_TOTALDIGITS       = "totalDigits";
    final static String FACET_FRACTIONDIGITS    = "fractionDigits";
    final static String FACET_MININCLUSIVE      = "minInclusive";
    final static String FACET_MAXINCLUSIVE      = "maxInclusive";
    final static String FACET_MINEXCLUSIVE      = "minExclusive";
    final static String FACET_MAXEXCLUSIVE      = "maxExclusive";
    final static String FACET_WHITESPACE        = "whiteSpace";
}
