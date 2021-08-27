/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.core.v2.runtime.unmarshaller.LocatorEx;
import jakarta.xml.bind.ValidationEventLocator;
import jakarta.xml.bind.helpers.ValidationEventLocatorImpl;
import org.xml.sax.Locator;

/**
 * {@link LocatorEx} implemented by {@link Locator}.
 * 
 * @author Kohsuke Kawaguchi
 */
class LocatorExWrapper implements LocatorEx {
    private final Locator locator;

    public LocatorExWrapper(Locator locator) {
        this.locator = locator;
    }

    @Override
    public ValidationEventLocator getLocation() {
        return new ValidationEventLocatorImpl(locator);
    }

    @Override
    public String getPublicId() {
        return locator.getPublicId();
    }

    @Override
    public String getSystemId() {
        return locator.getSystemId();
    }

    @Override
    public int getLineNumber() {
        return locator.getLineNumber();
    }

    @Override
    public int getColumnNumber() {
        return locator.getColumnNumber();
    }
}
