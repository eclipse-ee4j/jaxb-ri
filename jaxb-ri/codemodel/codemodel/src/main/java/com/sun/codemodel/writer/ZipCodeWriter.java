/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

/**
 * Writes all the files into a zip file.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ZipCodeWriter extends CodeWriter {
    /**
     * @param target
     *      Zip file will be written to this stream.
     */
    public ZipCodeWriter( OutputStream target ) {
        zip = new ZipOutputStream(target);
        // nullify the close method.
        filter = new FilterOutputStream(zip){
            @Override
            public void close() {}
        };
    }
    
    private final ZipOutputStream zip;
    
    private final OutputStream filter;
        
    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        final String name = pkg == null || pkg.isUnnamed() ? fileName : toDirName(pkg)+fileName;
        zip.putNextEntry(new ZipEntry(name));
        return filter;
    }

    /** Converts a package name to the directory name. */
    private static String toDirName( JPackage pkg ) {
        return pkg.name().replace('.','/')+'/';
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }

}
