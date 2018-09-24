/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api.impl.s2j;

import java.util.List;

import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.Property;
import com.sun.tools.xjc.api.TypeAndAnnotation;
import com.sun.tools.xjc.model.CClassInfo;

/**
 * Partial implementation of {@link Mapping}
 * for bean classes.
 *
 * @author Kohsuke Kawaguchi
 */
final class BeanMappingImpl extends AbstractMappingImpl<CClassInfo> {

    private final TypeAndAnnotationImpl taa = new TypeAndAnnotationImpl(parent.outline,clazz);

    BeanMappingImpl(JAXBModelImpl parent, CClassInfo classInfo) {
        super(parent,classInfo);
        assert classInfo.isElement();
    }

    public TypeAndAnnotation getType() {
        return taa;
    }

    public final String getTypeClass() {
        return getClazz();
    }

    public List<Property> calcDrilldown() {
        if(!clazz.isOrdered())
            return null;    // all is not eligible for the wrapper style
        return buildDrilldown(clazz);
    }
}
