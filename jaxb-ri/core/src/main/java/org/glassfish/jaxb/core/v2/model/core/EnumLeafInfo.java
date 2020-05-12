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
 * {@link NonElement} that represents an {@link Enum} class.
 *
 * @author Kohsuke Kawaguchi
 */
public interface EnumLeafInfo<T,C> extends LeafInfo<T,C> {
    /**
     * The same as {@link #getType()} but an {@link EnumLeafInfo}
     * is guaranteed to represent an enum declaration, which is a
     * kind of a class declaration.
     *
     * @return
     *      always non-null.
     */
    C getClazz();

    /**
     * Returns the base type of the enumeration.
     *
     * <p>
     * For example, with the following enum class, this method
     * returns {@link BuiltinLeafInfo} for {@link Integer}.
     *
     * <pre>
     * &amp;XmlEnum(Integer.class)
     * enum Foo {
     *   &amp;XmlEnumValue("1")
     *   ONE,
     *   &amp;XmlEnumValue("2")
     *   TWO
     * }
     * </pre>
     *
     * @return
     *      never null.
     */
    NonElement<T,C> getBaseType();

    /**
     * Returns the read-only list of enumeration constants.
     *
     * @return
     *      never null. Can be empty (really?).
     */
    Iterable<? extends EnumConstant> getConstants();
}
