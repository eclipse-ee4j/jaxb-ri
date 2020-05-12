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

import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.PropertyInfo;
import org.glassfish.jaxb.core.v2.runtime.Location;

import java.lang.annotation.Annotation;

/**
 * {@link PropertyInfo} implementation backed by a field.
 */
class FieldPropertySeed<TypeT,ClassDeclT,FieldT,MethodT> implements
        PropertySeed<TypeT,ClassDeclT,FieldT,MethodT> {

    protected final FieldT field;
    private ClassInfoImpl<TypeT,ClassDeclT,FieldT,MethodT> parent;

    FieldPropertySeed(ClassInfoImpl<TypeT,ClassDeclT,FieldT,MethodT> classInfo, FieldT field) {
        this.parent = classInfo;
        this.field = field;
    }

    public <A extends Annotation> A readAnnotation(Class<A> a) {
        return parent.reader().getFieldAnnotation(a, field,this);
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return parent.reader().hasFieldAnnotation(annotationType,field);
    }

    public String getName() {
        // according to the spec team, the BeanIntrospector.decapitalize does not apply
        // to the fields. Don't call Introspector.decapitalize
        return parent.nav().getFieldName(field);
    }

    public TypeT getRawType() {
        return parent.nav().getFieldType(field);
    }

    /**
     * Use the enclosing class as the upsream {@link Location}.
     */
    public Locatable getUpstream() {
        return parent;
    }

    public Location getLocation() {
        return parent.nav().getFieldLocation(field);
    }
}
