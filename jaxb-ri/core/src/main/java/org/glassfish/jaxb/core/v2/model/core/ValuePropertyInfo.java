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
 * Value {@link PropertyInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface ValuePropertyInfo<T,C> extends PropertyInfo<T,C>, NonElementRef<T,C> {
    Adapter<T,C> getAdapter();
}
