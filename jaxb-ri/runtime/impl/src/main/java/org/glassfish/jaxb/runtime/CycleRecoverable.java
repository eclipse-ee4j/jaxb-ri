/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime;

import jakarta.xml.bind.Marshaller;

/**
 * Optional interface that can be implemented by JAXB-bound objects
 * to handle cycles in the object graph.
 *
 * <p>
 * As discussed in <a href="https://javaee.github.io/jaxb-v2/doc/user-guide/ch03.html#annotating-your-classes-mapping-cyclic-references-to-xml">
 * the users' guide</a>, normally a cycle in the object graph causes the marshaller to report an error,
 * and when an error is found, the JAXB RI recovers by cutting the cycle arbitrarily.
 * This is not always a desired behavior.
 *
 * <p>
 * Implementing this interface allows user application to change this behavior.
 *
 * @since JAXB 2.1 EA2
 * @author Kohsuke Kawaguchi
 */
public interface CycleRecoverable {
    /**
     * Called when a cycle is detected by the JAXB RI marshaller
     * to nominate a new object to be marshalled instead.
     *
     * @param context
     *      This object is provided by the JAXB RI to inform
     *      the object about the marshalling process that's going on.
     *
     *
     * @return
     *      the object to be marshalled instead of {@code this} object.
     *      Or return null to indicate that the JAXB RI should behave
     *      just like when your object does not implement
     *      (IOW, cut the cycle arbitrarily and try to go on.)
     */
    Object onCycleDetected(Context context);

    /**
     * This interface is implemented by the JAXB RI to provide
     * information about the on-going marshalling process.
     *
     * <p>
     * We may add more methods in the future, so please do not
     * implement this interface in your application.
     */
    interface Context {
        /**
         * Returns the marshaller object that's doing the marshalling.
         *
         * @return
         *      always non-null.
         */
        Marshaller getMarshaller();
    }
}
