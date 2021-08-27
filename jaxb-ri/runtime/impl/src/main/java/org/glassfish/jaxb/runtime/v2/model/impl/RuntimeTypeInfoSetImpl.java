/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.core.v2.model.annotation.AnnotationReader;
import org.glassfish.jaxb.core.v2.model.core.TypeInfoSet;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeNonElement;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeInfoSet;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * {@link TypeInfoSet} specialized for runtime.
 *
 * @author Kohsuke Kawaguchi
 */
final class RuntimeTypeInfoSetImpl extends TypeInfoSetImpl<Type,Class,Field,Method> implements RuntimeTypeInfoSet {
    public RuntimeTypeInfoSetImpl(AnnotationReader<Type,Class,Field,Method> reader) {
        super(Utils.REFLECTION_NAVIGATOR,reader,RuntimeBuiltinLeafInfoImpl.LEAVES);
    }

    @Override
    protected RuntimeNonElement createAnyType() {
        return RuntimeAnyTypeImpl.theInstance;
    }

    @Override
    public RuntimeNonElement getTypeInfo( Type type ) {
        return (RuntimeNonElement)super.getTypeInfo(type);
    }

    @Override
    public RuntimeNonElement getAnyTypeInfo() {
        return (RuntimeNonElement)super.getAnyTypeInfo();
    }

    @Override
    public RuntimeNonElement getClassInfo(Class clazz) {
        return (RuntimeNonElement)super.getClassInfo(clazz);
    }

    @Override
    public Map<Class,RuntimeClassInfoImpl> beans() {
        return (Map<Class,RuntimeClassInfoImpl>)super.beans();
    }

    @Override
    public Map<Type,RuntimeBuiltinLeafInfoImpl<?>> builtins() {
        return (Map<Type,RuntimeBuiltinLeafInfoImpl<?>>)super.builtins();
    }

    @Override
    public Map<Class,RuntimeEnumLeafInfoImpl<?,?>> enums() {
        return (Map<Class,RuntimeEnumLeafInfoImpl<?,?>>)super.enums();
    }

    @Override
    public Map<Class,RuntimeArrayInfoImpl> arrays() {
        return (Map<Class,RuntimeArrayInfoImpl>)super.arrays();
    }

    @Override
    public RuntimeElementInfoImpl getElementInfo(Class scope,QName name) {
        return (RuntimeElementInfoImpl)super.getElementInfo(scope,name);
    }

    @Override
    public Map<QName,RuntimeElementInfoImpl> getElementMappings(Class scope) {
        return (Map<QName,RuntimeElementInfoImpl>)super.getElementMappings(scope);
    }

    @Override
    public Iterable<RuntimeElementInfoImpl> getAllElements() {
        return (Iterable<RuntimeElementInfoImpl>)super.getAllElements();
    }
}
