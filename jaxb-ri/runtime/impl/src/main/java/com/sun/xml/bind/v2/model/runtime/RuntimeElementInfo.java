/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.runtime;

import java.lang.reflect.Type;

import javax.xml.bind.JAXBElement;

import com.sun.xml.bind.v2.model.core.ElementInfo;

/**
 * @author Kohsuke Kawaguchi
 */
public interface RuntimeElementInfo extends ElementInfo<Type,Class>, RuntimeElement {
    RuntimeClassInfo getScope();

    RuntimeElementPropertyInfo getProperty();

    /**
     * Returns the custom {@link JAXBElement} class tailored this element.
     */
    Class<? extends JAXBElement> getType();

    /**
     * {@inheritDoc}
     */
    RuntimeNonElement getContentType();
}
