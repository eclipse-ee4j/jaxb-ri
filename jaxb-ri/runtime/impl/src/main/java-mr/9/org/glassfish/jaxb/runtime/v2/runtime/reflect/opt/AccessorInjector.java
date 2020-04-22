/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect.opt;

/**
 * Stub version of {@link AccessorInjector} for java versions >= 9
 *
 * @author Daniel Kec
 */
class AccessorInjector {

    protected static final boolean noOptimize = true;

    public static Class<?> prepare(
            Class beanClass, String templateClassName, String newClassName, String... replacements) {
        return null;
    }

}
