/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.util;

import java.util.Map;

/**
 * @author Kohsuke Kawaguchi
 */
public class TypeCast {
    /**
     * Makes sure that a map contains the right type, and returns it to the desirable type.
     */
    public static <K,V> Map<K,V> checkedCast( Map<?,?> m, Class<K> keyType, Class<V> valueType ) {
        if(m==null)
            return null;
        for (Map.Entry e : m.entrySet()) {
            if(!keyType.isInstance(e.getKey()))
                throw new ClassCastException(e.getKey().getClass().toString());
            if(!valueType.isInstance(e.getValue()))
                throw new ClassCastException(e.getValue().getClass().toString());
        }
        return (Map<K,V>)m;
    }
}
