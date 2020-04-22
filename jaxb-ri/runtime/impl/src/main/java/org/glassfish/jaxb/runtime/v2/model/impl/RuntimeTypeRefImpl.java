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

import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeNonElement;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimePropertyInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeRef;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;

import javax.xml.namespace.QName;
import java.lang.reflect.Type;

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
