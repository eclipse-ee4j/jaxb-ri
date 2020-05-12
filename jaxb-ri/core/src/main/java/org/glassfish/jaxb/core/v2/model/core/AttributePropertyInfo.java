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
 * Attribute {@link PropertyInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface AttributePropertyInfo<T,C> extends PropertyInfo<T,C>, NonElementRef<T,C> {
    /**
     * Gets the type of the attribute.
     *
     * <p>
     * Note that when this property is a collection, this method returns
     * the type of each item in the collection.
     *
     * @return
     *      always non-null.
     */
    NonElement<T,C> getTarget();

    /**
     * Returns true if this attribute is mandatory.
     */
    boolean isRequired();

    /**
     * Gets the attribute name.
     *
     * @return
     *      must be non-null.
     */
    QName getXmlName();

    Adapter<T,C> getAdapter();
}
