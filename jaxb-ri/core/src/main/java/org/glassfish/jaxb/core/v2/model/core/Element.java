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

import javax.xml.namespace.QName;

/**
 * {@link TypeInfo} that maps to an element.
 *
 * Either {@link ElementInfo} or {@link ClassInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface Element<T,C> extends TypeInfo<T,C> {
    /**
     * Gets the element name of the class.
     *
     * @return
     *      Always non-null.
     */
    QName getElementName();

    /**
     * If this element can substitute another element, return that element.
     *
     * <p>
     * Substitutability of elements are transitive.
     *
     * @return
     *      null if no such element exists.
     */
    Element<T,C> getSubstitutionHead();

    /**
     * If non-null, this element is only active inside the given scope.
     */
    ClassInfo<T,C> getScope();
}
