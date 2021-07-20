/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * Information about a type referenced from {@link ElementPropertyInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface TypeRef<T,C> extends NonElementRef<T,C> {
    /**
     * The associated element name.
     *
     * @return
     *      never null.
     */
    QName getTagName();

    /**
     * Returns true if this element is nillable.
     */
    boolean isNillable();

    /**
     * The default value for this element if any.
     * Otherwise null.
     */
    String getDefaultValue();
}
