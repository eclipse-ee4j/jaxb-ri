/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime;

import java.lang.reflect.Method;

import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;

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
