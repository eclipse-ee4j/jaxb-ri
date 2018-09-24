/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model.prop;

import com.sun.codemodel.JType;

import javax.xml.namespace.QName;

/**
 * @author Kohsuke Kawaguchi
 */
public final class AttributeProp extends XmlItemProp {
    public AttributeProp(QName name, JType valueType) {
        super(name, valueType);
    }
}
