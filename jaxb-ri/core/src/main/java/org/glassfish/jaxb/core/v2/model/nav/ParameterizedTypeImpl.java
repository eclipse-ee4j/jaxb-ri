/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.nav;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;


/**
 * {@link ParameterizedType} implementation.
 */
class ParameterizedTypeImpl implements ParameterizedType {
    private Type[] actualTypeArguments;
    private Class<?> rawType;
    private Type ownerType;

    ParameterizedTypeImpl(Class<?> rawType,
                                  Type[] actualTypeArguments,
                                  Type ownerType) {
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
        if (ownerType != null) {
            this.ownerType = ownerType;
        } else {
            this.ownerType = rawType.getDeclaringClass();
        }
        validateConstructorArguments();
    }

    private void validateConstructorArguments() {
        TypeVariable<?>[] formals = rawType.getTypeParameters();
        // check correct arity of actual type args
        if (formals.length != actualTypeArguments.length) {
            throw new MalformedParameterizedTypeException();
        }
/*
        for (int i = 0; i < actualTypeArguments.length; i++) {
            // check actuals against formals' bounds
        }
*/
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments.clone();
    }

    @Override
    public Class<?> getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    /*
     * From the JavaDoc for java.lang.reflect.ParameterizedType
     * "Instances of classes that implement this interface must
     * implement an equals() method that equates any two instances
     * that share the same generic type declaration and have equal
     * type parameters."
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ParameterizedType) {
            // Check that information is equivalent
            ParameterizedType that = (ParameterizedType) o;

            if (this == that)
                return true;

            Type thatOwner = that.getOwnerType();
            Type thatRawType = that.getRawType();

/*
            if (false) { // Debugging
                boolean ownerEquality = (ownerType == null ?
                        thatOwner == null :
                        ownerType.equals(thatOwner));
                boolean rawEquality = (rawType == null ?
                        thatRawType == null :
                        rawType.equals(thatRawType));

                boolean typeArgEquality = Arrays.equals(actualTypeArguments, // avoid clone
                        that.getActualTypeArguments());
                for (Type t : actualTypeArguments) {
                    System.out.printf("\t\t%s%s%n", t, t.getClass());
                }

                System.out.printf("\towner %s\traw %s\ttypeArg %s%n",
                        ownerEquality, rawEquality, typeArgEquality);
                return ownerEquality && rawEquality && typeArgEquality;
            }
*/


            return
                    (Objects.equals(ownerType, thatOwner)) &&
                    (Objects.equals(rawType, thatRawType)) &&
                    Arrays.equals(actualTypeArguments, // avoid clone
                            that.getActualTypeArguments());
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return  Arrays.hashCode(actualTypeArguments) ^
                (ownerType == null ? 0 : ownerType.hashCode()) ^
                (rawType == null ? 0 : rawType.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (ownerType != null) {
            if (ownerType instanceof Class)
                sb.append(((Class) ownerType).getName());
            else
                sb.append(ownerType);

            sb.append(".");

            if (ownerType instanceof ParameterizedTypeImpl) {
                // Find simple name of nested type by removing the
                // shared prefix with owner.
                sb.append(rawType.getName().replace(((ParameterizedTypeImpl) ownerType).rawType.getName() + "$",
                        ""));
            } else
                sb.append(rawType.getName());
        } else
            sb.append(rawType.getName());

        if (actualTypeArguments != null &&
                actualTypeArguments.length > 0) {
            sb.append("<");
            boolean first = true;
            for (Type t : actualTypeArguments) {
                if (!first)
                    sb.append(", ");
                if (t instanceof Class)
                    sb.append(((Class) t).getName());
                else
                    sb.append(t.toString());
                first = false;
            }
            sb.append(">");
        }

        return sb.toString();
    }
}
