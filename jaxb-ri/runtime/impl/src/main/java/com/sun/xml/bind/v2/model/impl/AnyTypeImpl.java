/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.impl;

import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlIDREF;

import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.core.TypeInfo;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.annotation.Locatable;

/**
 * {@link TypeInfo} implementation for {@code xs:anyType}.
 *
 * @author Kohsuke Kawaguchi
 */
class AnyTypeImpl<T,C> implements NonElement<T,C> {

    private final T type;
    private final Navigator<T,C,?,?> nav;

    public AnyTypeImpl(Navigator<T,C,?,?> nav) {
        this.type = nav.ref(Object.class);
        this.nav = nav;
    }

    public QName getTypeName() {
        return ANYTYPE_NAME;
    }

    public T getType() {
        return type;
    }

    public Locatable getUpstream() {
        return null;
    }

    public boolean isSimpleType() {
        return false;
    }

    public Location getLocation() {
        return nav.getClassLocation(nav.asDecl(Object.class));
    }

    /**
     * xs:anyType can be referenced from {@link XmlIDREF}.
     *
     * @deprecated
     *      why are you calling a method whose return value is always known?
     */
    public final boolean canBeReferencedByIDREF() {
        return true;
    }

}
