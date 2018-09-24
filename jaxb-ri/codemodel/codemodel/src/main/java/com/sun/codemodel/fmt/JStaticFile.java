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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.codemodel.JResourceFile;

/**
 * Allows an application to copy a resource file to the output. 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class JStaticFile extends JResourceFile {
    
    private final ClassLoader classLoader;
    private final String resourceName;
    private final boolean isResource;

    public JStaticFile(String _resourceName) {
        this(_resourceName,!_resourceName.endsWith(".java"));
    }
    
    public JStaticFile(String _resourceName,boolean isResource) {
        this( SecureLoader.getClassClassLoader(JStaticFile.class), _resourceName, isResource );
    }

    /**
     * @param isResource
     *      false if this is a Java source file. True if this is other resource files.
     */
    public JStaticFile(ClassLoader _classLoader, String _resourceName, boolean isResource) {
        super(_resourceName.substring(_resourceName.lastIndexOf('/')+1));
        this.classLoader = _classLoader;
        this.resourceName = _resourceName;
        this.isResource = isResource;
    }

    protected boolean isResource() {
        return isResource;
    }

    protected void build(OutputStream os) throws IOException {
        DataInputStream dis = new DataInputStream(classLoader.getResourceAsStream(resourceName));
        
        byte[] buf = new byte[256];
        int sz;
        while( (sz=dis.read(buf))>0 )
            os.write(buf,0,sz);
        
        dis.close();
    }
    
}
