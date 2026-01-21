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

import org.glassfish.jaxb.core.v2.model.core.ElementPropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.property.ArrayElementLeafProperty;
import org.glassfish.jaxb.runtime.v2.runtime.property.ArrayElementNodeProperty;
import org.glassfish.jaxb.runtime.v2.runtime.property.ListElementProperty;
import org.glassfish.jaxb.runtime.v2.runtime.property.Property;
import org.glassfish.jaxb.runtime.v2.runtime.property.PropertyFactory;
import org.glassfish.jaxb.runtime.v2.runtime.property.SingleElementLeafProperty;
import org.glassfish.jaxb.runtime.v2.runtime.property.SingleElementNodeProperty;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeElementPropertyInfo extends ElementPropertyInfo<Type,Class>, RuntimePropertyInfo {
    @Override
    Collection<? extends RuntimeTypeInfo> ref();

    @Override
    List<? extends RuntimeTypeRef> getTypes();

    @Override
    default Property<?> create(JAXBContextImpl grammar) {
        if(this.isValueList())
            return new ListElementProperty<>(grammar, this);

        boolean isLeaf = PropertyFactory.isLeaf(this);
        boolean isCollection = this.isCollection();

        if (isLeaf) {
            return isCollection ? new ArrayElementLeafProperty<>(grammar, this) : new SingleElementLeafProperty<>(grammar, this);
        }

        return isCollection ? new ArrayElementNodeProperty<>(grammar, this) : new SingleElementNodeProperty<>(grammar, this);
    }
}
