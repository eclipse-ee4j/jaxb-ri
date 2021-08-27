/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.nav;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * Implementation of GenericArrayType interface for core reflection.
 */
final class GenericArrayTypeImpl implements GenericArrayType {
    private Type genericComponentType;

    GenericArrayTypeImpl(Type ct) {
        assert ct!=null;
        genericComponentType = ct;
    }

    /**
     * Returns  a {@code Type} object representing the component type
     * of this array.
     *
     * @return a {@code Type} object representing the component type
     *         of this array
     * @since 1.5
     */
    @Override
    public Type getGenericComponentType() {
        return genericComponentType; // return cached component type
    }

    @Override
    public String toString() {
        Type componentType = getGenericComponentType();
        StringBuilder sb = new StringBuilder();

        if (componentType instanceof Class)
            sb.append(((Class) componentType).getName());
        else
            sb.append(componentType.toString());
        sb.append("[]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GenericArrayType) {
            GenericArrayType that = (GenericArrayType) o;

            Type thatComponentType = that.getGenericComponentType();
            return genericComponentType.equals(thatComponentType);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return genericComponentType.hashCode();
    }
}
