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

import java.lang.reflect.Type;

import javax.xml.namespace.QName;

import com.sun.xml.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.bind.v2.runtime.Transducer;

/**
 * @author Kohsuke Kawaguchi
 */
final class RuntimeTypeRefImpl extends TypeRefImpl<Type,Class> implements RuntimeTypeRef {

    public RuntimeTypeRefImpl(RuntimeElementPropertyInfoImpl elementPropertyInfo, QName elementName, Type type, boolean isNillable, String defaultValue) {
        super(elementPropertyInfo, elementName, type, isNillable, defaultValue);
    }

    public RuntimeNonElement getTarget() {
        return (RuntimeNonElement)super.getTarget();
    }

    public Transducer getTransducer() {
        return RuntimeModelBuilder.createTransducer(this);
    }

    public RuntimePropertyInfo getSource() {
        return (RuntimePropertyInfo)owner;
    }
}
