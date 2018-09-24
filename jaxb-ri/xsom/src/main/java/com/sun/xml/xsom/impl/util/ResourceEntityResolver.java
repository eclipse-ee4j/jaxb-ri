/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class ResourceEntityResolver implements EntityResolver {
    public ResourceEntityResolver( Class _base ) {
        this.base = _base;
    }
    
    private final Class base;
    
    public InputSource resolveEntity( String publicId, String systemId ) {
        return new InputSource(base.getResourceAsStream(systemId));
    }
}
