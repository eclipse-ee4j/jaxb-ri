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

import org.glassfish.jaxb.core.v2.model.core.AttributePropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.property.AttributeProperty;
import org.glassfish.jaxb.runtime.v2.runtime.property.Property;

import java.lang.reflect.Type;

/**
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeAttributePropertyInfo extends AttributePropertyInfo<Type,Class>, RuntimePropertyInfo, RuntimeNonElementRef {
    // refinement
    @Override
    RuntimeNonElement getTarget();


    @Override
    default Property<?> create(JAXBContextImpl grammar) {
        return new AttributeProperty<>(grammar, this);
    }
}
