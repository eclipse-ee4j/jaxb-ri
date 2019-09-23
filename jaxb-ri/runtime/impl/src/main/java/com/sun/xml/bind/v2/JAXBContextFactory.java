/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.Map;

/**
 * Creates JAXB context.
 * Context factory supporting JavaSE service loading facilities.
 *
 * Redirects to {@link ContextFactory}, which is not removed due to compatibility reasons.
 */
public class JAXBContextFactory implements javax.xml.bind.JAXBContextFactory {

    /**
     * Creates JAXB context.
     *
     * @param classesToBeBound JAXB classes accessed by runtime.
     * @param properties JAXB properties passed to runtime.
     * @return JAXB context.
     * @throws JAXBException on any error.
     */
    @Override
    public JAXBContext createContext(Class<?>[] classesToBeBound, Map<String, ?> properties) throws JAXBException {
        return ContextFactory.createContext(classesToBeBound, (Map<String, Object>) properties);
    }

    /**
     * Creates JAXB context.
     * @param contextPath path to scan for JAXB classes accessed by runtime.
     * @param classLoader class loader to use.
     * @param properties JAXB properties.
     * @return JAXB context.
     * @throws JAXBException on any error.
     */
    @Override
    public JAXBContext createContext(String contextPath, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        return ContextFactory.createContext(contextPath, classLoader, (Map<String, Object>) properties);
    }
}
