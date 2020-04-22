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

import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeElementPropertyInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeInfo;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
class RuntimeElementPropertyInfoImpl extends ElementPropertyInfoImpl<Type,Class,Field,Method>
    implements RuntimeElementPropertyInfo {

    private final Accessor acc;

    RuntimeElementPropertyInfoImpl(RuntimeClassInfoImpl classInfo, PropertySeed<Type,Class,Field,Method> seed) {
        super(classInfo, seed);
        Accessor rawAcc = ((RuntimeClassInfoImpl.RuntimePropertySeed)seed).getAccessor();
        if(getAdapter()!=null && !isCollection())
            // adapter for a single-value property is handled by accessor.
            // adapter for a collection property is handled by lister.
            rawAcc = rawAcc.adapt(getAdapter());
        this.acc = rawAcc;
    }

    public Accessor getAccessor() {
        return acc;
    }

    public boolean elementOnlyContent() {
        return true;
    }

    public List<? extends RuntimeTypeInfo> ref() {
        return (List<? extends RuntimeTypeInfo>)super.ref();
    }

    @Override
    protected RuntimeTypeRefImpl createTypeRef(QName name, Type type, boolean isNillable, String defaultValue) {
        return new RuntimeTypeRefImpl(this,name,type,isNillable,defaultValue);
    }

    public List<RuntimeTypeRefImpl> getTypes() {
        return (List<RuntimeTypeRefImpl>)super.getTypes();
    }
}
