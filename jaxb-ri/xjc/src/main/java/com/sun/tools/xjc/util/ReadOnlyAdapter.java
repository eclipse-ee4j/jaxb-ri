/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.util;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@link XmlAdapter} used inside XJC is almost always unmarshal-only.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ReadOnlyAdapter<OnTheWire,InMemory> extends XmlAdapter<OnTheWire,InMemory> {
    public final OnTheWire marshal(InMemory onTheWire) {
        // the unmarshaller uses this method
        // to get the current collection object from the property.
        // so we can't just throw UnsupportedOperationException here
        return null;
    }
}
