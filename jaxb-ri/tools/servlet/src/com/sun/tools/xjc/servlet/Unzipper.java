/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Unzips the contents of a zip file into a specified directory.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Unzipper {
    private final File outDir;

    public Unzipper( File outDir ) {
        this.outDir = outDir;
    }
    
    public void unzip( InputStream input ) throws IOException {
        ZipInputStream zip = new ZipInputStream(input);
        while(true) {
            ZipEntry ze = zip.getNextEntry();
            if(ze==null)    break;
            
            procsesEntry(zip,ze);
            
            zip.closeEntry();
        }
        zip.close();
    }

    private void procsesEntry(ZipInputStream zip, ZipEntry ze) throws IOException {
        String name = ze.getName();
        System.out.println(name);
        
        if( name.indexOf("/impl/")!=-1 || name.indexOf("\\impl\\")!=-1 )
            return; // skip implementation
        
        if( name.endsWith("/") || name.endsWith("\\") ) {
            // directory
            return;
        }
        
        if( !name.endsWith(".java") ) return;
        
        
        File item = new File(outDir,name);
        item.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(item);
        
        Util.copyStream( out, zip );
        out.close();
    }
}
