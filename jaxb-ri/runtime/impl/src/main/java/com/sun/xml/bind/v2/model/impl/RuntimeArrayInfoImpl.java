/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.bind.v2.runtime.Transducer;

/**
 * @author Kohsuke Kawaguchi
 */
final class RuntimeArrayInfoImpl extends ArrayInfoImpl<Type,Class,Field,Method> implements RuntimeArrayInfo {
    RuntimeArrayInfoImpl(RuntimeModelBuilder builder, Locatable upstream, Class arrayType) {
        super(builder, upstream, arrayType);
    }

    public Class getType() {
        return (Class)super.getType();
    }

    public RuntimeNonElement getItemType() {
        return (RuntimeNonElement)super.getItemType();
    }

    public <V> Transducer<V> getTransducer() {
        return null;
    }
}
