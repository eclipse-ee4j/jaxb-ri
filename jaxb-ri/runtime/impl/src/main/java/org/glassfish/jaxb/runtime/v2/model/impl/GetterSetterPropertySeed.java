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

import java.beans.Introspector;
import java.lang.annotation.Annotation;

/**
 * {@link PropertyInfo} implementation backed by a getter and a setter.
 *
 * We allow the getter or setter to be null, in which case the bean
 * can only participate in unmarshalling (or marshalling)
 */
class GetterSetterPropertySeed<TypeT,ClassDeclT,FieldT,MethodT> implements
        PropertySeed<TypeT,ClassDeclT,FieldT,MethodT> {

    protected final MethodT getter;
    protected final MethodT setter;
    private ClassInfoImpl<TypeT,ClassDeclT,FieldT,MethodT> parent;

    GetterSetterPropertySeed(ClassInfoImpl<TypeT,ClassDeclT,FieldT,MethodT> parent, MethodT getter, MethodT setter) {
        this.parent = parent;
        this.getter = getter;
        this.setter = setter;

        if(getter==null && setter==null)
            throw new IllegalArgumentException();
    }

    public TypeT getRawType() {
        if(getter!=null)
            return parent.nav().getReturnType(getter);
        else
            return parent.nav().getMethodParameters(setter)[0];
    }

    public <A extends Annotation> A readAnnotation(Class<A> annotation) {
        return parent.reader().getMethodAnnotation(annotation, getter,setter,this);
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return parent.reader().hasMethodAnnotation(annotationType,getName(),getter,setter,this);
    }

    public String getName() {
        if(getter!=null)
            return getName(getter);
        else
            return getName(setter);
    }

    private String getName(MethodT m) {
        String seed = parent.nav().getMethodName(m);
        String lseed = seed.toLowerCase();
        if(lseed.startsWith("get") || lseed.startsWith("set"))
            return camelize(seed.substring(3));
        if(lseed.startsWith("is"))
            return camelize(seed.substring(2));
        return seed;
    }


    private static String camelize(String s) {
        return Introspector.decapitalize(s);
    }

    /**
     * Use the enclosing class as the upsream {@link Location}.
     */
    public Locatable getUpstream() {
        return parent;
    }

    public Location getLocation() {
        if(getter!=null)
            return parent.nav().getMethodLocation(getter);
        else
            return parent.nav().getMethodLocation(setter);
    }
}
