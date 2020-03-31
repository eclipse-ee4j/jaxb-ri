/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import com.sun.tools.xjc.reader.Const;

/**
 * Enumeration member customization.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
@XmlRootElement(name="typesafeEnumMember")
public class BIEnumMember extends AbstractDeclarationImpl {
    protected BIEnumMember() {
        name = null;
        javadoc = null;
    }

    /** Gets the specified class name, or null if not specified. */
    // regardless of the BIGlobalBinding.isJavaNamingConventionEnabled flag,
    // we don't modify the constant name.
    @XmlAttribute
    public final String name;

    /**
     * Gets the javadoc comment specified in the customization.
     * Can be null if none is specified.
     */
    @XmlElement
    public final String javadoc;

    public QName getName() { return NAME; }

    /** Name of this declaration. */
    public static final QName NAME = new QName(
        Const.JAXB_NSURI, "typesafeEnumMember" );
}
