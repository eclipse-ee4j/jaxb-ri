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

import org.glassfish.jaxb.core.v2.WellKnownNamespace;
import javax.xml.namespace.QName;

/**
 * {@link TypeInfo} that maps to an element.
 *
 * Either {@link LeafInfo} or {@link ClassInfo}.
 *
 * TODO: better ANYTYPE_NAME.
 *
 * @author Kohsuke Kawaguchi
 */
public interface NonElement<T,C> extends TypeInfo<T,C> {
    public static final QName ANYTYPE_NAME = new QName(WellKnownNamespace.XML_SCHEMA, "anyType");

    /**
     * Gets the primary XML type ANYTYPE_NAME of the class.
     *
     * <p>
     * A Java type can be mapped to multiple XML types, but one of them is
     * considered "primary" and used when we generate a schema.
     *
     * @return
     *      null if the object doesn't have an explicit type ANYTYPE_NAME (AKA anonymous.)
     */
    QName getTypeName();

    /**
     * Returns true if this {@link NonElement} maps to text in XML,
     * without any attribute nor child elements.
     */
    boolean isSimpleType();
}
