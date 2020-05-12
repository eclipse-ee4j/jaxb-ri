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

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.bind.JAXBException;

final class MUtils {

    private static final Logger logger = Logger.getLogger("org.glassfish.jaxb.runtime");

    /**
     * Api may be defined in a different module, in such case we need to delegate
     * {@linkplain Module#isOpen open} of classes to that module.
     *
     * @param classes used to resolve module for {@linkplain Module#addOpens(String, Module)}
     * @param factorySPI used to resolve {@link Module} of the implementation.
     *
     * @throws JAXBException if any of a classes package is not open to our module.
     */
    static void open(Class[] classes) throws JAXBException {
        final Module coreModule = org.glassfish.jaxb.core.v2.ClassFactory.class.getModule();
        final Module rtModule = JAXBContextFactory.class.getModule();

        if (rtModule == coreModule) {
            return;
        }

        for (Class cls : classes) {
            Class jaxbClass = cls.isArray() ?
                cls.getComponentType() : cls;

            final Module classModule = jaxbClass.getModule();
            //no need for unnamed and java.base types
            if (!classModule.isNamed() || "java.base".equals(classModule.getName())) {
                continue;
            }
            final String packageName = jaxbClass.getPackageName();

            if (classModule.isOpen(packageName, rtModule)) {
                classModule.addOpens(packageName, coreModule);
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Openning package {0} in {1} to {2}.",
                               new String[]{ packageName, classModule.getName(), coreModule.getName() });
                }
            } else {
                //report error if they are not open to api or our module
                throw new JAXBException(java.text.MessageFormat.format("Package {0} with class {1} defined in a module {2} must be open to at least {3} module.",
                                                        packageName, jaxbClass.getName(), classModule.getName(), rtModule.getName()));
            }
        }
    }

}
