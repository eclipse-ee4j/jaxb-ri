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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.sun.codemodel.JResourceFile;


/**
 * Simple text file.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JTextFile extends JResourceFile
{
    public JTextFile( String name ) {
        super(name);
    }
    
    private String contents = null;
    
    public void setContents( String _contents ) {
        this.contents = _contents;
    }
    
    public void build( OutputStream out ) throws IOException {
        Writer w = new OutputStreamWriter(out);
        w.write(contents);
        w.close();
    }
}
