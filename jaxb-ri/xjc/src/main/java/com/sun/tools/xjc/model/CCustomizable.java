/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import com.sun.tools.xjc.Plugin;
import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * Implemented by model components that can have customizations contributed by {@link Plugin}s.
 *
 * @author Kohsuke Kawaguchi
 */
public interface CCustomizable {
    /**
     * Gets the list of customizations attached to this model component.
     *
     * @return
     *      can be an empty list but never be null. The returned list is read-only.
     *      Do not modify.
     *
     * @see Plugin#getCustomizationURIs()
     */
    CCustomizations getCustomizations();

    /**
     * Gets the source location in the schema from which this model component is created.
     *
     * @return never null.
     */
    Locator getLocator();

    /**
     * If this model object is built from XML Schema,
     * this property returns a schema component from which the model is built.
     *
     * @return
     *      null if the model is built from sources other than XML Schema
     *      (such as DTD.)
     */
    XSComponent getSchemaComponent();
}
