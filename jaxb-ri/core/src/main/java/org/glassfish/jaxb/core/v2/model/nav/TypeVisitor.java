/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.nav;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * @author Kohsuke Kawaguchi
 */
abstract class TypeVisitor<T,P> {
    public final T visit( Type t, P param ) {
        assert t!=null;

        if (t instanceof Class)
            return onClass((Class)t,param);
        if (t instanceof ParameterizedType)
            return onParameterizdType( (ParameterizedType)t,param);
        if(t instanceof GenericArrayType)
            return onGenericArray((GenericArrayType)t,param);
        if(t instanceof WildcardType)
            return onWildcard((WildcardType)t,param);
        if(t instanceof TypeVariable)
            return onVariable((TypeVariable)t,param);

        // covered all the cases
        assert false;
        throw new IllegalArgumentException();
    }

    protected abstract T onClass(Class c, P param);
    protected abstract T onParameterizdType(ParameterizedType p, P param);
    protected abstract T onGenericArray(GenericArrayType g, P param);
    protected abstract T onVariable(TypeVariable v, P param);
    protected abstract T onWildcard(WildcardType w, P param);
}
