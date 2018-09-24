/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.bind.v2.model.core.TypeInfoSet;

/**
 * {@link TypeInfoSet} refined for runtime.
 *
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeTypeInfoSet extends TypeInfoSet<Type,Class,Field,Method>{
    Map<Class,? extends RuntimeArrayInfo> arrays();
    Map<Class,? extends RuntimeClassInfo> beans();
    Map<Type,? extends RuntimeBuiltinLeafInfo> builtins();
    Map<Class,? extends RuntimeEnumLeafInfo> enums();
    RuntimeNonElement getTypeInfo( Type type );
    RuntimeNonElement getAnyTypeInfo();
    RuntimeNonElement getClassInfo( Class type );
    RuntimeElementInfo getElementInfo( Class scope, QName name );
    Map<QName,? extends RuntimeElementInfo> getElementMappings( Class scope );
    Iterable<? extends RuntimeElementInfo> getAllElements();
}
