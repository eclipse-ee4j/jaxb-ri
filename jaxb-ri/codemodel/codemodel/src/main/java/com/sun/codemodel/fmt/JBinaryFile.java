/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.fmt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.codemodel.JResourceFile;

/**
 * Allows the application to use OutputStream to define data
 * that will be stored into a file.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class JBinaryFile extends JResourceFile {
    
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    public JBinaryFile( String name ) {
        super(name);
    }
    
    /**
     * 
     * @return
     *      Data written to the returned output stream will be written
     *      to the file.
     */
    public OutputStream getDataStore() {
        return baos;
    }
    
    public void build(OutputStream os) throws IOException {
        os.write( baos.toByteArray() );
    }
}
