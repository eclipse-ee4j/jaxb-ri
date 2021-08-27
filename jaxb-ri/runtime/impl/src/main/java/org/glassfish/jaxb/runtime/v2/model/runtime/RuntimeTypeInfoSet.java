/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.runtime;

import org.glassfish.jaxb.core.v2.model.core.TypeInfoSet;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * {@link TypeInfoSet} refined for runtime.
 *
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeTypeInfoSet extends TypeInfoSet<Type,Class,Field,Method>{
    @Override
    Map<Class,? extends RuntimeArrayInfo> arrays();
    @Override
    Map<Class,? extends RuntimeClassInfo> beans();
    @Override
    Map<Type,? extends RuntimeBuiltinLeafInfo> builtins();
    @Override
    Map<Class,? extends RuntimeEnumLeafInfo> enums();
    @Override
    RuntimeNonElement getTypeInfo( Type type );
    @Override
    RuntimeNonElement getAnyTypeInfo();
    @Override
    RuntimeNonElement getClassInfo( Class type );
    @Override
    RuntimeElementInfo getElementInfo( Class scope, QName name );
    @Override
    Map<QName,? extends RuntimeElementInfo> getElementMappings( Class scope );
    @Override
    Iterable<? extends RuntimeElementInfo> getAllElements();
}
