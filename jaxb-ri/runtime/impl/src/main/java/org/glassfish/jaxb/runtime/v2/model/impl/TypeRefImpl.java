/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.core.v2.model.core.NonElement;
import org.glassfish.jaxb.core.v2.model.core.PropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.TypeRef;

import javax.xml.namespace.QName;

/**
 * @author Kohsuke Kawaguchi
 */
class TypeRefImpl<TypeT,ClassDeclT> implements TypeRef<TypeT,ClassDeclT> {
    private final QName elementName;
    private final TypeT type;
    protected final ElementPropertyInfoImpl<TypeT,ClassDeclT,?,?> owner;
    private NonElement<TypeT,ClassDeclT> ref;
    private final boolean isNillable;
    private String defaultValue;

    public TypeRefImpl(ElementPropertyInfoImpl<TypeT, ClassDeclT, ?, ?> owner, QName elementName, TypeT type, boolean isNillable, String defaultValue) {
        this.owner = owner;
        this.elementName = elementName;
        this.type = type;
        this.isNillable = isNillable;
        this.defaultValue = defaultValue;
        assert owner!=null;
        assert elementName!=null;
        assert type!=null;
    }

    public NonElement<TypeT,ClassDeclT> getTarget() {
        if(ref==null)
            calcRef();
        return ref;
    }

    public QName getTagName() {
        return elementName;
    }

    public boolean isNillable() {
        return isNillable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    protected void link() {
        // if we have'nt computed the ref yet, do so now.
        calcRef();
    }

    private void calcRef() {
        // we can't do this eagerly because of a cyclic dependency
        ref = owner.parent.builder.getTypeInfo(type,owner);
        assert ref!=null;
    }

    public PropertyInfo<TypeT,ClassDeclT> getSource() {
        return owner;
    }
}
