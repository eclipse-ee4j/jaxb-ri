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

import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.runtime.Transducer;

/**
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeNonElement extends NonElement<Type,Class>, RuntimeTypeInfo {
    /**
     * This method doesn't take the reference properties defined on
     * {@link RuntimeNonElementRef} into account (such as ID-ness.)
     *
     * @see RuntimeNonElementRef#getTransducer()
     */
    <V> Transducer<V> getTransducer();
}
