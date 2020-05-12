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

import org.glassfish.jaxb.core.v2.model.core.MapPropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.NonElement;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.core.v2.model.core.TypeInfo;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author Kohsuke Kawaguchi
 */
class MapPropertyInfoImpl<T,C,F,M> extends PropertyInfoImpl<T,C,F,M> implements MapPropertyInfo<T,C> {

    private final QName xmlName;
    private boolean nil;
    private final T keyType;
    private final T valueType;

    // laziy computed to handle cyclic references
    private NonElement<T,C> keyTypeInfo;
    private NonElement<T,C> valueTypeInfo;


    public MapPropertyInfoImpl(ClassInfoImpl<T,C,F,M> ci, PropertySeed<T,C,F,M> seed) {
        super(ci, seed);

        XmlElementWrapper xe = seed.readAnnotation(XmlElementWrapper.class);
        xmlName = calcXmlName(xe);
        nil = xe!=null && xe.nillable();

        T raw = getRawType();
        T bt = nav().getBaseClass(raw, nav().asDecl(Map.class) );
        assert bt!=null;    // Map property is only for Maps

        if(nav().isParameterizedType(bt)) {
            keyType = nav().getTypeArgument(bt,0);
            valueType = nav().getTypeArgument(bt,1);
        } else {
            keyType = valueType = nav().ref(Object.class);
        }
    }

    public Collection<? extends TypeInfo<T,C>> ref() {
        return Arrays.asList(getKeyType(),getValueType());
    }

    public final PropertyKind kind() {
        return PropertyKind.MAP;
    }

    public QName getXmlName() {
        return xmlName;
    }

    public boolean isCollectionNillable() {
        return nil;
    }

    public NonElement<T,C> getKeyType() {
        if(keyTypeInfo==null)
            keyTypeInfo = getTarget(keyType);
        return keyTypeInfo;
    }

    public NonElement<T,C> getValueType() {
        if(valueTypeInfo==null)
            valueTypeInfo = getTarget(valueType);
        return valueTypeInfo;
    }

    public NonElement<T,C> getTarget(T type) {
        assert parent.builder!=null : "this method must be called during the build stage";
        return parent.builder.getTypeInfo(type,this);
    }
}
