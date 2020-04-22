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
 * Stub version of {@link Injector} for java versions >= 9
 *
 * @author Daniel Kec
 */
final class Injector {

    /**
     * Injects a new class into the given class loader.
     *
     * @return null
     * if it fails to inject.
     */
    static Class inject(ClassLoader cl, String className, byte[] image) {
        return null;
    }

    /**
     * Returns the already injected class, or null.
     */
    static Class find(ClassLoader cl, String className) {
        return null;
    }
}
