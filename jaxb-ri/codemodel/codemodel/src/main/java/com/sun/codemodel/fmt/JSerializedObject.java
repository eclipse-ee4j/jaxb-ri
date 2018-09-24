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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.sun.codemodel.JResourceFile;


/**
 * A simple class that takes an object and serializes it into a file
 * in the parent package with the given name.
 */
public class JSerializedObject extends JResourceFile {

    private final Object obj;
    
    /**
     * @exception   IOException
     *      If the serialization fails, this exception is thrown
     */
    public JSerializedObject( String name, Object obj ) throws IOException {
        super(name);
        this.obj = obj;
    }
    
    /**
     * called by JPackage to serialize the object 
     */
    protected void build( OutputStream os ) throws IOException {
        // serialize the obj into a ByteArrayOutputStream
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(obj);
        oos.close();
    }
}
