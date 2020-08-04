/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core;

/**
 * Utils for stack trace analysis. Indirection so that a more efficient
 * implementation can be used in later Java versions.
 * 
 * @author Philippe Marschall
 */
final class StackHelper {
    private StackHelper() {}   // no instanciation

    /**
     * Returns the name of the calling class of the second method in the call chain of this method.
     * 
     * @return the name of the caller class
     * @throws SecurityException in case a security manager is installed that
     *                           prevents stack introspection
     */
    static String getCallerClassName() {
       StackTraceElement[] trace = new Exception().getStackTrace();
       return trace[2].getClassName();
    }
}