/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.core;

/**
 * Stand-alone array that can be marshalled/unmarshalled on its own
 * (without being part of any encloding {@link ClassInfo}.)
 *
 * <p>
 * Most of the times arrays are treated as properties of their enclosing classes,
 * but sometimes we do need to map an array class to its own XML type.
 * This object is used for that purpose.
 *
 * @author Kohsuke Kawaguchi
 */
public interface ArrayInfo<T,C> extends NonElement<T,C> {
    /**
     * T of T[]. The type of the items of the array.
     *
     * @return  never null
     */
    NonElement<T,C> getItemType();
}
