/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import com.sun.tools.xjc.reader.Const;

/**
 * Forces a non-collapsing behavior to allow extension schemas
 * to perform element substitutions.
 *
 * See https://github.com/javaee/jaxb-v2/issues/289
 *
 * @author Kohsuke Kawaguchi
 * @since 2.1.1
 */
@XmlRootElement(name="substitutable",namespace= Const.XJC_EXTENSION_URI)
public final class BIXSubstitutable extends AbstractDeclarationImpl {
    public final QName getName() { return NAME; }

    /** Name of the conversion declaration. */
    public static final QName NAME = new QName(Const.XJC_EXTENSION_URI,"substitutable");
}
