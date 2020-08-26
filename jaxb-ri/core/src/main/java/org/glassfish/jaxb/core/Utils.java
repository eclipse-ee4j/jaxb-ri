/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core;

import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public final class Utils {
    private Utils() {}   // no instanciation

    /**
     * Gets the logger for the caller's class.
     *
     * @since 2.0
     */
    public static Logger getClassLogger() {
        try {
            return Logger.getLogger(StackHelper.getCallerClassName());
        } catch( SecurityException e) {
            return Logger.getLogger("org.glassfish.jaxb.core"); // use the default
        }
    }

    /**
     * Reads the system property value and takes care of {@link SecurityException}.
     */
    public static String getSystemProperty(String name) {
        try {
            return System.getProperty(name);
        } catch( SecurityException e ) {
            return null;
        }
    }
}
