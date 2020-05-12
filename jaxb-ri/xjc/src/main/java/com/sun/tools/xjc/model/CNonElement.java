/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import org.glassfish.jaxb.core.v2.model.core.NonElement;

/**
 * {@link NonElement} at compile-time.
 *
 * <p>
 * This interface implements {@link TypeUse} so that a {@link CNonElement}
 * instance can be used as a {@link TypeUse} instance.
 *
 * @author Kohsuke Kawaguchi
 */
public interface CNonElement extends NonElement<NType,NClass>, TypeUse, CTypeInfo {
    /**
     * Guaranteed to return this.
     */
    @Deprecated
    CNonElement getInfo();

    /**
     * Guaranteed to return false.
     */
    @Deprecated
    boolean isCollection();

    /**
     * Guaranteed to return null.
     */
    @Deprecated
    CAdapter getAdapterUse();
}
