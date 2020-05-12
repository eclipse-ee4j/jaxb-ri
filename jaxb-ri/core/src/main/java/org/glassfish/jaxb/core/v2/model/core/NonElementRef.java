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
 * Reference to a {@link NonElement}.
 *
 * This interface defines properties of a reference.
 *
 * @author Kohsuke Kawaguchi
 */
public interface NonElementRef<T,C> {
    /**
     * Target of the reference.
     *
     * @return never null
     */
    NonElement<T,C> getTarget();

    /**
     * Gets the property which is the source of this reference.
     *
     * @return never null
     */
    PropertyInfo<T,C> getSource();
}
