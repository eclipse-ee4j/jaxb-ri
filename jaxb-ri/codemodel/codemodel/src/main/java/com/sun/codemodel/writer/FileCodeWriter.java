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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

/**
 * Writes all the source files under the specified file folder.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class FileCodeWriter extends CodeWriter {

    /** The target directory to put source code. */
    private final File target;
    
    /** specify whether or not to mark the generated files read-only */
    private final boolean readOnly;

    /** Files that shall be marked as read only. */
    private final Set<File> readonlyFiles = new HashSet<>();

    public FileCodeWriter( File target ) throws IOException {
        this(target,false);
    }
    
    public FileCodeWriter( File target, String encoding ) throws IOException {
        this(target,false, encoding);
    }

    public FileCodeWriter( File target, boolean readOnly ) throws IOException {
        this(target, readOnly, null);
    }

    public FileCodeWriter( File target, boolean readOnly, String encoding ) throws IOException {
        this.target = target;
        this.readOnly = readOnly;
        this.encoding = encoding;
        if(!target.exists() || !target.isDirectory())
            throw new IOException(target + ": non-existent directory");
    }
    
    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        return new FileOutputStream(getFile(pkg,fileName));
    }
    
    protected File getFile(JPackage pkg, String fileName ) throws IOException {
        File dir;
        if(pkg == null || pkg.isUnnamed())
            dir = target;
        else
            dir = new File(target, toDirName(pkg));
        
        if(!dir.exists())   dir.mkdirs();
        
        File fn = new File(dir,fileName);
        
        if (fn.exists()) {
            if (!fn.delete())
                throw new IOException(fn + ": Can't delete previous version");
        }
        
        
        if(readOnly)        readonlyFiles.add(fn);
        return fn;
    }

    @Override
    public void close() throws IOException {
        // mark files as read-onnly if necessary
        for (File f : readonlyFiles)
            f.setReadOnly();
    }
    
    /** Converts a package name to the directory name. */
    private static String toDirName( JPackage pkg ) {
        return pkg.name().replace('.',File.separatorChar);
    }

}
