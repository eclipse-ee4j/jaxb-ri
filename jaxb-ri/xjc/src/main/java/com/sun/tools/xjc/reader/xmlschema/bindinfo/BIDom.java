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
import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import com.sun.tools.xjc.reader.Const;

/**
 * DOM customization.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
@XmlRootElement(name="dom")
public class BIDom extends AbstractDeclarationImpl {

    // unsupported yet
    @XmlAttribute
    String type;

    public final QName getName() { return NAME; }
    
    /** Name of the conversion declaration. */
    public static final QName NAME = new QName(Const.JAXB_NSURI,"dom");
}
