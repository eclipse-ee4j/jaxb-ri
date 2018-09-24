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
 * Common implementation between elements and attributes.
 *
 * @author Kohsuke Kawaguchi
 */
abstract class XmlItemProp extends Prop {
    private final QName name;
    private final JType type;

    public XmlItemProp(QName name, JType valueType) {
        this.name = name;
        this.type = valueType;
    }

    @Override
    public final boolean equals(Object o) {
        if ((o == null) || (this.getClass()!=o.getClass())) {
            return false;
        }

        XmlItemProp that = (XmlItemProp)o;

        return this.name.equals(that.name)
            && this.type.equals(that.type);
    }

    @Override
    public final int hashCode() {
        return name.hashCode()*29 + type.hashCode();
    }
}
