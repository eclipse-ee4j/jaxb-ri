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

import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * Individual constant of an enumeration.
 *
 * <p>
 * Javadoc in this class uses the following sample to explain the semantics:
 * <pre>
 * &#64;XmlEnum(Integer.class)
 * enum Foo {
 *   &#64;XmlEnumValue("1")
 *   ONE,
 *   &#64;XmlEnumValue("2")
 *   TWO
 * }
 * </pre>
 *
 * @see EnumLeafInfo
 * @author Kohsuke Kawaguchi
 */
public interface EnumConstant<T,C> {

    /**
     * Gets the {@link EnumLeafInfo} to which this constant belongs to.
     *
     * @return never null.
     */
    EnumLeafInfo<T,C> getEnclosingClass();

    /**
     * Lexical value of this constant.
     *
     * <p>
     * This value should be evaluated against
     * {@link EnumLeafInfo#getBaseType()} to obtain the typed value.
     *
     * <p>
     * This is the same value as written in the {@link XmlEnumValue} annotation.
     * In the above example, this method returns "1" and "2".
     *
     * @return
     *      never null.
     */
    String getLexicalValue();

    /**
     * Gets the constant name.
     *
     * <p>
     * In the above example this method return "ONE" and "TWO".
     *
     * @return
     *      never null. A valid Java identifier.
     */
    String getName();
}
