/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.runtime;

import org.glassfish.jaxb.core.v2.model.core.ArrayInfo;
import org.glassfish.jaxb.core.v2.model.core.TypeInfo;

import java.lang.reflect.Type;

/**
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeArrayInfo extends ArrayInfo<Type,Class>, RuntimeNonElement {
    /**
     * Represents {@code T[]}.
     *
     * The same as {@link TypeInfo#getType()} but at the runtime, an array
     * is guaranteed to have a {@link Class} representation, not just any {@link Type}.
     */
    Class getType();

    /**
     * {@inheritDoc}
     */
    RuntimeNonElement getItemType();
}
