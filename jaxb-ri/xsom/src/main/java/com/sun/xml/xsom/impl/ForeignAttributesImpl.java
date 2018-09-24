/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.ForeignAttributes;
import com.sun.tools.rngdatatype.ValidationContext;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Remembers foreign attributes.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ForeignAttributesImpl extends AttributesImpl implements ForeignAttributes {
    private final ValidationContext context;
    private final Locator locator;
    /**
     * {@link ForeignAttributes} forms a linked list.
     */
    final ForeignAttributesImpl next;

    public ForeignAttributesImpl(ValidationContext context, Locator locator, ForeignAttributesImpl next) {
        this.context = context;
        this.locator = locator;
        this.next = next;
    }

    public ValidationContext getContext() {
        return context;
    }

    public Locator getLocator() {
        return locator;
    }
}
