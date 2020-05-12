/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
