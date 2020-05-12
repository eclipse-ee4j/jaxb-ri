/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.runtime;

import org.glassfish.jaxb.core.v2.model.core.LeafInfo;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;

import javax.xml.namespace.QName;
import java.lang.reflect.Type;

/**
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeLeafInfo extends LeafInfo<Type,Class>, RuntimeNonElement {
    /**
     * {@inheritDoc}
     *
     * @return
     *      always non-null.
     */
    <V> Transducer<V> getTransducer();

    /**
     * The same as {@link #getType()} but returns the type as a {@link Class}.
     * <p>
     * Note that the returned {@link Class} object does not necessarily represents
     * a class declaration. It can be primitive types.
     */
    Class getClazz();

    /**
     * Returns all the type names recognized by this type for unmarshalling.
     *
     * <p>
     * While conceptually this method belongs to {@link RuntimeNonElement},
     * if we do that we have to put a lot of dummy implementations everywhere,
     * so it's placed here, where it's actually needed.
     * 
     * @return
     *      Always non-null. Do not modify the returned array.
     */
    QName[] getTypeNames();
}
