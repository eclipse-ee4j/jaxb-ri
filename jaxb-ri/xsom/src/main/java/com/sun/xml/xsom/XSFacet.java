/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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
    String FACET_LENGTH            = "length";
    String FACET_MINLENGTH         = "minLength";
    String FACET_MAXLENGTH         = "maxLength";
    String FACET_PATTERN           = "pattern";
    String FACET_ENUMERATION       = "enumeration";
    String FACET_TOTALDIGITS       = "totalDigits";
    String FACET_FRACTIONDIGITS    = "fractionDigits";
    String FACET_MININCLUSIVE      = "minInclusive";
    String FACET_MAXINCLUSIVE      = "maxInclusive";
    String FACET_MINEXCLUSIVE      = "minExclusive";
    String FACET_MAXEXCLUSIVE      = "maxExclusive";
    String FACET_WHITESPACE        = "whiteSpace";
}
