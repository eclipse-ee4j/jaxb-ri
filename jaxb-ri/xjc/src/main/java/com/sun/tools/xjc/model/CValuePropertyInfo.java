/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import javax.xml.namespace.QName;

import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.core.v2.model.core.ValuePropertyInfo;
import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * {@link ValuePropertyInfo} implementation for XJC.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class CValuePropertyInfo extends CSingleTypePropertyInfo implements ValuePropertyInfo<NType,NClass> {
    public CValuePropertyInfo(String name, XSComponent source, CCustomizations customizations, Locator locator, TypeUse type, QName typeName) {
        super(name, type, typeName, source, customizations, locator);
    }

    public final PropertyKind kind() {
        return  PropertyKind.VALUE;
    }

    public <V> V accept(CPropertyVisitor<V> visitor) {
        return visitor.onValue(this);
    }

    @Override
    public <R, P> R accept(CPropertyVisitor2<R, P> visitor, P p) {
        return visitor.visit(this, p);
    }
}
