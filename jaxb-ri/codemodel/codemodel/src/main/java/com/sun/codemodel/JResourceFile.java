/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a resource file in the application-specific file format.
 */
public abstract class JResourceFile {

    private final String name;
    
    protected JResourceFile( String name ) {
        this.name = name;
    }
    
    /**
     * Gets the name of this property file
     */
    public String name() { 
        return name; 
    }

    /**
     * Returns true if this file should be generated into the directory
     * that the resource files go into.
     *
     * <p>
     * Returns false if this file should be generated into the directory
     * where other source files go.
     */
    protected boolean isResource() {
        return true;
    }

    /**
     * called by JPackage to produce the file image.
     */
    protected abstract void build( OutputStream os ) throws IOException;
}
