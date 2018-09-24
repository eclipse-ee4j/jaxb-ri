/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.runtime;

import java.lang.reflect.Type;

import com.sun.xml.bind.v2.model.core.NonElementRef;
import com.sun.xml.bind.v2.runtime.Transducer;

/**
 * Runtime version of {@link NonElementRef}.
 *
 * <p>
 * Inside the implementation, reference decorators implement this interface
 * and this interface is used for chaining. Also, every {@link RuntimeNonElement}
 * implementation implements this interface so that undecorated plain
 * reference can be represented without using a separate object.
 *
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeNonElementRef extends NonElementRef<Type,Class> {
    // refinements
    RuntimeNonElement getTarget();
    RuntimePropertyInfo getSource();

    /**
     * If the XML representation of the referenced Java type is just a text,
     * return a transducer that converts between the bean and XML.
     */
    Transducer getTransducer();

    // TransducedAccessor should be created
}
