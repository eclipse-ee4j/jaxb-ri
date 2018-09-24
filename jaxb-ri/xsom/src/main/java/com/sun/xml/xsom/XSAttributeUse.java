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
 * Attribute use.
 * 
 * @author
 *  Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSAttributeUse extends XSComponent
{
    boolean isRequired();
    XSAttributeDecl getDecl();

    /**
     * Gets the default value of this attribute use, if one is specified.
     * 
     * Note that if a default value is specified in the attribute
     * declaration, this method returns that value.
     */
    XmlString getDefaultValue();

    /**
     * Gets the fixed value of this attribute use, if one is specified.
     * 
     * Note that if a fixed value is specified in the attribute
     * declaration, this method returns that value.
     */
    XmlString getFixedValue();
}
