/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Loader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallingContext;

import java.lang.reflect.Method;

/**
 * This class is a simple container for caching lifecycle methods that are
 * discovered during construction of (@link JAXBContext}.
 * 
 * @see JaxBeanInfo#lcm
 * @see Loader#fireBeforeUnmarshal(JaxBeanInfo, Object, UnmarshallingContext.State)
 * @see Loader#fireAfterUnmarshal(JaxBeanInfo, Object, UnmarshallingContext.State) 
 * @see XMLSerializer#fireMarshalEvent(Object, Method)
 */
final class LifecycleMethods {
    Method beforeUnmarshal;
    Method afterUnmarshal;
    Method beforeMarshal;
    Method afterMarshal;
}
