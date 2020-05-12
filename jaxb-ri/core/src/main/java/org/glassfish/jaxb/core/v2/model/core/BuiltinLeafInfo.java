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
 * JAXB spec designates a few Java classes to be mapped to leaves in XML.
 *
 * <p>
 * Built-in leaves also have another priviledge; specifically, they often
 * have more than one XML type names associated with it.
 *
 * @author Kohsuke Kawaguchi
 */
public interface BuiltinLeafInfo<T,C> extends LeafInfo<T,C> {
    /**
     * {@inheritDoc}
     *
     * <p>
     * This method returns the 'primary' type name of this built-in leaf,
     * which should be used when values of this type are marshalled.
     *
     * @return
     *      never null.
     */
    public QName getTypeName();
}
