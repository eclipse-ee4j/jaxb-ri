/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2;

import jakarta.xml.bind.JAXBException;

final class MUtils {

    /**
     * Api may be defined in a different module, in such case we need to delegate
     * {@linkplain Module#isOpen open} of classes to that module.
     *
     * @param classes used to resolve module for {@linkplain Module#addOpens(String, Module)}
     * @param factorySPI used to resolve {@link Module} of the implementation.
     *
     * @throws JAXBException if ony of a classes package is not open to {@code java.xml.bind} module.
     */
    static void open(Class[] classes) throws JAXBException {
        //noop on jdk < 9
    }

}
