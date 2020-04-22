/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.runtime.v2.runtime.ElementBeanInfoImpl;
import org.xml.sax.SAXException;

/**
 * Used solely by {@link ElementBeanInfoImpl} to wrap
 * the loaded value object into a JAXBElement object.
 *
 * UGLY HACK.
 *
 * @author Kohsuke Kawaguchi
 */
public interface Intercepter {
    /**
     * Called when the child loader is deactivated.
     *
     * @param state
     *      points to the parent's current state.
     * @param o
     *      object that was loaded. may be null.
     */
    Object intercept(UnmarshallingContext.State state, Object o) throws SAXException;
}
