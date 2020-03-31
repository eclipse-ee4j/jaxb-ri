/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import jakarta.xml.bind.annotation.XmlTransient;

import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * Partial default implementation of {@link CElement}.
 * 
 * @author Kohsuke Kawaguchi
 */
abstract class AbstractCElement extends AbstractCTypeInfoImpl implements CElement {

    /**
     * The location in the source file where this class was declared.
     */
    @XmlTransient
    private final Locator locator;

    private boolean isAbstract;

    protected AbstractCElement(Model model, XSComponent source, Locator locator, CCustomizations customizations) {
        super(model, source, customizations);
        this.locator = locator;
    }

    public Locator getLocator() {
        return locator;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract() {
        isAbstract = true;
    }
}
