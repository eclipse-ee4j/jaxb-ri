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

import org.w3c.dom.Element;
import org.xml.sax.Locator;

/**
 * Vendor extension customization contributed from {@link Plugin}s.
 *
 * @author Kohsuke Kawaguchi
 */
public class CPluginCustomization {
    /**
     * The annotation found in a schema (or in an external binding file.)
     *
     * Always non-null.
     */
    public final Element element;

    /**
     * The source location where this customization is placed.
     *
     * <p>
     * When an error is found in this customization, this information
     * should be used to point the user to the source of the problem.
     *
     * Always non-nul.
     */
    public final Locator locator;

    private boolean acknowledged;

    /**
     * When a {@link Plugin} "uses" this annotation, call this method
     * to mark it.
     *
     * <p>
     * {@link CPluginCustomization}s that are not marked will be
     * reporeted as an error to users. This allows us to catch
     * customizations that are not used by anybody.
     */
    public void markAsAcknowledged() {
        acknowledged = true;
    }

    public CPluginCustomization(Element element, Locator locator) {
        this.element = element;
        this.locator = locator;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }
}
